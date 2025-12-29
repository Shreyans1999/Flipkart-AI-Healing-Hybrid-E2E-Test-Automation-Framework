import { Page } from '@playwright/test';
import { logger } from './Logger.js';

/**
 * Element context extracted from DOM
 */
export interface ElementContext {
    tagName: string;
    id?: string;
    classes: string[];
    attributes: Record<string, string>;
    textContent?: string;
    innerText?: string;
    placeholder?: string;
    ariaLabel?: string;
    role?: string;
    name?: string;
    type?: string;
    href?: string;
    parentTag?: string;
    parentClasses?: string[];
    siblingTags?: string[];
    childCount: number;
    position: { x: number; y: number };
}

/**
 * DOM Snapshot for a region around an element
 */
export interface DomSnapshot {
    html: string;
    elementContext: ElementContext | null;
    surroundingElements: ElementContext[];
    pageTitle: string;
    pageUrl: string;
    timestamp: string;
}

/**
 * DOM Snapshot utility for capturing element context
 */
export class DomSnapshotUtils {
    /**
     * Capture full DOM snapshot around a failed selector
     */
    public static async captureSnapshot(
        page: Page,
        failedSelector: string
    ): Promise<DomSnapshot> {
        const timestamp = new Date().toISOString();

        try {
            // Get page info
            const pageTitle = await page.title();
            const pageUrl = page.url();

            // Try to get the element context if it partially exists
            let elementContext: ElementContext | null = null;
            try {
                elementContext = await this.extractElementContext(page, failedSelector);
            } catch {
                // Element not found, which is expected in healing scenario
                logger.debug(`Could not find element with selector: ${failedSelector}`);
            }

            // Get surrounding HTML (limited to reduce token usage)
            const html = await page.evaluate((): string => {
                const body = document.body;
                // Get a cleaned version of the HTML (remove scripts, styles)
                const clone = body.cloneNode(true) as HTMLElement;
                clone.querySelectorAll('script, style, svg, noscript').forEach((el: Element) => el.remove());

                // Limit HTML size
                let htmlContent = clone.innerHTML;
                if (htmlContent.length > 10000) {
                    htmlContent = htmlContent.substring(0, 10000) + '... [truncated]';
                }
                return htmlContent;
            });

            // Get surrounding elements that might be similar
            const surroundingElements = await this.findSimilarElements(page, failedSelector);

            return {
                html,
                elementContext,
                surroundingElements,
                pageTitle,
                pageUrl,
                timestamp,
            };
        } catch (error) {
            logger.error('Error capturing DOM snapshot', { error, failedSelector });
            return {
                html: '',
                elementContext: null,
                surroundingElements: [],
                pageTitle: '',
                pageUrl: page.url(),
                timestamp,
            };
        }
    }

    /**
     * Extract detailed context about an element
     */
    public static async extractElementContext(
        page: Page,
        selector: string
    ): Promise<ElementContext | null> {
        try {
            const context = await page.evaluate((sel: string): ElementContext | null => {
                const element = document.querySelector(sel);
                if (!element) return null;

                const rect = element.getBoundingClientRect();
                const parent = element.parentElement;
                const siblings = parent ? Array.from(parent.children).map((s: Element) => s.tagName.toLowerCase()) : [];

                return {
                    tagName: element.tagName.toLowerCase(),
                    id: element.id || undefined,
                    classes: Array.from(element.classList) as string[],
                    attributes: Object.fromEntries(
                        Array.from(element.attributes).map((attr: Attr) => [attr.name, attr.value])
                    ),
                    textContent: element.textContent?.trim().substring(0, 100) || undefined,
                    innerText: (element as HTMLElement).innerText?.trim().substring(0, 100) || undefined,
                    placeholder: (element as HTMLInputElement).placeholder || undefined,
                    ariaLabel: element.getAttribute('aria-label') || undefined,
                    role: element.getAttribute('role') || undefined,
                    name: (element as HTMLInputElement).name || undefined,
                    type: (element as HTMLInputElement).type || undefined,
                    href: (element as HTMLAnchorElement).href || undefined,
                    parentTag: parent?.tagName.toLowerCase() || undefined,
                    parentClasses: parent ? Array.from(parent.classList) as string[] : [],
                    siblingTags: siblings,
                    childCount: element.children.length,
                    position: { x: Math.round(rect.x), y: Math.round(rect.y) },
                };
            }, selector);

            return context;
        } catch (error) {
            logger.debug(`Could not extract element context for: ${selector}`);
            return null;
        }
    }

    /**
     * Find elements that might be similar to what we're looking for
     */
    public static async findSimilarElements(
        page: Page,
        failedSelector: string
    ): Promise<ElementContext[]> {
        try {
            // Extract tag and key attributes from selector to find similar elements
            const selectorInfo = this.parseSelector(failedSelector);

            const similarElements = await page.evaluate((info: { tagName?: string; id?: string; classes: string[] }): ElementContext[] => {
                const elements: ElementContext[] = [];

                // Find by tag name
                if (info.tagName) {
                    const tagElements = document.querySelectorAll(info.tagName);
                    tagElements.forEach((el: Element, index: number) => {
                        if (index < 5) { // Limit to 5 similar elements
                            const rect = el.getBoundingClientRect();
                            elements.push({
                                tagName: el.tagName.toLowerCase(),
                                id: el.id || undefined,
                                classes: Array.from(el.classList) as string[],
                                attributes: Object.fromEntries(
                                    Array.from(el.attributes)
                                        .filter((a: Attr) => ['id', 'class', 'name', 'type', 'role', 'aria-label', 'data-testid', 'placeholder'].includes(a.name))
                                        .map((attr: Attr) => [attr.name, attr.value])
                                ),
                                textContent: el.textContent?.trim().substring(0, 50) || undefined,
                                innerText: (el as HTMLElement).innerText?.trim().substring(0, 50) || undefined,
                                placeholder: (el as HTMLInputElement).placeholder || undefined,
                                ariaLabel: el.getAttribute('aria-label') || undefined,
                                role: el.getAttribute('role') || undefined,
                                name: (el as HTMLInputElement).name || undefined,
                                type: (el as HTMLInputElement).type || undefined,
                                childCount: el.children.length,
                                position: { x: Math.round(rect.x), y: Math.round(rect.y) },
                            });
                        }
                    });
                }

                return elements;
            }, selectorInfo);

            return similarElements;
        } catch (error) {
            logger.debug('Error finding similar elements');
            return [];
        }
    }

    /**
     * Parse a selector to extract key information
     */
    private static parseSelector(selector: string): { tagName?: string; id?: string; classes: string[] } {
        const result: { tagName?: string; id?: string; classes: string[] } = { classes: [] };

        // Extract tag name
        const tagMatch = selector.match(/^([a-zA-Z]+)/);
        if (tagMatch) {
            result.tagName = tagMatch[1];
        }

        // Extract ID
        const idMatch = selector.match(/#([a-zA-Z0-9_-]+)/);
        if (idMatch) {
            result.id = idMatch[1];
        }

        // Extract classes
        const classMatches = selector.matchAll(/\.([a-zA-Z0-9_-]+)/g);
        for (const match of classMatches) {
            result.classes.push(match[1]);
        }

        // Common elements to search for
        if (!result.tagName) {
            if (selector.includes('button') || selector.includes('btn')) {
                result.tagName = 'button';
            } else if (selector.includes('input')) {
                result.tagName = 'input';
            } else if (selector.includes('link') || selector.includes('href')) {
                result.tagName = 'a';
            }
        }

        return result;
    }

    /**
     * Create a simplified DOM representation for LLM
     */
    public static formatForLLM(snapshot: DomSnapshot): string {
        const parts: string[] = [];

        parts.push(`Page: ${snapshot.pageTitle}`);
        parts.push(`URL: ${snapshot.pageUrl}`);
        parts.push('');

        if (snapshot.elementContext) {
            parts.push('Target Element Context:');
            parts.push(JSON.stringify(snapshot.elementContext, null, 2));
            parts.push('');
        }

        if (snapshot.surroundingElements.length > 0) {
            parts.push('Similar Elements Found:');
            snapshot.surroundingElements.forEach((el, i) => {
                parts.push(`${i + 1}. ${el.tagName}${el.id ? `#${el.id}` : ''}${el.classes.length ? `.${el.classes.join('.')}` : ''}`);
                if (el.textContent) parts.push(`   Text: "${el.textContent}"`);
                if (el.ariaLabel) parts.push(`   Aria-label: "${el.ariaLabel}"`);
            });
            parts.push('');
        }

        // Include relevant portion of HTML
        if (snapshot.html) {
            parts.push('Relevant DOM HTML (truncated):');
            parts.push('```html');
            parts.push(snapshot.html.substring(0, 5000));
            parts.push('```');
        }

        return parts.join('\n');
    }
}

export default DomSnapshotUtils;
