import { Page, Locator, TestInfo } from '@playwright/test';
import { RetryHandler } from '../core/RetryHandler.js';
import { FileUtils, LocatorEntry, LocatorFile } from '../utils/FileUtils.js';
import { logger } from '../utils/Logger.js';
import { PlaywrightHooks } from '../core/PlaywrightHooks.js';

/**
 * Base Page Object with self-healing smart actions
 * 
 * All page objects should extend this class to inherit:
 * - Smart locator resolution with fallbacks
 * - Self-healing on locator failures
 * - Automatic retry with healing
 * - Structured logging
 */
export abstract class BasePage {
    protected page: Page;
    protected testInfo: TestInfo;
    protected abstract pageName: string;
    protected retryHandler: RetryHandler;
    protected locators: LocatorFile | null = null;

    constructor(page: Page, testInfo: TestInfo) {
        this.page = page;
        this.testInfo = testInfo;
        this.retryHandler = new RetryHandler(page);
    }

    /**
     * Load locators for this page
     */
    protected loadLocators(): LocatorFile {
        if (!this.locators) {
            this.locators = FileUtils.readLocatorFile(this.pageName) || {};
        }
        return this.locators;
    }

    /**
     * Get locator entry by element key
     */
    protected getLocatorEntry(elementKey: string): LocatorEntry | null {
        const locators = this.loadLocators();
        return locators[elementKey] || null;
    }

    /**
     * Get primary selector for an element
     */
    protected getSelector(elementKey: string): string {
        const entry = this.getLocatorEntry(elementKey);
        if (!entry) {
            throw new Error(`Locator not found: ${this.pageName}.${elementKey}`);
        }
        return entry.primary;
    }

    /**
     * Get Playwright locator for a selector
     */
    protected getLocator(selector: string): Locator {
        // Handle Playwright-specific selector syntax
        if (selector.startsWith('role=')) {
            const roleMatch = selector.match(/role=([a-z]+)(?:\[name=['"]?([^'"]+)['"]?\])?/i);
            if (roleMatch) {
                const role = roleMatch[1] as 'button' | 'link' | 'textbox' | 'heading';
                const name = roleMatch[2];
                return this.page.getByRole(role, name ? { name } : undefined);
            }
        }

        if (selector.startsWith('text=')) {
            return this.page.getByText(selector.substring(5));
        }

        if (selector.startsWith('label=')) {
            return this.page.getByLabel(selector.substring(6));
        }

        if (selector.startsWith('placeholder=')) {
            return this.page.getByPlaceholder(selector.substring(12));
        }

        if (selector.startsWith('testid=') || selector.startsWith('data-testid=')) {
            const testId = selector.replace(/^(testid=|data-testid=)/, '');
            return this.page.getByTestId(testId);
        }

        return this.page.locator(selector);
    }

    /**
     * Smart click with self-healing
     */
    protected async smartClick(elementKey: string, options?: { timeout?: number }): Promise<void> {
        const selector = this.getSelector(elementKey);

        await this.retryHandler.executeWithHealing(
            async (currentSelector: string) => {
                const locator = this.getLocator(currentSelector);
                await locator.click({ timeout: options?.timeout || 10000 });
                logger.debug(`Clicked: ${elementKey}`);
            },
            {
                pageName: this.pageName,
                elementKey,
                selector,
                testName: this.testInfo.title,
                actionType: 'click',
            }
        );
    }

    /**
     * Smart fill with self-healing
     */
    protected async smartFill(elementKey: string, value: string, options?: { timeout?: number }): Promise<void> {
        const selector = this.getSelector(elementKey);

        await this.retryHandler.executeWithHealing(
            async (currentSelector: string) => {
                const locator = this.getLocator(currentSelector);
                await locator.fill(value, { timeout: options?.timeout || 10000 });
                logger.debug(`Filled: ${elementKey} with value`);
            },
            {
                pageName: this.pageName,
                elementKey,
                selector,
                testName: this.testInfo.title,
                actionType: 'fill',
            }
        );
    }

    /**
     * Smart type with self-healing (keystroke by keystroke)
     */
    protected async smartType(elementKey: string, value: string, options?: { delay?: number }): Promise<void> {
        const selector = this.getSelector(elementKey);

        await this.retryHandler.executeWithHealing(
            async (currentSelector: string) => {
                const locator = this.getLocator(currentSelector);
                await locator.pressSequentially(value, { delay: options?.delay || 50 });
                logger.debug(`Typed: ${elementKey}`);
            },
            {
                pageName: this.pageName,
                elementKey,
                selector,
                testName: this.testInfo.title,
                actionType: 'fill',
            }
        );
    }

    /**
     * Smart wait for element to be visible
     */
    protected async smartWait(elementKey: string, options?: { timeout?: number; state?: 'visible' | 'hidden' }): Promise<void> {
        const selector = this.getSelector(elementKey);

        await this.retryHandler.executeWithHealing(
            async (currentSelector: string) => {
                const locator = this.getLocator(currentSelector);
                await locator.waitFor({
                    state: options?.state || 'visible',
                    timeout: options?.timeout || 10000
                });
                logger.debug(`Waited for: ${elementKey} (${options?.state || 'visible'})`);
            },
            {
                pageName: this.pageName,
                elementKey,
                selector,
                testName: this.testInfo.title,
                actionType: 'visible',
            }
        );
    }

    /**
     * Smart get text with self-healing
     */
    protected async smartGetText(elementKey: string): Promise<string> {
        const selector = this.getSelector(elementKey);

        const result = await this.retryHandler.executeWithHealing(
            async (currentSelector: string) => {
                const locator = this.getLocator(currentSelector);
                const text = await locator.textContent();
                logger.debug(`Got text from: ${elementKey}`);
                return text || '';
            },
            {
                pageName: this.pageName,
                elementKey,
                selector,
                testName: this.testInfo.title,
                actionType: 'text',
            }
        );

        return result.result || '';
    }

    /**
     * Smart get inner text with self-healing
     */
    protected async smartGetInnerText(elementKey: string): Promise<string> {
        const selector = this.getSelector(elementKey);

        const result = await this.retryHandler.executeWithHealing(
            async (currentSelector: string) => {
                const locator = this.getLocator(currentSelector);
                const text = await locator.innerText();
                logger.debug(`Got inner text from: ${elementKey}`);
                return text;
            },
            {
                pageName: this.pageName,
                elementKey,
                selector,
                testName: this.testInfo.title,
                actionType: 'text',
            }
        );

        return result.result || '';
    }

    /**
     * Smart check if element is visible
     */
    protected async smartIsVisible(elementKey: string): Promise<boolean> {
        const selector = this.getSelector(elementKey);

        try {
            const locator = this.getLocator(selector);
            return await locator.isVisible();
        } catch {
            return false;
        }
    }

    /**
     * Smart select option from dropdown
     */
    protected async smartSelect(elementKey: string, value: string | { label?: string; value?: string }): Promise<void> {
        const selector = this.getSelector(elementKey);

        await this.retryHandler.executeWithHealing(
            async (currentSelector: string) => {
                const locator = this.getLocator(currentSelector);
                if (typeof value === 'string') {
                    await locator.selectOption(value);
                } else {
                    await locator.selectOption(value);
                }
                logger.debug(`Selected: ${elementKey}`);
            },
            {
                pageName: this.pageName,
                elementKey,
                selector,
                testName: this.testInfo.title,
                actionType: 'click',
            }
        );
    }

    /**
     * Smart check checkbox
     */
    protected async smartCheck(elementKey: string): Promise<void> {
        const selector = this.getSelector(elementKey);

        await this.retryHandler.executeWithHealing(
            async (currentSelector: string) => {
                const locator = this.getLocator(currentSelector);
                await locator.check();
                logger.debug(`Checked: ${elementKey}`);
            },
            {
                pageName: this.pageName,
                elementKey,
                selector,
                testName: this.testInfo.title,
                actionType: 'click',
            }
        );
    }

    /**
     * Smart uncheck checkbox
     */
    protected async smartUncheck(elementKey: string): Promise<void> {
        const selector = this.getSelector(elementKey);

        await this.retryHandler.executeWithHealing(
            async (currentSelector: string) => {
                const locator = this.getLocator(currentSelector);
                await locator.uncheck();
                logger.debug(`Unchecked: ${elementKey}`);
            },
            {
                pageName: this.pageName,
                elementKey,
                selector,
                testName: this.testInfo.title,
                actionType: 'click',
            }
        );
    }

    /**
     * Navigate to a URL
     */
    protected async navigate(path: string): Promise<void> {
        const url = path.startsWith('http') ? path : `${this.page.context().browser()?.version}${path}`;
        await this.page.goto(path);
        logger.info(`Navigated to: ${path}`);
    }

    /**
     * Wait for page to load
     */
    protected async waitForPageLoad(): Promise<void> {
        await this.page.waitForLoadState('networkidle');
        logger.debug('Page loaded');
    }

    /**
     * Take screenshot
     */
    protected async takeScreenshot(name: string): Promise<void> {
        const path = this.testInfo.outputPath(`${name}.png`);
        await this.page.screenshot({ path, fullPage: true });
        await this.testInfo.attach(name, { path, contentType: 'image/png' });
        logger.debug(`Screenshot saved: ${name}`);
    }

    /**
     * Add step to Allure report
     */
    protected async step<T>(name: string, action: () => Promise<T>): Promise<T> {
        return await PlaywrightHooks.step(this.testInfo, name, action);
    }
}

export default BasePage;
