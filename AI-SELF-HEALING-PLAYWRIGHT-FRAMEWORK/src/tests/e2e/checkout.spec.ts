import { test, expect } from '@playwright/test';
import { logger } from '../../utils/Logger.js';

/**
 * Interactive Elements E2E Tests
 * 
 * Tests for various interactive elements on the demo site that
 * benefit from the self-healing framework.
 */
test.describe('Checkout Tests', () => {
    test('Checkbox toggle interactions', async ({ page }) => {
        // Navigate to checkboxes page
        await page.goto('/checkboxes');

        // Get both checkboxes
        const checkbox1 = page.locator('input[type="checkbox"]').first();
        const checkbox2 = page.locator('input[type="checkbox"]').last();

        // Verify initial states
        await expect(checkbox1).not.toBeChecked();
        await expect(checkbox2).toBeChecked();

        // Toggle checkbox 1 (check it)
        await checkbox1.check();
        await expect(checkbox1).toBeChecked();

        // Toggle checkbox 2 (uncheck it)
        await checkbox2.uncheck();
        await expect(checkbox2).not.toBeChecked();

        // Toggle both again
        await checkbox1.uncheck();
        await checkbox2.check();
        await expect(checkbox1).not.toBeChecked();
        await expect(checkbox2).toBeChecked();

        logger.info('Checkbox toggle test passed');
    });

    test('File upload functionality', async ({ page }) => {
        // Navigate to file upload page
        await page.goto('/upload');

        // Verify upload elements are present
        const fileInput = page.locator('#file-upload');
        const uploadButton = page.locator('#file-submit');

        await expect(fileInput).toBeVisible();
        await expect(uploadButton).toBeVisible();

        // Create a test file and upload it
        // Using Playwright's setInputFiles to simulate file selection
        await fileInput.setInputFiles({
            name: 'test-file.txt',
            mimeType: 'text/plain',
            buffer: Buffer.from('This is a test file for upload testing')
        });

        // Click upload button
        await uploadButton.click();

        // Verify upload success
        await expect(page.locator('#uploaded-files')).toContainText('test-file.txt');

        logger.info('File upload test passed');
    });

    test('Dynamic loading and waiting', async ({ page }) => {
        // Navigate to dynamic loading page
        await page.goto('/dynamic_loading/1');

        // Verify the start button is visible
        const startButton = page.locator('#start button');
        await expect(startButton).toBeVisible();

        // The finish element should be hidden initially
        const finishText = page.locator('#finish');
        await expect(finishText).not.toBeVisible();

        // Click start to trigger loading
        await startButton.click();

        // Wait for loading to complete and verify result
        await expect(finishText).toBeVisible({ timeout: 10000 });
        await expect(finishText).toContainText('Hello World!');

        logger.info('Dynamic loading test passed');
    });

    // Keep existing tests
    test('Navigate through multiple pages', async ({ page }) => {
        // This test demonstrates navigation through the demo site
        // The self-healing would kick in if any locators change

        await page.goto('/');

        // Click on checkboxes link
        await page.click('a[href="/checkboxes"]');
        await expect(page).toHaveURL(/checkboxes/);

        // Navigate back
        await page.goBack();

        // Click on dropdown link
        await page.click('a[href="/dropdown"]');
        await expect(page).toHaveURL(/dropdown/);

        logger.info('Multi-page navigation test passed');
    });

    test('Interact with form elements', async ({ page }) => {
        // Test form interactions that would benefit from self-healing

        // Navigate to dropdown page
        await page.goto('/dropdown');

        // Select an option
        await page.selectOption('#dropdown', 'Option 1');

        // Verify selection
        const selectedValue = await page.locator('#dropdown').inputValue();
        expect(selectedValue).toBe('1');

        logger.info('Form elements test passed');
    });
});
