import express, { Request, Response, NextFunction } from 'express';
import cors from 'cors';
import { chromium, Browser, Page } from '@playwright/test';
import { LLMClientFactory } from '../ai/LLMClient.js';
import { LocatorAnalyzer } from '../ai/LocatorAnalyzer.js';
import { HealingStrategy } from '../ai/HealingStrategy.js';
import { DomSnapshotUtils } from '../utils/DomSnapshot.js';
import { FileUtils } from '../utils/FileUtils.js';
import { logger, logHealingEvent, HealingLogData } from '../utils/Logger.js';
import { envLoader } from '../utils/EnvLoader.js';
import 'dotenv/config';

/**
 * Healing API Server
 * 
 * Exposes the TypeScript self-healing engine via REST API
 * for integration with Java/Selenium framework
 */

// ==================== Types ====================

interface HealRequest {
    pageName: string;
    elementKey: string;
    failedSelector: string;
    domSnapshot: string;
    action: 'click' | 'fill' | 'visible' | 'text' | 'any';
    fallbacks?: string[];
}

interface HealResponse {
    success: boolean;
    healedSelector?: string;
    confidence: number;
    fallbacksAttempted: string[];
    duration: number;
    error?: string;
}

interface ValidateRequest {
    selector: string;
    domSnapshot: string;
}

interface ValidateResponse {
    valid: boolean;
    confidence: number;
    elementCount: number;
}

// ==================== Server Setup ====================

const app = express();
const PORT = process.env.HEALING_SERVER_PORT || 3001;

// Middleware
app.use(cors());
app.use(express.json({ limit: '10mb' })); // Large DOM snapshots

// Request logging middleware
app.use((req: Request, res: Response, next: NextFunction) => {
    logger.info(`${req.method} ${req.path}`, {
        body: req.method === 'POST' ? { ...req.body, domSnapshot: '[truncated]' } : undefined
    });
    next();
});

// ==================== Browser Management ====================

let browser: Browser | null = null;
let page: Page | null = null;

async function getBrowser(): Promise<Browser> {
    if (!browser) {
        browser = await chromium.launch({ headless: true });
        logger.info('Browser launched for healing validation');
    }
    return browser;
}

async function getPage(): Promise<Page> {
    if (!page || page.isClosed()) {
        const b = await getBrowser();
        page = await b.newPage();
    }
    return page;
}

async function setPageContent(html: string): Promise<Page> {
    const p = await getPage();
    await p.setContent(html, { waitUntil: 'domcontentloaded' });
    return p;
}

// ==================== Endpoints ====================

/**
 * Health check endpoint
 */
app.get('/api/health', (req: Request, res: Response) => {
    const healingConfig = envLoader.getHealingConfig();
    const llmProvider = envLoader.getLLMProvider();

    res.json({
        status: 'ok',
        provider: llmProvider,
        healingEnabled: healingConfig.enabled,
        timestamp: new Date().toISOString()
    });
});

/**
 * Main healing endpoint
 * Accepts failed selector + DOM and returns healed selector
 */
app.post('/api/heal', async (req: Request, res: Response) => {
    const startTime = Date.now();
    const healRequest: HealRequest = req.body;

    // Validate request
    if (!healRequest.pageName || !healRequest.elementKey || !healRequest.failedSelector || !healRequest.domSnapshot) {
        res.status(400).json({
            success: false,
            error: 'Missing required fields: pageName, elementKey, failedSelector, domSnapshot'
        } as HealResponse);
        return;
    }

    logger.info(`🔧 Healing request for: ${healRequest.pageName}.${healRequest.elementKey}`, {
        failedSelector: healRequest.failedSelector,
        action: healRequest.action
    });

    try {
        // Step 1: Set up page with DOM snapshot
        const p = await setPageContent(healRequest.domSnapshot);
        const healingStrategy = new HealingStrategy(p);

        const attemptedSelectors: string[] = [];
        let healedSelector: string | undefined;
        let confidence = 0;

        // Step 2: Try fallback selectors first (if provided)
        if (healRequest.fallbacks && healRequest.fallbacks.length > 0) {
            logger.debug(`Trying ${healRequest.fallbacks.length} fallback selectors`);

            for (const fallback of healRequest.fallbacks) {
                attemptedSelectors.push(fallback);
                const validated = await healingStrategy.validateSelector(fallback);

                if (validated.isValid && validated.confidence >= 0.7) {
                    healedSelector = fallback;
                    confidence = validated.confidence;
                    logger.info(`✅ Fallback selector worked: ${fallback}`);
                    break;
                }
            }
        }

        // Step 3: If no fallback worked, query LLM
        if (!healedSelector) {
            logger.debug('Fallbacks failed, querying LLM...');

            // Build analysis context
            const locatorEntry = healRequest.fallbacks ? {
                primary: healRequest.failedSelector,
                fallbacks: healRequest.fallbacks
            } : null;

            // Create simplified DOM snapshot for LLM
            const simplifiedDom = healRequest.domSnapshot.substring(0, 10000); // Limit size

            // Create a DomSnapshot compatible object for the analyzer
            const domSnapshotForAnalyzer: import('../utils/DomSnapshot.js').DomSnapshot = {
                html: simplifiedDom,
                elementContext: null,
                surroundingElements: [],
                pageTitle: healRequest.pageName,
                pageUrl: 'about:blank',
                timestamp: new Date().toISOString()
            };

            const analysis = LocatorAnalyzer.analyze(
                healRequest.failedSelector,
                domSnapshotForAnalyzer,
                locatorEntry,
                healRequest.elementKey
            );
            const prompt = LocatorAnalyzer.buildPrompt(analysis);

            // Query LLM
            const llmClient = LLMClientFactory.getClient();
            const llmResponse = await llmClient.generateSelectors(prompt);

            if (llmResponse.selectors && llmResponse.selectors.length > 0) {
                logger.info(`LLM returned ${llmResponse.selectors.length} selectors`);
                attemptedSelectors.push(...llmResponse.selectors);

                // Validate each LLM-suggested selector
                for (const selector of llmResponse.selectors) {
                    const validated = await healingStrategy.validateSelector(selector);

                    if (validated.isValid && validated.confidence >= 0.7) {
                        healedSelector = selector;
                        confidence = validated.confidence;
                        logger.info(`✅ LLM selector validated: ${selector}`);
                        break;
                    }
                }
            } else {
                logger.warn('LLM returned no selectors');
            }
        }

        // Step 4: Update locator file if healing succeeded
        if (healedSelector) {
            const healingConfig = envLoader.getHealingConfig();
            if (healingConfig.autoUpdateLocators) {
                FileUtils.updateLocator(
                    healRequest.pageName,
                    healRequest.elementKey,
                    healedSelector,
                    true
                );
            }

            // Log healing event
            const logData: HealingLogData = {
                testName: 'api-request',
                elementKey: healRequest.elementKey,
                originalSelector: healRequest.failedSelector,
                healedSelector,
                fallbacksAttempted: attemptedSelectors,
                confidenceScore: confidence,
                success: true,
                retryCount: attemptedSelectors.length,
                duration: Date.now() - startTime
            };
            logHealingEvent(logData);
        }

        const response: HealResponse = {
            success: !!healedSelector,
            healedSelector,
            confidence,
            fallbacksAttempted: attemptedSelectors,
            duration: Date.now() - startTime,
            error: healedSelector ? undefined : 'No valid selector found'
        };

        logger.info(healedSelector ? '✅ Healing succeeded' : '❌ Healing failed', {
            duration: response.duration,
            healedSelector,
            attemptedCount: attemptedSelectors.length
        });

        res.json(response);

    } catch (error) {
        logger.error('Healing error', { error });

        const response: HealResponse = {
            success: false,
            confidence: 0,
            fallbacksAttempted: [],
            duration: Date.now() - startTime,
            error: error instanceof Error ? error.message : 'Unknown error'
        };

        res.status(500).json(response);
    }
});

/**
 * Validate a selector against DOM
 */
app.post('/api/validate', async (req: Request, res: Response) => {
    const validateRequest: ValidateRequest = req.body;

    if (!validateRequest.selector || !validateRequest.domSnapshot) {
        res.status(400).json({
            valid: false,
            confidence: 0,
            elementCount: 0,
            error: 'Missing required fields: selector, domSnapshot'
        });
        return;
    }

    try {
        const p = await setPageContent(validateRequest.domSnapshot);
        const healingStrategy = new HealingStrategy(p);
        const result = await healingStrategy.validateSelector(validateRequest.selector);

        const response: ValidateResponse = {
            valid: result.isValid,
            confidence: result.confidence,
            elementCount: result.matchCount
        };

        res.json(response);

    } catch (error) {
        res.status(500).json({
            valid: false,
            confidence: 0,
            elementCount: 0,
            error: error instanceof Error ? error.message : 'Unknown error'
        });
    }
});

/**
 * Get locators for a page
 */
app.get('/api/locators/:pageName', (req: Request, res: Response) => {
    const { pageName } = req.params;
    const locators = FileUtils.readLocatorFile(pageName);

    if (locators) {
        res.json(locators);
    } else {
        res.status(404).json({ error: `Locators not found for page: ${pageName}` });
    }
});

// ==================== Server Lifecycle ====================

const server = app.listen(PORT, () => {
    logger.info(`🚀 Healing API Server running on http://localhost:${PORT}`);
    logger.info(`   - Health: GET  /api/health`);
    logger.info(`   - Heal:   POST /api/heal`);
    logger.info(`   - Validate: POST /api/validate`);
    logger.info(`   - Locators: GET  /api/locators/:pageName`);
});

// Graceful shutdown
process.on('SIGINT', async () => {
    logger.info('Shutting down server...');

    if (page) await page.close();
    if (browser) await browser.close();

    server.close(() => {
        logger.info('Server closed');
        process.exit(0);
    });
});

process.on('SIGTERM', async () => {
    if (page) await page.close();
    if (browser) await browser.close();
    server.close();
});

export default app;
