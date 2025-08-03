import { test, expect } from '@playwright/test';

test.describe('Insurance Quote Application - Negative Scenarios', () => {
  test.beforeEach(async ({ page }) => {
    // Clear localStorage before each test
    await page.goto('/');
    await page.evaluate(() => localStorage.clear());
    await page.reload();
  });

  test('should show validation errors for empty required fields', async ({ page }) => {
    await page.goto('/');
    
    // Try to save without filling any fields
    await page.click('button:has-text("Save Quote")');
    
    // Check for validation error message
    await expect(page.locator('.alert-error')).toBeVisible();
    await expect(page.locator('.alert-error')).toContainText('required fields');
    
    // Check for individual field errors
    await expect(page.locator('.error-message')).toBeVisible();
    
    // Verify the form is still in edit mode
    await expect(page.locator('button:has-text("Save Quote")')).toBeVisible();
  });

  test('should validate business name minimum length', async ({ page }) => {
    await page.goto('/');
    
    // Enter a name that's too short
    await page.fill('#name', 'A');
    await page.keyboard.press('Tab'); // Trigger validation
    
    // Check for specific validation error
    await expect(page.locator('#name ~ .error-message')).toContainText('Business name must be at least 2 characters');
    
    // Try to save with invalid name
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'CA');
    await page.click('button:has-text("Save Quote")');
    
    // Should show validation errors
    await expect(page.locator('.alert-error')).toBeVisible();
    await expect(page.locator('.alert-error')).toContainText('Business name must be at least 2 characters');
  });

  test('should validate state field format', async ({ page }) => {
    await page.goto('/');
    
    // Test various invalid state formats
    const invalidStates = ['C', 'CAL', 'ca', '12', 'C1', ''];
    
    for (const invalidState of invalidStates) {
      await page.fill('#state', invalidState);
      await page.blur('#state');
      
      if (invalidState === '') {
        await expect(page.locator('.error-message')).toContainText('State is required');
      } else {
        await expect(page.locator('.error-message')).toContainText('State must be a 2-letter code');
      }
      
      // Clear the field for next iteration
      await page.fill('#state', '');
    }
  });

  test('should require all dropdown fields to be selected', async ({ page }) => {
    await page.goto('/');
    
    // Fill only some fields
    await page.fill('#name', 'Test Business');
    await page.fill('#state', 'NY');
    // Leave business type and industry empty
    
    await page.click('button:has-text("Save Quote")');
    
    // Should show validation errors
    await expect(page.locator('.alert:has-text("error")')).toBeVisible();
    await expect(page.locator('.alert:has-text("Business type is required")')).toBeVisible();
    await expect(page.locator('.alert:has-text("Industry is required")')).toBeVisible();
    
    // Check individual field error messages
    await expect(page.locator('#business-type + .error-message')).toContainText('Business type is required');
    await expect(page.locator('#industry + .error-message')).toContainText('Industry is required');
  });

  test('should handle invalid state format with visual feedback', async ({ page }) => {
    await page.goto('/');
    
    // Enter invalid state
    await page.fill('#state', 'california');
    await page.blur('#state');
    
    // Check that field has error styling
    await expect(page.locator('#state')).toHaveClass(/error/);
    await expect(page.locator('.error-message')).toContainText('State must be a 2-letter code');
    
    // Fill other valid fields
    await page.fill('#name', 'Valid Business');
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    
    // Try to save
    await page.click('button:has-text("Save Quote")');
    
    // Should still show validation error
    await expect(page.locator('.alert:has-text("error")')).toBeVisible();
    await expect(page.locator('.alert:has-text("State must be a 2-letter code")')).toBeVisible();
  });

  test('should prevent saving with partially filled form', async ({ page }) => {
    await page.goto('/');
    
    // Fill only name and business type
    await page.fill('#name', 'Partial Business');
    await page.selectOption('#business-type', 'TECHNOLOGY');
    
    // Try to save
    await page.click('button:has-text("Save Quote")');
    
    // Should show validation errors for missing fields
    await expect(page.locator('.alert:has-text("error")')).toBeVisible();
    await expect(page.locator('.alert:has-text("Industry is required")')).toBeVisible();
    await expect(page.locator('.alert:has-text("State must be a 2-letter code")')).toBeVisible();
    
    // Verify quote was not saved
    await expect(page.locator('button:has-text("Save Quote")')).toBeVisible();
    await expect(page.locator('.status-draft')).toBeVisible();
  });

  test('should handle form reset on cancel', async ({ page }) => {
    await page.goto('/');
    
    // Fill out some fields
    await page.fill('#name', 'Test Business');
    await page.selectOption('#business-type', 'RETAIL');
    
    // Click cancel
    await page.click('button:has-text("Cancel")');
    
    // Verify form is reset
    await expect(page.locator('#name')).toHaveValue('');
    await expect(page.locator('#business-type')).toHaveValue('');
    
    // Verify no error messages are shown
    await expect(page.locator('.alert:has-text("error")')).toBeHidden();
    await expect(page.locator('.error-message')).toBeHidden();
  });

  test('should handle maximum character limits', async ({ page }) => {
    await page.goto('/');
    
    // Test state field max length (should only accept 2 characters)
    await page.fill('#state', 'CALIFORNIA'); // More than 2 characters
    
    // Verify only 2 characters are accepted
    await expect(page.locator('#state')).toHaveValue('CA');
    
    // Test extremely long business name
    const longName = 'A'.repeat(1000);
    await page.fill('#name', longName);
    
    // Should accept it but show validation error on save since it's not practical
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'NY');
    
    await page.click('button:has-text("Save Quote")');
    
    // Should save successfully (no explicit max length validation on name)
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible();
  });

  test('should prevent saving with invalid form state after edit', async ({ page }) => {
    await page.goto('/');
    
    // First save a valid quote
    await page.fill('#name', 'Valid Business');
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'CA');
    await page.click('button:has-text("Save Quote")');
    
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible();
    
    // Enter edit mode
    await page.click('button:has-text("Edit")');
    
    // Make the form invalid
    await page.fill('#name', ''); // Clear required field
    await page.fill('#state', 'INVALID'); // Invalid state
    
    // Try to save
    await page.click('button:has-text("Save Quote")');
    
    // Should show validation errors
    await expect(page.locator('.alert:has-text("error")')).toBeVisible();
    await expect(page.locator('.alert:has-text("Business name must be at least 2 characters long")')).toBeVisible();
    await expect(page.locator('.alert:has-text("State must be a 2-letter code")')).toBeVisible();
    
    // Should remain in edit mode
    await expect(page.locator('button:has-text("Save Quote")')).toBeVisible();
  });

  test('should handle special characters in form fields', async ({ page }) => {
    await page.goto('/');
    
    // Test business name with special characters
    await page.fill('#name', 'Test & Co. LLC (Special Characters)');
    await page.selectOption('#business-type', 'PROFESSIONAL');
    await page.selectOption('#industry', 'CONSULTING');
    await page.fill('#state', 'NY');
    
    // Should save successfully
    await page.click('button:has-text("Save Quote")');
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible();
    
    // Verify special characters are preserved
    await expect(page.locator('#name')).toHaveValue('Test & Co. LLC (Special Characters)');
  });

  test('should handle form submission with network-like delays', async ({ page }) => {
    await page.goto('/');
    
    // Fill valid form
    await page.fill('#name', 'Network Test Business');
    await page.selectOption('#business-type', 'TECHNOLOGY');
    await page.selectOption('#industry', 'SOFTWARE');
    await page.fill('#state', 'WA');
    
    // Simulate rapid clicking of save button
    await page.click('button:has-text("Save Quote")');
    await page.click('button:has-text("Save Quote")'); // Second click should be ignored/handled gracefully
    
    // Should still save successfully without errors
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible();
    await expect(page.locator('button:has-text("Edit")')).toBeVisible();
    
    // Should not show duplicate success messages
    const successMessages = await page.locator('.alert:has-text("successfully")').count();
    expect(successMessages).toBe(1);
  });
});