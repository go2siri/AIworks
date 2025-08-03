import { test, expect } from '@playwright/test';

test.describe('Insurance Quote Application - Positive Scenarios', () => {
  test.beforeEach(async ({ page }) => {
    // Clear localStorage before each test
    await page.goto('/');
    await page.evaluate(() => localStorage.clear());
    await page.reload();
  });

  test('should load the application with all components', async ({ page }) => {
    await page.goto('/');
    
    // Check that all main components are present
    await expect(page.locator('h2:has-text("Business Information")')).toBeVisible();
    await expect(page.locator('h2:has-text("Quote Summary")')).toBeVisible();
    await expect(page.locator('h2:has-text("Risk Rating Snapshot")')).toBeVisible();
    await expect(page.locator('h2:has-text("Coverage Configurator")')).toBeVisible();
    await expect(page.locator('h2:has-text("Underwriter Notes")')).toBeVisible();
  });

  test('should successfully create and save a new quote', async ({ page }) => {
    await page.goto('/');
    
    // Fill out business information form
    await page.fill('#name', 'Test Business LLC');
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'CA');
    
    // Verify form fields are filled
    await expect(page.locator('#name')).toHaveValue('Test Business LLC');
    await expect(page.locator('#business-type')).toHaveValue('RETAIL');
    await expect(page.locator('#industry')).toHaveValue('RETAIL_TRADE');
    await expect(page.locator('#state')).toHaveValue('CA');
    
    // Click save button
    await page.click('button:has-text("Save Quote")');
    
    // Check for success message
    await expect(page.locator('.alert').filter({ hasText: 'Quote successfully saved' })).toBeVisible({ timeout: 10000 });
    
    // Verify button changes to Update Quote
    await expect(page.locator('button:has-text("Update Quote")')).toBeVisible();
    
    // Verify quote appears in the list
    await page.click('button:has-text("List Quotes")');
    await expect(page.locator('.quote-item')).toBeVisible();
  });

  test('should allow editing an existing quote', async ({ page }) => {
    await page.goto('/');
    
    // First, create and save a quote
    await page.fill('#name', 'Original Business');
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'NY');
    await page.click('button:has-text("Save Quote")');
    
    // Wait for save confirmation
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 5000 });
    
    // Click Edit button
    await page.click('button:has-text("Edit")');
    
    // Verify we're in edit mode (Save button should be visible)
    await expect(page.locator('button:has-text("Save Quote")')).toBeVisible();
    
    // Modify the business name
    await page.fill('#name', 'Updated Business LLC');
    
    // Save the changes
    await page.click('button:has-text("Save Quote")');
    
    // Verify success message and updated data
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 5000 });
    await expect(page.locator('#name')).toHaveValue('Updated Business LLC');
    await expect(page.locator('button:has-text("Edit")')).toBeVisible();
  });

  test('should calculate premium when coverage options are selected', async ({ page }) => {
    await page.goto('/');
    
    // Initially premium should be $0
    await expect(page.locator('.premium-amount')).toContainText('$0');
    
    // Expand and select General Liability coverage
    await page.click('.coverage-item:has(.coverage-name:text("General Liability"))');
    await expect(page.locator('.coverage-details')).toBeVisible();
    
    // Select the coverage checkbox
    await page.check('input[type="checkbox"]:near(.coverage-name:text("General Liability"))');
    
    // Verify premium is updated
    await expect(page.locator('.premium-amount')).toContainText('$500');
    
    // Select Property coverage
    await page.click('.coverage-item:has(.coverage-name:text("Property"))');
    await page.check('input[type="checkbox"]:near(.coverage-name:text("Property"))');
    
    // Verify premium is updated to include both
    await expect(page.locator('.premium-amount')).toContainText('$1,250');
    
    // Select Additional Coverage
    await page.click('.coverage-item:has(.coverage-name:text("Additional Coverage Options"))');
    await page.check('input[type="checkbox"]:near(.coverage-name:text("Additional Coverage Options"))');
    
    // Verify total premium
    await expect(page.locator('.premium-amount')).toContainText('$1,550');
  });

  test('should expand and collapse coverage sections', async ({ page }) => {
    await page.goto('/');
    
    // Initially, details should not be visible
    await expect(page.locator('.coverage-details')).toBeHidden();
    
    // Click to expand General Liability
    await page.click('.coverage-item:has(.coverage-name:text("General Liability"))');
    
    // Verify details are now visible
    await expect(page.locator('.coverage-details').first()).toBeVisible();
    await expect(page.locator('.coverage-details').first()).toContainText('General liability insurance protects');
    
    // Verify the expand icon changed
    await expect(page.locator('.expand-icon').first()).toContainText('⌄');
    
    // Click again to collapse
    await page.click('.coverage-item:has(.coverage-name:text("General Liability"))');
    
    // Verify details are hidden again
    await expect(page.locator('.coverage-details').first()).toBeHidden();
    await expect(page.locator('.expand-icon').first()).toContainText('›');
  });

  test('should persist data between page refreshes', async ({ page }) => {
    await page.goto('/');
    
    // Fill out and save a quote
    await page.fill('#name', 'Persistent Business');
    await page.selectOption('#business-type', 'TECHNOLOGY');
    await page.selectOption('#industry', 'SOFTWARE');
    await page.fill('#state', 'TX');
    
    // Select some coverage
    await page.click('.coverage-item:has(.coverage-name:text("General Liability"))');
    await page.check('input[type="checkbox"]:near(.coverage-name:text("General Liability"))');
    
    await page.click('button:has-text("Save Quote")');
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible();
    
    // Refresh the page
    await page.reload();
    
    // Verify data persists
    await expect(page.locator('#name')).toHaveValue('Persistent Business');
    await expect(page.locator('#business-type')).toHaveValue('TECHNOLOGY');
    await expect(page.locator('#industry')).toHaveValue('SOFTWARE');
    await expect(page.locator('#state')).toHaveValue('TX');
    
    // Verify coverage selection persists
    await expect(page.locator('input[type="checkbox"]:near(.coverage-name:text("General Liability"))')).toBeChecked();
    await expect(page.locator('.premium-amount')).toContainText('$500');
    
    // Verify quote is in saved state
    await expect(page.locator('button:has-text("Edit")')).toBeVisible();
    await expect(page.locator('.status-saved')).toBeVisible();
  });

  test('should handle form auto-save functionality', async ({ page }) => {
    await page.goto('/');
    
    // Fill out form fields gradually
    await page.fill('#name', 'Auto Save Test');
    
    // Wait a moment for auto-save to process
    await page.waitForTimeout(500);
    
    await page.selectOption('#business-type', 'HEALTHCARE');
    await page.waitForTimeout(500);
    
    await page.selectOption('#industry', 'HEALTHCARE');
    await page.waitForTimeout(500);
    
    await page.fill('#state', 'FL');
    await page.waitForTimeout(500);
    
    // Refresh without saving manually
    await page.reload();
    
    // Verify that form data was auto-saved (even though quote status is draft)
    await expect(page.locator('#name')).toHaveValue('Auto Save Test');
    await expect(page.locator('#business-type')).toHaveValue('HEALTHCARE');
    await expect(page.locator('#industry')).toHaveValue('HEALTHCARE');
    await expect(page.locator('#state')).toHaveValue('FL');
    
    // Verify it's still in draft status
    await expect(page.locator('.status-draft')).toBeVisible();
  });
});