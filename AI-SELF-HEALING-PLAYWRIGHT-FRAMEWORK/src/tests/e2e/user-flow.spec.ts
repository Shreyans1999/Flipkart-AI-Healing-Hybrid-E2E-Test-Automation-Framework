import { test, expect } from '@playwright/test';
import { LoginPage } from '../../pages/LoginPage.js';
import { DashboardPage } from '../../pages/DashboardPage.js';
import { logger } from '../../utils/Logger.js';

/**
 * User Flow E2E Tests
 * 
 * Complete user journey tests that exercise multiple pages
 * and demonstrate the self-healing framework capabilities.
 */
test.describe('User Flow Tests', () => {
    test('Complete user journey: Login → Dashboard → Logout', async ({ page }, testInfo) => {
        // Arrange
        const loginPage = new LoginPage(page, testInfo);
        const dashboardPage = new DashboardPage(page, testInfo);

        // Step 1: Navigate to login
        await test.step('Navigate to Login Page', async () => {
            await loginPage.navigateToLogin();
            const isLoginFormDisplayed = await loginPage.isLoginFormDisplayed();
            expect(isLoginFormDisplayed).toBe(true);
        });

        // Step 2: Perform login
        await test.step('Login with valid credentials', async () => {
            await loginPage.login('tomsmith', 'SuperSecretPassword!');
        });

        // Step 3: Verify dashboard
        await test.step('Verify Dashboard Access', async () => {
            await dashboardPage.waitForDashboard();
            const isOnSecureArea = await dashboardPage.isOnSecureArea();
            expect(isOnSecureArea).toBe(true);
        });

        // Step 4: Check welcome message
        await test.step('Verify Welcome Message', async () => {
            const welcomeMessage = await dashboardPage.getWelcomeMessage();
            expect(welcomeMessage.toLowerCase()).toContain('secure area');
        });

        // Step 5: Logout
        await test.step('Logout', async () => {
            await dashboardPage.logout();
        });

        // Step 6: Verify back on login page
        await test.step('Verify Return to Login Page', async () => {
            await loginPage.waitForLoginForm();
            const isLoginFormDisplayed = await loginPage.isLoginFormDisplayed();
            expect(isLoginFormDisplayed).toBe(true);
        });

        logger.info('Complete user journey test passed');
    });

    test('Failed login recovery flow', async ({ page }, testInfo) => {
        // Arrange
        const loginPage = new LoginPage(page, testInfo);
        const dashboardPage = new DashboardPage(page, testInfo);

        // Step 1: Navigate to login
        await loginPage.navigateToLogin();

        // Step 2: Try invalid login
        await test.step('Attempt Invalid Login', async () => {
            await loginPage.login('wronguser', 'wrongpassword');
            const errorMessage = await loginPage.getErrorMessage();
            expect(errorMessage).toContain('invalid');
        });

        // Step 3: Retry with correct credentials
        await test.step('Retry with Valid Credentials', async () => {
            await loginPage.login('tomsmith', 'SuperSecretPassword!');
        });

        // Step 4: Verify successful login after retry
        await test.step('Verify Successful Login', async () => {
            await dashboardPage.waitForDashboard();
            const welcomeMessage = await dashboardPage.getWelcomeMessage();
            expect(welcomeMessage.toLowerCase()).toContain('secure area');
        });

        logger.info('Failed login recovery flow test passed');
    });

    test('Multiple login/logout cycles', async ({ page }, testInfo) => {
        // Arrange
        const loginPage = new LoginPage(page, testInfo);
        const dashboardPage = new DashboardPage(page, testInfo);
        const cycles = 3;

        await loginPage.navigateToLogin();

        for (let i = 1; i <= cycles; i++) {
            await test.step(`Cycle ${i}: Login`, async () => {
                await loginPage.login('tomsmith', 'SuperSecretPassword!');
                await dashboardPage.waitForDashboard();

                const isOnSecureArea = await dashboardPage.isOnSecureArea();
                expect(isOnSecureArea).toBe(true);
            });

            await test.step(`Cycle ${i}: Logout`, async () => {
                await dashboardPage.logout();
                await loginPage.waitForLoginForm();

                const isLoginFormDisplayed = await loginPage.isLoginFormDisplayed();
                expect(isLoginFormDisplayed).toBe(true);
            });

            logger.info(`Cycle ${i} completed`);
        }

        logger.info(`Multiple login/logout cycles test passed (${cycles} cycles)`);
    });

    test('Session persistence check', async ({ page, context }, testInfo) => {
        // Arrange
        const loginPage = new LoginPage(page, testInfo);
        const dashboardPage = new DashboardPage(page, testInfo);

        // Step 1: Login
        await loginPage.navigateToLogin();
        await loginPage.login('tomsmith', 'SuperSecretPassword!');
        await dashboardPage.waitForDashboard();

        // Step 2: Navigate directly to secure area
        await test.step('Navigate directly to secure area', async () => {
            await page.goto('/secure');

            // Should still be logged in
            const isOnSecureArea = await dashboardPage.isOnSecureArea();
            expect(isOnSecureArea).toBe(true);
        });

        // Step 3: Refresh page and verify we're still on secure area
        await test.step('Refresh page and verify session', async () => {
            await page.reload();
            await page.waitForLoadState('networkidle');

            // Verify we're still on a page (session persists on this demo site)
            const url = page.url();
            expect(url).toContain('secure');
        });

        logger.info('Session persistence test passed');
    });

    test('Handle broken locator scenario (self-healing demo)', async ({ page }, testInfo) => {
        // This test demonstrates how the framework handles locator failures
        // In a real scenario, when a locator breaks, the healing engine will:
        // 1. Try fallback selectors
        // 2. If fallbacks fail, query LLM for alternatives
        // 3. Validate proposed selectors
        // 4. Update locator file with working selector

        const loginPage = new LoginPage(page, testInfo);

        await test.step('Navigate to login with potentially broken locators', async () => {
            // The framework will automatically heal any broken locators
            await loginPage.navigateToLogin();

            // Verify page loaded correctly
            const isLoginFormDisplayed = await loginPage.isLoginFormDisplayed();
            expect(isLoginFormDisplayed).toBe(true);
        });

        await test.step('Interact with elements (healing kicks in if needed)', async () => {
            // These actions will trigger self-healing if selectors are broken
            await loginPage.enterUsername('testuser');
            await loginPage.enterPassword('testpassword');
            await loginPage.clickLoginButton();

            // We expect an error since credentials are invalid
            const errorMessage = await loginPage.getErrorMessage();
            expect(errorMessage).toContain('invalid');
        });

        logger.info('Self-healing demo test passed');
    });
});
