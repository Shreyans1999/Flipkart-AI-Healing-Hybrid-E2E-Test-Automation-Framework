import * as fs from 'fs';
import * as path from 'path';
import { logger } from './Logger.js';

/**
 * Locator entry structure
 */
export interface LocatorEntry {
    primary: string;
    fallbacks: string[];
    lastHealed?: string;
    healCount?: number;
}

/**
 * Locator file structure
 */
export interface LocatorFile {
    [elementKey: string]: LocatorEntry;
}

/**
 * File utilities for reading/writing locator JSON files
 */
export class FileUtils {
    /**
     * Read JSON file
     */
    public static readJsonFile<T>(filePath: string): T | null {
        try {
            if (!fs.existsSync(filePath)) {
                logger.warn(`File not found: ${filePath}`);
                return null;
            }
            const content = fs.readFileSync(filePath, 'utf-8');
            return JSON.parse(content) as T;
        } catch (error) {
            logger.error(`Error reading JSON file: ${filePath}`, { error });
            return null;
        }
    }

    /**
     * Write JSON file with atomic write (write to temp, then rename)
     */
    public static writeJsonFile<T>(filePath: string, data: T): boolean {
        try {
            const dir = path.dirname(filePath);
            if (!fs.existsSync(dir)) {
                fs.mkdirSync(dir, { recursive: true });
            }

            // Create backup before modifying
            if (fs.existsSync(filePath)) {
                const backupPath = `${filePath}.backup`;
                fs.copyFileSync(filePath, backupPath);
            }

            // Write to temp file first
            const tempPath = `${filePath}.tmp`;
            const content = JSON.stringify(data, null, 2);
            fs.writeFileSync(tempPath, content, 'utf-8');

            // Rename temp to actual file (atomic operation)
            fs.renameSync(tempPath, filePath);

            logger.info(`Successfully wrote JSON file: ${filePath}`);
            return true;
        } catch (error) {
            logger.error(`Error writing JSON file: ${filePath}`, { error });
            return false;
        }
    }

    /**
     * Read locator file for a specific page
     */
    public static readLocatorFile(pageName: string): LocatorFile | null {
        const locatorPath = path.join(process.cwd(), 'src', 'locators', `${pageName}.locators.json`);
        return FileUtils.readJsonFile<LocatorFile>(locatorPath);
    }

    /**
     * Write locator file for a specific page
     */
    public static writeLocatorFile(pageName: string, data: LocatorFile): boolean {
        const locatorPath = path.join(process.cwd(), 'src', 'locators', `${pageName}.locators.json`);
        return FileUtils.writeJsonFile(locatorPath, data);
    }

    /**
     * Update a single locator entry
     */
    public static updateLocator(
        pageName: string,
        elementKey: string,
        newPrimarySelector: string,
        addToFallbacks: boolean = true
    ): boolean {
        const locators = FileUtils.readLocatorFile(pageName);
        if (!locators) {
            logger.error(`Cannot update locator: File not found for page ${pageName}`);
            return false;
        }

        const existingEntry = locators[elementKey];
        if (!existingEntry) {
            logger.error(`Cannot update locator: Element ${elementKey} not found in ${pageName}`);
            return false;
        }

        // Move current primary to fallbacks if requested
        if (addToFallbacks && !existingEntry.fallbacks.includes(existingEntry.primary)) {
            existingEntry.fallbacks.unshift(existingEntry.primary);
        }

        // Update primary selector
        existingEntry.primary = newPrimarySelector;
        existingEntry.lastHealed = new Date().toISOString();
        existingEntry.healCount = (existingEntry.healCount || 0) + 1;

        logger.info(`Updating locator: ${elementKey} -> ${newPrimarySelector}`);
        return FileUtils.writeLocatorFile(pageName, locators);
    }

    /**
     * Get locator for an element
     */
    public static getLocator(pageName: string, elementKey: string): LocatorEntry | null {
        const locators = FileUtils.readLocatorFile(pageName);
        if (!locators || !locators[elementKey]) {
            logger.warn(`Locator not found: ${pageName}.${elementKey}`);
            return null;
        }
        return locators[elementKey];
    }

    /**
     * Ensure directory exists
     */
    public static ensureDir(dirPath: string): void {
        if (!fs.existsSync(dirPath)) {
            fs.mkdirSync(dirPath, { recursive: true });
        }
    }

    /**
     * Check if file exists
     */
    public static fileExists(filePath: string): boolean {
        return fs.existsSync(filePath);
    }
}

export default FileUtils;
