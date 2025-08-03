import { test, expect } from '@playwright/test';

test.describe('Insurance Quote Application - Edge Cases', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.evaluate(() => localStorage.clear());
    await page.reload();
  });

  test('should handle rapid form interactions', async ({ page }) => {
    await page.goto('/');
    
    // Rapidly fill and clear fields
    for (let i = 0; i < 5; i++) {
      await page.fill('#name', `Business ${i}`);
      await page.fill('#name', '');
    }
    
    // Fill final valid data
    await page.fill('#name', 'Final Business');
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'CA');
    
    // Should save successfully
    await page.click('button:has-text("Save Quote")');
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 5000 });
  });

  test('should handle coverage selection and deselection', async ({ page }) => {
    await page.goto('/');
    
    // Select and deselect coverage multiple times
    const coverageCheckbox = page.locator('#general-liability-checkbox');
    
    for (let i = 0; i < 3; i++) {
      // Click to check the coverage
      await coverageCheckbox.click();
      // Wait for the premium to update
      await expect(page.locator('.premium-amount')).toContainText('$500', { timeout: 5000 });
      
      // Click to uncheck the coverage
      await coverageCheckbox.click();
      // Wait for the premium to reset
      await expect(page.locator('.premium-amount')).toContainText('$0', { timeout: 5000 });
    }
  });

  test('should maintain form state during coverage interactions', async ({ page }) => {
    await page.goto('/');
    
    // Fill business information
    await page.fill('#name', 'Test Business');
    await page.selectOption('#business-type', 'TECHNOLOGY');
    await page.selectOption('#industry', 'SOFTWARE');
    await page.fill('#state', 'TX');
    
    // Interact with coverage options
    await page.click('.coverage-item:has(.coverage-name:text("Property"))');
    await page.check('#property-checkbox');
    
    // Verify business form data is preserved
    await expect(page.locator('#name')).toHaveValue('Test Business');
    await expect(page.locator('#business-type')).toHaveValue('TECHNOLOGY');
    await expect(page.locator('#industry')).toHaveValue('SOFTWARE');
    await expect(page.locator('#state')).toHaveValue('TX');
  });

  test('should handle browser back/forward navigation', async ({ page }) => {
    await page.goto('/');
    
    // Fill and save a quote
    await page.fill('#name', 'Navigation Test');
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'NY');
    await page.click('button:has-text("Save Quote")');
    
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 5000 });
    
    // Navigate to a different page (if routing was implemented) or simulate
    // For now, just reload and verify persistence
    await page.reload();
    
    // Verify data persists
    await expect(page.locator('#name')).toHaveValue('Navigation Test');
    await expect(page.locator('button:has-text("Edit")')).toBeVisible();
  });

  test('should handle window resize and responsive behavior', async ({ page }) => {
    await page.goto('/');
    
    // Test desktop view
    await page.setViewportSize({ width: 1200, height: 800 });
    await expect(page.locator('.left-column')).toBeVisible();
    await expect(page.locator('.right-column')).toBeVisible();
    
    // Test tablet view
    await page.setViewportSize({ width: 768, height: 1024 });
    await expect(page.locator('.main-content')).toBeVisible();
    
    // Test mobile view
    await page.setViewportSize({ width: 375, height: 667 });
    await expect(page.locator('.main-content')).toBeVisible();
    
    // Fill form in mobile view
    await page.fill('#name', 'Mobile Test');
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'CA');
    
    // Should still work on mobile
    await page.click('button:has-text("Save Quote")');
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 5000 });
  });

  test('should handle keyboard navigation', async ({ page }) => {
    await page.goto('/');
    
    // Tab through form fields
    await page.keyboard.press('Tab'); // Should focus first form field
    await page.keyboard.type('Keyboard Test Business');
    
    await page.keyboard.press('Tab'); // Business type dropdown
    await page.keyboard.press('ArrowDown'); // Select first option
    await page.keyboard.press('Enter');
    
    await page.keyboard.press('Tab'); // Industry dropdown
    await page.keyboard.press('ArrowDown');
    await page.keyboard.press('Enter');
    
    await page.keyboard.press('Tab'); // State field
    await page.keyboard.type('WA');
    
    await page.keyboard.press('Tab'); // Save button
    await page.keyboard.press('Enter'); // Click save
    
    // Should save successfully using only keyboard
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 5000 });
  });

  test('should handle localStorage corruption/unavailability', async ({ page }) => {
    await page.goto('/');
    
    // Simulate localStorage being unavailable
    await page.addInitScript(() => {
      Object.defineProperty(window, 'localStorage', {
        value: {
          getItem: () => { throw new Error('localStorage unavailable'); },
          setItem: () => { throw new Error('localStorage unavailable'); },
          removeItem: () => { throw new Error('localStorage unavailable'); },
          clear: () => { throw new Error('localStorage unavailable'); }
        }
      });
    });
    
    await page.reload();
    
    // Application should still work without localStorage
    await expect(page.locator('h2:has-text("Business Information")')).toBeVisible();
    
    // Fill form
    await page.fill('#name', 'No Storage Test');
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'OR');
    
    // Should attempt to save (though won't persist)
    await page.click('button:has-text("Save Quote")');
    // May show success or error depending on implementation
  });

  test('should handle multiple quotes lifecycle', async ({ page }) => {
    await page.goto('/');
    
    // Create first quote
    await page.fill('#name', 'First Business');
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'CA');
    await page.click('button:has-text("Save Quote")');
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 5000 });
    
    // Clear and create second quote (simulating new quote creation)
    await page.evaluate(() => {
      // This would typically be done through a "New Quote" button
      // For testing, we'll simulate the service behavior
      window.localStorage.clear();
    });
    await page.reload();
    
    // Create second quote
    await page.fill('#name', 'Second Business');
    await page.selectOption('#business-type', 'TECHNOLOGY');
    await page.selectOption('#industry', 'SOFTWARE');
    await page.fill('#state', 'NY');
    await page.click('button:has-text("Save Quote")');
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 5000 });
  });

  test('should handle form focus and blur events', async ({ page }) => {
    await page.goto('/');
    
    // Focus and blur name field with invalid data
    await page.focus('#name');
    await page.keyboard.type('A'); // Too short
    await page.blur('#name');
    
    // Should show validation error
    await expect(page.locator('.error-message')).toContainText('Business name must be at least 2 characters');
    
    // Fix the field
    await page.focus('#name');
    await page.keyboard.press('Control+a');
    await page.keyboard.type('Valid Business Name');
    await page.blur('#name');
    
    // Error should clear
    await expect(page.locator('.error-message')).toBeHidden();
  });

  test('should handle concurrent coverage selections', async ({ page }) => {
    await page.goto('/');
    
    // Select multiple coverages rapidly
    await Promise.all([
      page.click('.coverage-item:has(.coverage-name:text("General Liability"))'),
      page.click('.coverage-item:has(.coverage-name:text("Property"))'),
      page.click('.coverage-item:has(.coverage-name:text("Additional Coverage Options"))')
    ]);
    
    // Check all coverages
    await page.check('input[type="checkbox"]:near(.coverage-name:text("General Liability"))');
    await page.check('input[type="checkbox"]:near(.coverage-name:text("Property"))');
    await page.check('input[type="checkbox"]:near(.coverage-name:text("Additional Coverage Options"))');
    
    // Verify total premium calculation
    await expect(page.locator('.premium-amount')).toContainText('$1,550');
  });
});