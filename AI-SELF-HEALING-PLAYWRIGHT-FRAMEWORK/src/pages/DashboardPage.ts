import { Page, TestInfo } from '@playwright/test';
import { BasePage } from './BasePage.js';
import { logger } from '../utils/Logger.js';

/**
 * Dashboard Page Object
 * 
 * Handles dashboard/secure area functionality.
 * Uses self-healing smart actions from BasePage.
 */
export class DashboardPage extends BasePage {
    protected pageName = 'dashboard';

    constructor(page: Page, testInfo: TestInfo) {
        super(page, testInfo);
    }

    /**
     * Get welcome message text
     */
    async getWelcomeMessage(): Promise<string> {
        return await this.step('Get Welcome Message', async () => {
            return await this.smartGetText('welcomeMessage');
        });
    }

    /**
     * Check if welcome message is displayed
     */
    async isWelcomeMessageDisplayed(): Promise<boolean> {
        return await this.smartIsVisible('welcomeMessage');
    }

    /**
     * Click logout button
     */
    async clickLogout(): Promise<void> {
        await this.step('Click Logout button', async () => {
            await this.smartClick('logoutButton');
            logger.info('Clicked logout button');
        });
    }

    /**
     * Complete logout flow
     */
    async logout(): Promise<void> {
        await this.step('Complete Logout Flow', async () => {
            await this.clickLogout();
            await this.waitForPageLoad();
            logger.info('Logout completed');
        });
    }

    /**
     * Get secure area header text
     */
    async getSecureAreaHeader(): Promise<string> {
        return await this.step('Get Secure Area Header', async () => {
            return await this.smartGetText('secureAreaHeader');
        });
    }

    /**
     * Check if user is on secure area
     */
    async isOnSecureArea(): Promise<boolean> {
        try {
            await this.smartWait('secureAreaContent', { timeout: 5000 });
            return true;
        } catch {
            return false;
        }
    }

    /**
     * Wait for dashboard to load
     */
    async waitForDashboard(): Promise<void> {
        await this.step('Wait for Dashboard', async () => {
            await this.smartWait('welcomeMessage');
            logger.info('Dashboard loaded');
        });
    }

    /**
     * Verify successful login
     */
    async verifySuccessfulLogin(): Promise<boolean> {
        const welcomeMessage = await this.getWelcomeMessage();
        return welcomeMessage.toLowerCase().includes('secure area') ||
            welcomeMessage.toLowerCase().includes('logged');
    }
}

export default DashboardPage;
