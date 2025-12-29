import { defineConfig, devices } from '@playwright/test';
import * as dotenv from 'dotenv';
import * as path from 'path';

// Load environment variables
dotenv.config();

/**
 * Root Playwright Configuration
 * This file extends the main config in src/config/
 * @see https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
    testDir: './src/tests/e2e',
    fullyParallel: true,
    forbidOnly: !!process.env.CI,
    retries: process.env.CI ? 2 : 0,
    workers: process.env.CI ? 1 : undefined,

    reporter: [
        ['html', { outputFolder: 'playwright-report' }],
        ['allure-playwright', { outputFolder: 'reports/allure-results' }],
        ['list'],
    ],

    use: {
        baseURL: process.env.BASE_URL || 'https://the-internet.herokuapp.com',
        trace: 'on-first-retry',
        screenshot: 'only-on-failure',
        video: 'retain-on-failure',
        actionTimeout: 10000,
        navigationTimeout: 30000,
    },

    timeout: 60000,

    expect: {
        timeout: 10000,
    },

    projects: [
        {
            name: 'chromium',
            use: { ...devices['Desktop Chrome'] },
        },
        {
            name: 'firefox',
            use: { ...devices['Desktop Firefox'] },
        },
        {
            name: 'webkit',
            use: { ...devices['Desktop Safari'] },
        },
    ],

    outputDir: 'test-results',
});
