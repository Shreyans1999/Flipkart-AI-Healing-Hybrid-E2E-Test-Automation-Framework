import { Page, TestInfo } from '@playwright/test';
import { BasePage } from './BasePage.js';
import { logger } from '../utils/Logger.js';

/**
 * Login Page Object
 * 
 * Handles login functionality for the application.
 * Uses self-healing smart actions from BasePage.
 */
export class LoginPage extends BasePage {
    protected pageName = 'login';

    constructor(page: Page, testInfo: TestInfo) {
        super(page, testInfo);
    }

    /**
     * Navigate to login page
     */
    async navigateToLogin(): Promise<void> {
        await this.step('Navigate to Login Page', async () => {
            await this.page.goto('/login');
            await this.waitForPageLoad();
            logger.info('Navigated to login page');
        });
    }

    /**
     * Enter username
     */
    async enterUsername(username: string): Promise<void> {
        await this.step(`Enter username: ${username}`, async () => {
            await this.smartFill('usernameInput', username);
        });
    }

    /**
     * Enter password
     */
    async enterPassword(password: string): Promise<void> {
        await this.step('Enter password', async () => {
            await this.smartFill('passwordInput', password);
        });
    }

    /**
     * Click login button
     */
    async clickLoginButton(): Promise<void> {
        await this.step('Click Login button', async () => {
            await this.smartClick('loginButton');
        });
    }

    /**
     * Complete login flow
     */
    async login(username: string, password: string): Promise<void> {
        await this.step('Complete Login Flow', async () => {
            await this.enterUsername(username);
            await this.enterPassword(password);
            await this.clickLoginButton();
            logger.info(`Login completed for user: ${username}`);
        });
    }

    /**
     * Get error message text
     */
    async getErrorMessage(): Promise<string> {
        return await this.step('Get Error Message', async () => {
            return await this.smartGetText('errorMessage');
        });
    }

    /**
     * Get success message text
     */
    async getSuccessMessage(): Promise<string> {
        return await this.step('Get Success Message', async () => {
            return await this.smartGetText('successMessage');
        });
    }

    /**
     * Check if error message is displayed
     */
    async isErrorDisplayed(): Promise<boolean> {
        return await this.smartIsVisible('errorMessage');
    }

    /**
     * Check if login form is displayed
     */
    async isLoginFormDisplayed(): Promise<boolean> {
        return await this.smartIsVisible('loginForm');
    }

    /**
     * Wait for login form to be visible
     */
    async waitForLoginForm(): Promise<void> {
        await this.step('Wait for Login Form', async () => {
            await this.smartWait('loginForm');
        });
    }
}

export default LoginPage;
