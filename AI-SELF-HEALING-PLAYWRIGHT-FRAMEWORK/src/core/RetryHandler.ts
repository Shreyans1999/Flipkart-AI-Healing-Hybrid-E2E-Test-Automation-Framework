import { Page } from '@playwright/test';
import { HealingEngine, HealingResult, HealingContext } from '../ai/HealingEngine.js';
import { FileUtils, LocatorEntry } from '../utils/FileUtils.js';
import { logger } from '../utils/Logger.js';
import { envLoader } from '../utils/EnvLoader.js';

/**
 * Retry configuration
 */
export interface RetryConfig {
    maxRetries: number;
    healOnFailure: boolean;
    throwOnFinalFailure: boolean;
}

/**
 * Action function type
 */
export type ActionFunction<T> = () => Promise<T>;

/**
 * Action factory type - creates action with current selector
 */
export type ActionFactory<T> = (currentSelector: string) => Promise<T>;

/**
 * Retry result
 */
export interface RetryResult<T> {
    success: boolean;
    result?: T;
    attempts: number;
    healingResult?: HealingResult;
    error?: Error;
}

/**
 * Retry Handler with self-healing integration
 * 
 * Implements smart retry logic:
 * - No blind retries
 * - Retry only after healing
 * - Track flaky patterns
 * - Prevent infinite loops
 */
export class RetryHandler {
    private page: Page;
    private healingEngine: HealingEngine;
    private config: RetryConfig;
    private flakyPatterns: Map<string, number> = new Map();

    constructor(page: Page, config?: Partial<RetryConfig>) {
        this.page = page;
        this.healingEngine = new HealingEngine(page);

        const healingConfig = envLoader.getHealingConfig();
        this.config = {
            maxRetries: config?.maxRetries ?? healingConfig.maxRetries,
            healOnFailure: config?.healOnFailure ?? healingConfig.enabled,
            throwOnFinalFailure: config?.throwOnFinalFailure ?? true,
        };
    }

    /**
     * Execute action with retry and self-healing
     * @param actionFactory - Function that takes current selector and returns action promise
     */
    public async executeWithHealing<T>(
        actionFactory: ActionFactory<T>,
        context: {
            pageName: string;
            elementKey: string;
            selector: string;
            testName: string;
            actionType: 'click' | 'fill' | 'visible' | 'text' | 'any';
        }
    ): Promise<RetryResult<T>> {
        let attempts = 0;
        let lastError: Error | undefined;
        let currentSelector = context.selector;
        let healingResult: HealingResult | undefined;

        // Get locator entry for fallbacks
        const locatorEntry = FileUtils.getLocator(context.pageName, context.elementKey);

        while (attempts <= this.config.maxRetries) {
            attempts++;

            try {
                logger.debug(`Attempt ${attempts}/${this.config.maxRetries + 1} for ${context.elementKey} with selector: ${currentSelector}`);
                const result = await actionFactory(currentSelector);

                // Success - clear flaky pattern tracking
                this.clearFlakyPattern(context.elementKey);

                return {
                    success: true,
                    result,
                    attempts,
                    healingResult,
                };

            } catch (error) {
                lastError = error instanceof Error ? error : new Error(String(error));
                logger.debug(`Attempt ${attempts} failed for ${context.elementKey}`, {
                    error: lastError.message,
                });

                // Check if error is healable
                if (!HealingEngine.isHealableError(lastError)) {
                    logger.warn(`Non-healable error encountered: ${lastError.message}`);
                    break;
                }

                // Track flaky pattern
                this.trackFlakyPattern(context.elementKey);

                // Check if we should attempt healing
                if (attempts <= this.config.maxRetries && this.config.healOnFailure) {
                    logger.info(`Initiating healing for ${context.elementKey}`);

                    const healingContext: HealingContext = {
                        page: this.page,
                        pageName: context.pageName,
                        elementKey: context.elementKey,
                        failedSelector: currentSelector,
                        locatorEntry,
                        testName: context.testName,
                        action: context.actionType,
                    };

                    healingResult = await this.healingEngine.heal(healingContext);

                    if (healingResult.success && healingResult.healedSelector) {
                        // Update selector for next attempt
                        currentSelector = healingResult.healedSelector;
                        logger.info(`Healed selector: ${currentSelector}`);

                        // Small delay before retry
                        await this.page.waitForTimeout(500);
                        continue;
                    } else {
                        logger.warn(`Healing failed: ${healingResult.error}`);
                    }
                }
            }
        }

        // All attempts failed
        logger.error(`All ${attempts} attempts failed for ${context.elementKey}`);

        if (this.config.throwOnFinalFailure && lastError) {
            throw lastError;
        }

        return {
            success: false,
            attempts,
            healingResult,
            error: lastError,
        };
    }

    /**
     * Simple retry without healing (for non-locator operations)
     */
    public async retryAction<T>(
        action: ActionFunction<T>,
        maxRetries: number = 3,
        delayMs: number = 1000
    ): Promise<T> {
        let lastError: Error | undefined;

        for (let attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return await action();
            } catch (error) {
                lastError = error instanceof Error ? error : new Error(String(error));
                logger.debug(`Retry attempt ${attempt}/${maxRetries} failed`, {
                    error: lastError.message,
                });

                if (attempt < maxRetries) {
                    await this.page.waitForTimeout(delayMs);
                }
            }
        }

        throw lastError || new Error('All retry attempts failed');
    }

    /**
     * Track flaky patterns for analytics
     */
    private trackFlakyPattern(elementKey: string): void {
        const count = this.flakyPatterns.get(elementKey) || 0;
        this.flakyPatterns.set(elementKey, count + 1);

        // Log warning if element is frequently flaky
        if (count + 1 >= 3) {
            logger.warn(`Element "${elementKey}" has been flaky ${count + 1} times`, {
                elementKey,
                flakyCount: count + 1,
            });
        }
    }

    /**
     * Clear flaky pattern tracking on success
     */
    private clearFlakyPattern(elementKey: string): void {
        this.flakyPatterns.delete(elementKey);
    }

    /**
     * Get flaky pattern statistics
     */
    public getFlakyPatterns(): Map<string, number> {
        return new Map(this.flakyPatterns);
    }

    /**
     * Check if healing should be blocked (infinite loop prevention)
     */
    public isHealingBlocked(elementKey: string): boolean {
        const flakyCount = this.flakyPatterns.get(elementKey) || 0;
        // Block healing if element has been flaky more than 10 times
        return flakyCount > 10;
    }
}

export default RetryHandler;
