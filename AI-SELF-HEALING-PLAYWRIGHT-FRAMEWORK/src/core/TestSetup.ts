import { test as base, Page, BrowserContext } from '@playwright/test';
import { envLoader } from '../utils/EnvLoader.js';
import { logger } from '../utils/Logger.js';

/**
 * Extended test fixtures with self-healing support
 */
export interface TestFixtures {
    testInfo: {
        testName: string;
        testFile: string;
    };
}

/**
 * Create configured test instance
 */
export const test = base.extend<TestFixtures>({
    testInfo: async ({ }, use, testInfo) => {
        await use({
            testName: testInfo.title,
            testFile: testInfo.file,
        });
    },
});

/**
 * Test setup utilities
 */
export class TestSetup {
    /**
     * Initialize test environment
     */
    public static async initialize(): Promise<void> {
        const env = envLoader.getEnvironment();
        logger.info(`Initializing test environment: ${env}`);

        // Validate required environment variables
        const healingConfig = envLoader.getHealingConfig();
        logger.info('Healing configuration:', healingConfig);
    }

    /**
     * Get base URL for tests
     */
    public static getBaseUrl(): string {
        return envLoader.getBaseUrl();
    }

    /**
     * Get test configuration
     */
    public static getConfig() {
        return envLoader.getConfig();
    }

    /**
     * Setup browser context with common options
     */
    public static async setupContext(context: BrowserContext): Promise<void> {
        // Set default timeout
        const config = envLoader.getConfig();
        context.setDefaultTimeout(config.timeout);

        // Add console message logging
        context.on('page', (page) => {
            page.on('console', (msg) => {
                if (msg.type() === 'error') {
                    logger.debug(`Browser console error: ${msg.text()}`);
                }
            });

            page.on('pageerror', (error) => {
                logger.debug(`Page error: ${error.message}`);
            });
        });

        logger.debug('Browser context configured');
    }

    /**
     * Setup page with common options
     */
    public static async setupPage(page: Page): Promise<void> {
        const config = envLoader.getConfig();

        // Set default navigation timeout
        page.setDefaultNavigationTimeout(config.timeout);
        page.setDefaultTimeout(config.timeout);

        logger.debug('Page configured');
    }

    /**
     * Navigate to base URL
     */
    public static async navigateToBase(page: Page): Promise<void> {
        const baseUrl = TestSetup.getBaseUrl();
        await page.goto(baseUrl);
        logger.info(`Navigated to: ${baseUrl}`);
    }
}

export { expect } from '@playwright/test';
export default TestSetup;
