import { DomSnapshot, ElementContext } from '../utils/DomSnapshot.js';
import { LocatorEntry } from '../utils/FileUtils.js';
import { logger } from '../utils/Logger.js';

/**
 * Analysis result for building LLM prompt
 */
export interface LocatorAnalysis {
    failedSelector: string;
    elementKey: string;
    elementType: string;
    expectedAttributes: Record<string, string>;
    expectedText?: string;
    domContext: string;
    previouslyWorkingSelectors: string[];
}

/**
 * Locator Analyzer - Analyzes failed selectors and DOM to build LLM prompts
 */
export class LocatorAnalyzer {
    /**
     * Analyze a failed locator and generate context for LLM
     */
    public static analyze(
        failedSelector: string,
        domSnapshot: DomSnapshot,
        locatorEntry: LocatorEntry | null,
        elementKey?: string
    ): LocatorAnalysis {
        // Use elementKey to infer type first (more reliable), then fall back to selector
        const elementType = this.inferElementType(elementKey || '', failedSelector);
        const expectedAttributes = this.extractExpectedAttributes(failedSelector);
        const expectedText = this.extractExpectedText(failedSelector);

        const previouslyWorkingSelectors: string[] = [];
        if (locatorEntry) {
            previouslyWorkingSelectors.push(locatorEntry.primary);
            previouslyWorkingSelectors.push(...locatorEntry.fallbacks);
        }

        const analysis: LocatorAnalysis = {
            failedSelector,
            elementKey: elementKey || '',
            elementType,
            expectedAttributes,
            expectedText,
            domContext: this.formatDomContext(domSnapshot),
            previouslyWorkingSelectors,
        };

        logger.debug('Locator analysis complete', { analysis });
        return analysis;
    }

    /**
     * Infer element type from elementKey and selector
     * elementKey (e.g., 'loginButton') takes priority over selector
     */
    private static inferElementType(elementKey: string, selector: string): string {
        // Check elementKey first (more reliable since it's semantic)
        const keyLower = elementKey.toLowerCase();
        const selectorLower = selector.toLowerCase();
        const combined = keyLower + ' ' + selectorLower;

        if (combined.includes('button') || combined.includes('btn') || combined.includes('submit')) {
            return 'button (clickable submit element)';
        }
        if (combined.includes('input') || combined.includes('field') || combined.includes('username') || combined.includes('password') || combined.includes('email')) {
            return 'input';
        }
        if (combined.includes('link') || combined.includes('href') || selector.startsWith('a')) {
            return 'link';
        }
        if (combined.includes('select') || combined.includes('dropdown')) {
            return 'select';
        }
        if (combined.includes('checkbox')) {
            return 'checkbox';
        }
        if (combined.includes('radio')) {
            return 'radio';
        }
        if (combined.includes('message') || combined.includes('error') || combined.includes('success') || combined.includes('flash')) {
            return 'message/notification element';
        }
        if (combined.includes('text') || combined.includes('label') || combined.includes('heading') || combined.includes('title')) {
            return 'text';
        }
        if (combined.includes('form')) {
            return 'form';
        }

        // Extract tag from selector
        const tagMatch = selector.match(/^([a-zA-Z]+)/);
        return tagMatch ? tagMatch[1] : 'unknown';
    }

    /**
     * Extract expected attributes from selector
     */
    private static extractExpectedAttributes(selector: string): Record<string, string> {
        const attributes: Record<string, string> = {};

        // Extract ID
        const idMatch = selector.match(/#([a-zA-Z0-9_-]+)/);
        if (idMatch) {
            attributes['id'] = idMatch[1];
        }

        // Extract classes
        const classMatches = [...selector.matchAll(/\.([a-zA-Z0-9_-]+)/g)];
        if (classMatches.length > 0) {
            attributes['class'] = classMatches.map(m => m[1]).join(' ');
        }

        // Extract attribute selectors
        const attrMatches = [...selector.matchAll(/\[([a-zA-Z0-9_-]+)=['"]?([^'"\]]+)['"]?\]/g)];
        for (const match of attrMatches) {
            attributes[match[1]] = match[2];
        }

        // Extract data-testid specifically
        const testIdMatch = selector.match(/data-testid=['"]?([^'"\]]+)['"]?/);
        if (testIdMatch) {
            attributes['data-testid'] = testIdMatch[1];
        }

        return attributes;
    }

    /**
     * Extract expected text from selector
     */
    private static extractExpectedText(selector: string): string | undefined {
        // Playwright text selector
        const textMatch = selector.match(/text=['"]?([^'"]+)['"]?/i);
        if (textMatch) {
            return textMatch[1];
        }

        // has-text pseudo selector
        const hasTextMatch = selector.match(/:has-text\(['"]?([^'"]+)['"]?\)/i);
        if (hasTextMatch) {
            return hasTextMatch[1];
        }

        // XPath text contains
        const xpathTextMatch = selector.match(/contains\(text\(\),\s*['"]([^'"]+)['"]\)/i);
        if (xpathTextMatch) {
            return xpathTextMatch[1];
        }

        return undefined;
    }

    /**
     * Format DOM context for LLM consumption
     */
    private static formatDomContext(snapshot: DomSnapshot): string {
        const parts: string[] = [];

        parts.push(`Page Title: ${snapshot.pageTitle}`);
        parts.push(`Page URL: ${snapshot.pageUrl}`);
        parts.push('');

        if (snapshot.elementContext) {
            parts.push('Last Known Element Details:');
            parts.push(this.formatElementContext(snapshot.elementContext));
            parts.push('');
        }

        if (snapshot.surroundingElements.length > 0) {
            parts.push('Similar Elements on Page:');
            snapshot.surroundingElements.forEach((el, i) => {
                parts.push(`  ${i + 1}. ${this.formatElementContext(el)}`);
            });
            parts.push('');
        }

        // Add relevant HTML excerpt
        if (snapshot.html) {
            parts.push('Relevant DOM Structure:');
            parts.push('```html');
            // Truncate to reasonable size for LLM
            const truncatedHtml = snapshot.html.substring(0, 4000);
            parts.push(truncatedHtml);
            if (snapshot.html.length > 4000) {
                parts.push('... [truncated]');
            }
            parts.push('```');
        }

        return parts.join('\n');
    }

    /**
     * Format single element context
     */
    private static formatElementContext(el: ElementContext): string {
        const parts: string[] = [];

        parts.push(`<${el.tagName}`);
        if (el.id) parts.push(` id="${el.id}"`);
        if (el.classes.length > 0) parts.push(` class="${el.classes.join(' ')}"`);
        if (el.name) parts.push(` name="${el.name}"`);
        if (el.type) parts.push(` type="${el.type}"`);
        if (el.role) parts.push(` role="${el.role}"`);
        if (el.ariaLabel) parts.push(` aria-label="${el.ariaLabel}"`);
        if (el.placeholder) parts.push(` placeholder="${el.placeholder}"`);
        parts.push('>');

        if (el.textContent) {
            parts.push(` Text: "${el.textContent}"`);
        }

        return parts.join('');
    }

    /**
     * Build complete LLM prompt for healing
     */
    public static buildPrompt(analysis: LocatorAnalysis): string {
        const parts: string[] = [];

        parts.push('# Element Locator Healing Request');
        parts.push('');
        parts.push('## Element Key (Variable Name)');
        parts.push(`**${analysis.elementKey}**`);
        parts.push('');
        parts.push('## Required Element Type');
        parts.push(`**IMPORTANT: You MUST find a ${analysis.elementType}. Do NOT select other element types.**`);
        parts.push('');
        parts.push('## Failed Selector');
        parts.push(`\`${analysis.failedSelector}\``);
        parts.push('');

        if (Object.keys(analysis.expectedAttributes).length > 0) {
            parts.push('## Expected Attributes');
            for (const [key, value] of Object.entries(analysis.expectedAttributes)) {
                parts.push(`- ${key}: ${value}`);
            }
            parts.push('');
        }

        if (analysis.expectedText) {
            parts.push('## Expected Text Content');
            parts.push(`"${analysis.expectedText}"`);
            parts.push('');
        }

        if (analysis.previouslyWorkingSelectors.length > 0) {
            parts.push('## Previously Working Selectors');
            analysis.previouslyWorkingSelectors.forEach(sel => {
                parts.push(`- \`${sel}\``);
            });
            parts.push('');
        }

        parts.push('## Current DOM Context');
        parts.push(analysis.domContext);
        parts.push('');
        parts.push('## Task');
        parts.push(`Find selectors for the **${analysis.elementType}** element named "${analysis.elementKey}".`);
        parts.push('Generate 3-5 alternative Playwright-compatible selectors.');
        parts.push('Prioritize stability and uniqueness. Explain your reasoning.');

        return parts.join('\n');
    }
}

export default LocatorAnalyzer;
