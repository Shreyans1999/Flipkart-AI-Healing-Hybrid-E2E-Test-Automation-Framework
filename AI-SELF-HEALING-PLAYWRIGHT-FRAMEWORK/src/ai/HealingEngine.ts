import { Page } from '@playwright/test';
import { LLMClientFactory, LLMSelectorResponse } from './LLMClient.js';
import { LocatorAnalyzer } from './LocatorAnalyzer.js';
import { HealingStrategy, ValidatedSelector } from './HealingStrategy.js';
import { DomSnapshotUtils, DomSnapshot } from '../utils/DomSnapshot.js';
import { FileUtils, LocatorEntry } from '../utils/FileUtils.js';
import { logger, healingLogger, logHealingEvent, HealingLogData } from '../utils/Logger.js';
import { envLoader } from '../utils/EnvLoader.js';

/**
 * Healing result
 */
export interface HealingResult {
    success: boolean;
    originalSelector: string;
    healedSelector?: string;
    allAttemptedSelectors: string[];
    confidence: number;
    retryCount: number;
    duration: number;
    error?: string;
}

/**
 * Healing context
 */
export interface HealingContext {
    page: Page;
    pageName: string;
    elementKey: string;
    failedSelector: string;
    locatorEntry: LocatorEntry | null;
    testName: string;
    action: 'click' | 'fill' | 'visible' | 'text' | 'any';
}

/**
 * AI Self-Healing Engine
 * 
 * Orchestrates the healing process:
 * 1. Captures DOM snapshot
 * 2. Analyzes failed selector
 * 3. Queries LLM for alternatives
 * 4. Validates proposed selectors
 * 5. Updates locator files
 * 6. Returns healed selector
 */
export class HealingEngine {
    private page: Page;
    private healingConfig = envLoader.getHealingConfig();
    private healingStrategy: HealingStrategy;

    constructor(page: Page) {
        this.page = page;
        this.healingStrategy = new HealingStrategy(page);
    }

    /**
     * Main healing entry point
     */
    public async heal(context: HealingContext): Promise<HealingResult> {
        const startTime = Date.now();
        const result: HealingResult = {
            success: false,
            originalSelector: context.failedSelector,
            allAttemptedSelectors: [],
            confidence: 0,
            retryCount: 0,
            duration: 0,
        };

        // Check if healing is enabled
        if (!this.healingConfig.enabled) {
            logger.info('Self-healing is disabled');
            result.error = 'Self-healing is disabled';
            result.duration = Date.now() - startTime;
            return result;
        }

        logger.info(`🔧 Starting healing for: ${context.elementKey}`, {
            failedSelector: context.failedSelector,
            page: context.pageName,
        });

        try {
            // Step 1: Try fallback selectors first (faster than LLM)
            const fallbackResult = await this.tryFallbackSelectors(context);
            if (fallbackResult.success) {
                result.success = true;
                result.healedSelector = fallbackResult.healedSelector;
                result.confidence = fallbackResult.confidence;
                result.allAttemptedSelectors = fallbackResult.attemptedSelectors;
                result.duration = Date.now() - startTime;

                this.logHealingResult(context, result);
                return result;
            }
            result.allAttemptedSelectors.push(...fallbackResult.attemptedSelectors);

            // Step 2: Capture DOM snapshot
            const domSnapshot = await DomSnapshotUtils.captureSnapshot(
                this.page,
                context.failedSelector
            );

            // Step 3: Analyze and build LLM prompt
            const analysis = LocatorAnalyzer.analyze(
                context.failedSelector,
                domSnapshot,
                context.locatorEntry,
                context.elementKey
            );
            const prompt = LocatorAnalyzer.buildPrompt(analysis);

            // Step 4: Query LLM for alternative selectors
            const llmResponse = await this.queryLLM(prompt);
            if (!llmResponse.selectors || llmResponse.selectors.length === 0) {
                result.error = 'LLM returned no selectors';
                result.duration = Date.now() - startTime;
                this.logHealingResult(context, result);
                return result;
            }

            logger.info(`LLM returned ${llmResponse.selectors.length} selectors`, {
                selectors: llmResponse.selectors,
                reasoning: llmResponse.reasoning,
            });

            // Step 5: Validate LLM-suggested selectors
            const validatedSelectors = await this.healingStrategy.validateSelectors(
                llmResponse.selectors
            );
            result.allAttemptedSelectors.push(...llmResponse.selectors);

            // Step 6: Select best working selector
            const bestSelector = this.healingStrategy.selectBestSelector(
                validatedSelectors,
                this.healingConfig.confidenceThreshold
            );

            if (!bestSelector) {
                result.error = 'No valid selector found above confidence threshold';
                result.duration = Date.now() - startTime;
                this.logHealingResult(context, result);
                return result;
            }

            // Step 7: Test the selector with the intended action
            const actionValid = await this.healingStrategy.testAction(
                bestSelector.selector,
                context.action
            );

            if (!actionValid) {
                result.error = `Selector found but not suitable for action: ${context.action}`;
                result.duration = Date.now() - startTime;
                this.logHealingResult(context, result);
                return result;
            }

            // Step 8: Update locator file (if enabled)
            if (this.healingConfig.autoUpdateLocators) {
                await this.updateLocatorFile(
                    context.pageName,
                    context.elementKey,
                    bestSelector.selector
                );
            }

            // Success!
            result.success = true;
            result.healedSelector = bestSelector.selector;
            result.confidence = bestSelector.confidence;
            result.duration = Date.now() - startTime;

            logger.info(`✅ Healing successful: ${context.elementKey}`, {
                original: context.failedSelector,
                healed: bestSelector.selector,
                confidence: bestSelector.confidence,
            });

            this.logHealingResult(context, result);
            return result;

        } catch (error) {
            result.error = error instanceof Error ? error.message : 'Unknown error';
            result.duration = Date.now() - startTime;
            logger.error('Healing failed with error', { error, context });
            this.logHealingResult(context, result);
            return result;
        }
    }

    /**
     * Try fallback selectors before querying LLM
     */
    private async tryFallbackSelectors(context: HealingContext): Promise<{
        success: boolean;
        healedSelector?: string;
        confidence: number;
        attemptedSelectors: string[];
    }> {
        const attemptedSelectors: string[] = [];

        if (!context.locatorEntry || !context.locatorEntry.fallbacks) {
            return { success: false, confidence: 0, attemptedSelectors };
        }

        logger.debug(`Trying ${context.locatorEntry.fallbacks.length} fallback selectors`);

        for (const fallback of context.locatorEntry.fallbacks) {
            attemptedSelectors.push(fallback);

            const validated = await this.healingStrategy.validateSelector(fallback);

            if (validated.isValid && validated.confidence >= this.healingConfig.confidenceThreshold) {
                logger.info(`✅ Fallback selector worked: ${fallback}`);

                // Update primary selector in file
                if (this.healingConfig.autoUpdateLocators) {
                    await this.updateLocatorFile(
                        context.pageName,
                        context.elementKey,
                        fallback
                    );
                }

                return {
                    success: true,
                    healedSelector: fallback,
                    confidence: validated.confidence,
                    attemptedSelectors,
                };
            }
        }

        logger.debug('No fallback selectors worked, proceeding to LLM');
        return { success: false, confidence: 0, attemptedSelectors };
    }

    /**
     * Query LLM for alternative selectors
     */
    private async queryLLM(prompt: string): Promise<LLMSelectorResponse> {
        try {
            const client = LLMClientFactory.getClient();
            return await client.generateSelectors(prompt);
        } catch (error) {
            logger.error('LLM query failed', { error });
            return {
                selectors: [],
                reasoning: 'LLM query failed',
                confidence: 0,
            };
        }
    }

    /**
     * Update locator file with healed selector
     */
    private async updateLocatorFile(
        pageName: string,
        elementKey: string,
        newSelector: string
    ): Promise<void> {
        try {
            const success = FileUtils.updateLocator(pageName, elementKey, newSelector, true);
            if (success) {
                logger.info(`Locator file updated: ${pageName}.${elementKey} -> ${newSelector}`);
            } else {
                logger.warn(`Failed to update locator file for ${pageName}.${elementKey}`);
            }
        } catch (error) {
            logger.error('Error updating locator file', { error, pageName, elementKey });
        }
    }

    /**
     * Log healing result for analytics
     */
    private logHealingResult(context: HealingContext, result: HealingResult): void {
        const logData: HealingLogData = {
            testName: context.testName,
            elementKey: context.elementKey,
            originalSelector: result.originalSelector,
            healedSelector: result.healedSelector,
            fallbacksAttempted: result.allAttemptedSelectors,
            confidenceScore: result.confidence,
            success: result.success,
            retryCount: result.retryCount,
            duration: result.duration,
            error: result.error,
        };

        logHealingEvent(logData);
    }

    /**
     * Check if an error is healable
     */
    public static isHealableError(error: Error): boolean {
        const healablePatterns = [
            'Element not found',
            'Timeout',
            'waiting for',
            'strict mode violation',
            'locator resolved to',
            'no element matching',
            'Target closed',
        ];

        const errorMessage = error.message.toLowerCase();
        return healablePatterns.some(pattern =>
            errorMessage.includes(pattern.toLowerCase())
        );
    }
}

export default HealingEngine;
