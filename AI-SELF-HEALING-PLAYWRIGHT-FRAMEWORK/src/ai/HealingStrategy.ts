import { Page } from '@playwright/test';
import { logger } from '../utils/Logger.js';

/**
 * Validated selector result
 */
export interface ValidatedSelector {
    selector: string;
    isValid: boolean;
    matchCount: number;
    confidence: number;
    validationDetails: {
        isVisible: boolean;
        isEnabled: boolean;
        tagName?: string;
        hasText?: boolean;
    };
}

/**
 * Healing Strategy - Validates and ranks selectors
 */
export class HealingStrategy {
    private page: Page;

    constructor(page: Page) {
        this.page = page;
    }

    /**
     * Validate multiple selectors and return ranked results
     */
    public async validateSelectors(selectors: string[]): Promise<ValidatedSelector[]> {
        const results: ValidatedSelector[] = [];

        for (const selector of selectors) {
            const result = await this.validateSelector(selector);
            results.push(result);
        }

        // Sort by confidence (highest first)
        results.sort((a, b) => b.confidence - a.confidence);

        logger.debug('Selector validation results', {
            total: selectors.length,
            valid: results.filter(r => r.isValid).length,
        });

        return results;
    }

    /**
     * Validate a single selector
     */
    public async validateSelector(selector: string): Promise<ValidatedSelector> {
        const result: ValidatedSelector = {
            selector,
            isValid: false,
            matchCount: 0,
            confidence: 0,
            validationDetails: {
                isVisible: false,
                isEnabled: false,
            },
        };

        try {
            // Handle different selector types
            const locator = this.getLocator(selector);

            // Count matches
            const count = await locator.count();
            result.matchCount = count;

            if (count === 0) {
                result.isValid = false;
                result.confidence = 0;
                return result;
            }

            // Check if element is visible
            const isVisible = await locator.first().isVisible().catch(() => false);
            result.validationDetails.isVisible = isVisible;

            // Check if element is enabled
            const isEnabled = await locator.first().isEnabled().catch(() => true);
            result.validationDetails.isEnabled = isEnabled;

            // Get tag name
            const tagName = await locator.first().evaluate(el => el.tagName.toLowerCase()).catch(() => undefined);
            result.validationDetails.tagName = tagName;

            // Check if has text content
            const hasText = await locator.first().evaluate(el => !!el.textContent?.trim()).catch(() => false);
            result.validationDetails.hasText = hasText;

            // Calculate confidence score
            result.confidence = this.calculateConfidence(result);
            result.isValid = result.confidence > 0;

            logger.debug(`Selector validation: ${selector}`, {
                isValid: result.isValid,
                confidence: result.confidence,
                matchCount: result.matchCount,
            });

        } catch (error) {
            logger.debug(`Selector validation failed: ${selector}`, { error });
            result.isValid = false;
            result.confidence = 0;
        }

        return result;
    }

    /**
     * Get Playwright locator from selector string
     */
    private getLocator(selector: string) {
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
            const text = selector.substring(5);
            return this.page.getByText(text);
        }

        if (selector.startsWith('label=')) {
            const label = selector.substring(6);
            return this.page.getByLabel(label);
        }

        if (selector.startsWith('placeholder=')) {
            const placeholder = selector.substring(12);
            return this.page.getByPlaceholder(placeholder);
        }

        if (selector.startsWith('testid=') || selector.startsWith('data-testid=')) {
            const testId = selector.replace(/^(testid=|data-testid=)/, '');
            return this.page.getByTestId(testId);
        }

        // CSS or XPath selector
        return this.page.locator(selector);
    }

    /**
     * Calculate confidence score for a validated selector
     */
    private calculateConfidence(result: ValidatedSelector): number {
        let score = 0;

        // Base score for finding element
        if (result.matchCount > 0) {
            score += 0.3;
        }

        // Unique match is highly preferred
        if (result.matchCount === 1) {
            score += 0.3;
        } else if (result.matchCount > 1 && result.matchCount <= 3) {
            score += 0.1; // Multiple matches reduce confidence
        }
        // More than 3 matches = no additional score (too ambiguous)

        // Visibility bonus
        if (result.validationDetails.isVisible) {
            score += 0.2;
        }

        // Enabled bonus
        if (result.validationDetails.isEnabled) {
            score += 0.1;
        }

        // Semantic element bonus
        const semanticTags = ['button', 'input', 'a', 'select', 'textarea'];
        if (result.validationDetails.tagName && semanticTags.includes(result.validationDetails.tagName)) {
            score += 0.1;
        }

        return Math.min(score, 1.0); // Cap at 1.0
    }

    /**
     * Select the best selector from validated results
     */
    public selectBestSelector(results: ValidatedSelector[], minimumConfidence: number = 0.5): ValidatedSelector | null {
        const validResults = results.filter(r => r.isValid && r.confidence >= minimumConfidence);

        if (validResults.length === 0) {
            logger.warn('No valid selectors found with sufficient confidence');
            return null;
        }

        // Return the highest confidence selector
        const best = validResults[0];
        logger.info(`Best selector selected: ${best.selector} (confidence: ${best.confidence.toFixed(2)})`);

        return best;
    }

    /**
     * Test an action with a selector (dry run)
     */
    public async testAction(
        selector: string,
        action: 'click' | 'fill' | 'visible' | 'text' | 'any'
    ): Promise<boolean> {
        try {
            const locator = this.getLocator(selector);

            switch (action) {
                case 'click':
                    // Just verify it's clickable, don't actually click
                    await locator.first().waitFor({ state: 'visible', timeout: 2000 });
                    const isClickable = await locator.first().isEnabled();
                    return isClickable;

                case 'fill':
                    await locator.first().waitFor({ state: 'visible', timeout: 2000 });
                    const tagName = await locator.first().evaluate(el => el.tagName.toLowerCase());
                    return ['input', 'textarea'].includes(tagName);

                case 'visible':
                    return await locator.first().isVisible();

                default:
                    return true;
            }
        } catch {
            return false;
        }
    }
}

export default HealingStrategy;
