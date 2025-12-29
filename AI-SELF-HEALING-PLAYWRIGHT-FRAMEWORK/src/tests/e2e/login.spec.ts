import { test, expect } from '@playwright/test';
import { LoginPage } from '../../pages/LoginPage.js';
import { DashboardPage } from '../../pages/DashboardPage.js';
import { logger } from '../../utils/Logger.js';

/**
 * Login E2E Tests
 * 
 * Tests the login functionality using the self-healing framework.
 * Uses Page Object Model pattern with smart locator resolution.
 */
test.describe('Login Tests', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/login');
        logger.info('Navigated to login page');
    });

    test('Valid login with correct credentials', async ({ page }, testInfo) => {
        // Arrange
        const loginPage = new LoginPage(page, testInfo);
        const dashboardPage = new DashboardPage(page, testInfo);

        // Act
        await loginPage.login('tomsmith', 'SuperSecretPassword!');

        // Assert
        await dashboardPage.waitForDashboard();
        const welcomeMessage = await dashboardPage.getWelcomeMessage();
        expect(welcomeMessage).toContain('secure area');

        logger.info('Valid login test passed');
    });

    test('Invalid login with wrong password', async ({ page }, testInfo) => {
        // Arrange
        const loginPage = new LoginPage(page, testInfo);

        // Act
        await loginPage.login('tomsmith', 'WrongPassword');

        // Assert
        const errorMessage = await loginPage.getErrorMessage();
        expect(errorMessage).toContain('password is invalid');

        logger.info('Invalid password test passed');
    });

    test('Invalid login with wrong username', async ({ page }, testInfo) => {
        // Arrange
        const loginPage = new LoginPage(page, testInfo);

        // Act
        await loginPage.login('invaliduser', 'SuperSecretPassword!');

        // Assert
        const errorMessage = await loginPage.getErrorMessage();
        expect(errorMessage).toContain('username is invalid');

        logger.info('Invalid username test passed');
    });

    test('Login form is displayed on page load', async ({ page }, testInfo) => {
        // Arrange
        const loginPage = new LoginPage(page, testInfo);

        // Assert
        const isFormDisplayed = await loginPage.isLoginFormDisplayed();
        expect(isFormDisplayed).toBe(true);

        logger.info('Login form display test passed');
    });

    test('Logout after successful login', async ({ page }, testInfo) => {
        // Arrange
        const loginPage = new LoginPage(page, testInfo);
        const dashboardPage = new DashboardPage(page, testInfo);

        // Act - Login
        await loginPage.login('tomsmith', 'SuperSecretPassword!');
        await dashboardPage.waitForDashboard();

        // Act - Logout
        await dashboardPage.logout();

        // Assert - Back on login page
        await loginPage.waitForLoginForm();
        const isLoginFormDisplayed = await loginPage.isLoginFormDisplayed();
        expect(isLoginFormDisplayed).toBe(true);

        logger.info('Logout test passed');
    });

    test('Empty username shows error', async ({ page }, testInfo) => {
        // Arrange
        const loginPage = new LoginPage(page, testInfo);

        // Act
        await loginPage.enterPassword('SomePassword');
        await loginPage.clickLoginButton();

        // Assert
        const errorMessage = await loginPage.getErrorMessage();
        expect(errorMessage).toContain('username is invalid');

        logger.info('Empty username test passed');
    });

    test('Empty password shows error', async ({ page }, testInfo) => {
        // Arrange
        const loginPage = new LoginPage(page, testInfo);

        // Act
        await loginPage.enterUsername('tomsmith');
        await loginPage.clickLoginButton();

        // Assert
        const errorMessage = await loginPage.getErrorMessage();
        expect(errorMessage).toContain('password is invalid');

        logger.info('Empty password test passed');
    });
});
