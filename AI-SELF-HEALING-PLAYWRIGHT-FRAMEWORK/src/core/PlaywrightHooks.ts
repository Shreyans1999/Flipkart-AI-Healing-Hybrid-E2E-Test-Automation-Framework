import { test, Page, TestInfo } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';
import { logger } from '../utils/Logger.js';
import { envLoader } from '../utils/EnvLoader.js';

/**
 * Playwright Hooks for test lifecycle management
 */
export class PlaywrightHooks {
    /**
     * Before each test hook
     */
    public static beforeEach(page: Page, testInfo: TestInfo): void {
        logger.info(`Starting test: ${testInfo.title}`, {
            file: testInfo.file,
            project: testInfo.project.name,
        });

        // Attach test metadata for Allure
        testInfo.annotations.push({
            type: 'environment',
            description: envLoader.getEnvironment(),
        });
    }

    /**
     * After each test hook
     */
    public static async afterEach(page: Page, testInfo: TestInfo): Promise<void> {
        const status = testInfo.status;
        const duration = testInfo.duration;

        logger.info(`Test completed: ${testInfo.title}`, {
            status,
            duration: `${duration}ms`,
        });

        // Capture screenshot on failure
        if (status === 'failed' || status === 'timedOut') {
            await PlaywrightHooks.captureFailureArtifacts(page, testInfo);
        }
    }

    /**
     * Capture failure artifacts (screenshot, DOM snapshot)
     */
    private static async captureFailureArtifacts(page: Page, testInfo: TestInfo): Promise<void> {
        try {
            // Capture screenshot
            const screenshotPath = testInfo.outputPath('failure-screenshot.png');
            await page.screenshot({ path: screenshotPath, fullPage: true });
            await testInfo.attach('failure-screenshot', {
                path: screenshotPath,
                contentType: 'image/png',
            });
            logger.info(`Screenshot saved: ${screenshotPath}`);

            // Capture HTML snapshot
            const html = await page.content();
            const htmlPath = testInfo.outputPath('failure-dom.html');
            fs.writeFileSync(htmlPath, html);
            await testInfo.attach('failure-dom', {
                path: htmlPath,
                contentType: 'text/html',
            });
            logger.info(`DOM snapshot saved: ${htmlPath}`);

            // Capture console logs
            const logs: string[] = [];
            page.on('console', (msg) => logs.push(`[${msg.type()}] ${msg.text()}`));

            if (logs.length > 0) {
                const logsPath = testInfo.outputPath('console-logs.txt');
                fs.writeFileSync(logsPath, logs.join('\n'));
                await testInfo.attach('console-logs', {
                    path: logsPath,
                    contentType: 'text/plain',
                });
            }

        } catch (error) {
            logger.error('Failed to capture failure artifacts', { error });
        }
    }

    /**
     * Add Allure step
     */
    public static async step<T>(
        testInfo: TestInfo,
        stepName: string,
        action: () => Promise<T>
    ): Promise<T> {
        logger.debug(`Step: ${stepName}`);
        return await test.step(stepName, action);
    }

    /**
     * Add Allure attachment
     */
    public static async addAttachment(
        testInfo: TestInfo,
        name: string,
        content: string | Buffer,
        contentType: string
    ): Promise<void> {
        await testInfo.attach(name, {
            body: content,
            contentType,
        });
    }

    /**
     * Mark test with healing annotation
     */
    public static markHealed(
        testInfo: TestInfo,
        elementKey: string,
        originalSelector: string,
        healedSelector: string
    ): void {
        testInfo.annotations.push({
            type: 'healed',
            description: `${elementKey}: "${originalSelector}" → "${healedSelector}"`,
        });

        logger.info(`Test marked as healed: ${elementKey}`);
    }

    /**
     * Setup global hooks
     */
    public static setupGlobalHooks(): void {
        // Ensure reports directory exists
        const reportsDir = path.join(process.cwd(), 'reports');
        const allureDir = path.join(reportsDir, 'allure-results');
        const htmlDir = path.join(reportsDir, 'html-report');

        [reportsDir, allureDir, htmlDir].forEach(dir => {
            if (!fs.existsSync(dir)) {
                fs.mkdirSync(dir, { recursive: true });
            }
        });

        logger.info('Global hooks configured');
    }
}

export default PlaywrightHooks;
