import { test, expect } from '@playwright/test';

test.describe('Full Stack Integration Tests', () => {
  test.beforeEach(async ({ page }) => {
    // Ensure backend is running
    try {
      const response = await page.request.get('http://localhost:8080/api/actuator/health');
      expect(response.status()).toBe(200);
    } catch (error) {
      throw new Error('Backend server is not running. Please start the backend first.');
    }
    
    // Clear localStorage and go to app
    await page.goto('/');
    await page.evaluate(() => localStorage.clear());
    await page.reload();
  });

  test('should create, save, and retrieve quote from backend', async ({ page }) => {
    await page.goto('/');
    
    // Fill out business information
    await page.fill('#name', 'Full Stack Test Business');
    await page.selectOption('#business-type', 'TECHNOLOGY');
    await page.selectOption('#industry', 'SOFTWARE');
    await page.fill('#state', 'CA');
    
    // Verify form is filled
    await expect(page.locator('#name')).toHaveValue('Full Stack Test Business');
    
    // Save the quote
    await page.click('button:has-text("Save Quote")');
    
    // Wait for success message
    await expect(page.locator('.alert').filter({ hasText: 'Quote successfully saved' })).toBeVisible({ timeout: 10000 });
    
    // Verify quote appears in list
    await page.click('button:has-text("List Quotes")');
    await expect(page.locator('.quote-item')).toBeVisible();
    
    // Verify backend has the quote by checking API directly
    const response = await page.request.get('http://localhost:8080/api/quotes');
    expect(response.status()).toBe(200);
    
    const quotesData = await response.json();
    expect(quotesData.content).toBeDefined();
    expect(quotesData.content.length).toBeGreaterThan(0);
    
    const savedQuote = quotesData.content.find((q: any) => 
      q.businessInformation.name === 'Full Stack Test Business'
    );
    expect(savedQuote).toBeDefined();
    expect(savedQuote.businessInformation.businessType).toBe('TECHNOLOGY');
    expect(savedQuote.businessInformation.industry).toBe('SOFTWARE');
    expect(savedQuote.businessInformation.state).toBe('CA');
  });

  test('should handle coverage selection and premium calculation via API', async ({ page }) => {
    await page.goto('/');
    
    // Create a quote first
    await page.fill('#name', 'Premium Test Business');
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'NY');
    await page.click('button:has-text("Save Quote")');
    
    // Wait for quote to be saved
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 10000 });
    
    // Select coverage options
    await page.click('.coverage-item:has(.coverage-name:text("General Liability"))');
    await page.check('input[type="checkbox"]:near(.coverage-name:text("General Liability"))');
    
    // Verify premium calculation
    await expect(page.locator('.premium-amount')).toContainText('500', { timeout: 5000 });
    
    // Select additional coverage
    await page.click('.coverage-item:has(.coverage-name:text("Property"))');
    await page.check('input[type="checkbox"]:near(.coverage-name:text("Property"))');
    
    // Verify total premium
    await expect(page.locator('.premium-amount')).toContainText('1,250', { timeout: 5000 });
    
    // Save the updated quote
    await page.click('button:has-text("Edit")');
    await page.click('button:has-text("Save Quote")');
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 10000 });
    
    // Verify the backend has the updated premium
    const response = await page.request.get('http://localhost:8080/api/quotes');
    const quotesData = await response.json();
    const updatedQuote = quotesData.content.find((q: any) => 
      q.businessInformation.name === 'Premium Test Business'
    );
    
    expect(updatedQuote.totalPremium).toBe(1250);
    expect(updatedQuote.coverageOptions.some((opt: any) => 
      opt.coverageType === 'GENERAL_LIABILITY' && opt.isSelected
    )).toBe(true);
    expect(updatedQuote.coverageOptions.some((opt: any) => 
      opt.coverageType === 'PROPERTY' && opt.isSelected
    )).toBe(true);
  });

  test('should handle validation errors from backend', async ({ page }) => {
    await page.goto('/');
    
    // Try to save with invalid data
    await page.fill('#name', 'A'); // Too short
    await page.fill('#state', 'INVALID'); // Invalid format
    
    await page.click('button:has-text("Save Quote")');
    
    // Should show validation errors
    await expect(page.locator('.alert:has-text("error")')).toBeVisible({ timeout: 10000 });
    await expect(page.locator('.error-message')).toBeVisible();
  });

  test('should persist data across page refreshes with backend sync', async ({ page }) => {
    await page.goto('/');
    
    // Create and save a quote
    await page.fill('#name', 'Persistence Test Business');
    await page.selectOption('#business-type', 'HEALTHCARE');
    await page.selectOption('#industry', 'HEALTHCARE_SERVICES');
    await page.fill('#state', 'TX');
    
    await page.click('button:has-text("Save Quote")');
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 10000 });
    
    // Get the quote ID from the backend
    const response = await page.request.get('http://localhost:8080/api/quotes');
    const quotesData = await response.json();
    const savedQuote = quotesData.content.find((q: any) => 
      q.businessInformation.name === 'Persistence Test Business'
    );
    
    // Refresh the page
    await page.reload();
    
    // Data should still be there (loaded from backend or localStorage)
    await expect(page.locator('#name')).toHaveValue('Persistence Test Business');
    await expect(page.locator('#business-type')).toHaveValue('HEALTHCARE');
    await expect(page.locator('#industry')).toHaveValue('HEALTHCARE_SERVICES');
    await expect(page.locator('#state')).toHaveValue('TX');
    await expect(page.locator('button:has-text("Edit")')).toBeVisible();
  });

  test('should handle backend unavailable gracefully', async ({ page }) => {
    await page.goto('/');
    
    // Mock backend failure by intercepting requests
    await page.route('http://localhost:8080/api/**', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'Internal Server Error' })
      });
    });
    
    // Try to save a quote
    await page.fill('#name', 'Backend Error Test');
    await page.selectOption('#business-type', 'RETAIL');
    await page.selectOption('#industry', 'RETAIL_TRADE');
    await page.fill('#state', 'CA');
    
    await page.click('button:has-text("Save Quote")');
    
    // Should show error message
    await expect(page.locator('.alert:has-text("error")')).toBeVisible({ timeout: 10000 });
  });

  test('should display quote statistics from backend', async ({ page }) => {
    // First ensure we have some quotes
    await page.goto('/');
    
    // Create a test quote
    await page.fill('#name', 'Statistics Test Business');
    await page.selectOption('#business-type', 'MANUFACTURING');
    await page.selectOption('#industry', 'MANUFACTURING');
    await page.fill('#state', 'OH');
    await page.click('button:has-text("Save Quote")');
    await expect(page.locator('.alert:has-text("successfully")')).toBeVisible({ timeout: 10000 });
    
    // Check statistics via API
    const statsResponse = await page.request.get('http://localhost:8080/api/quotes/statistics');
    expect(statsResponse.status()).toBe(200);
    
    const stats = await statsResponse.json();
    expect(stats.totalQuotes).toBeGreaterThan(0);
    expect(stats.savedQuotes).toBeGreaterThan(0);
    expect(stats.totalPremiumValue).toBeGreaterThanOrEqual(0);
  });
});

test.describe('Backend API Direct Tests', () => {
  test('should create quote via API', async ({ request }) => {
    const newQuote = {
      businessInformation: {
        name: 'API Test Business',
        businessType: 'TECHNOLOGY',
        industry: 'SOFTWARE',
        state: 'WA'
      },
      coverageOptions: [],
      totalPremium: 0,
      status: 'DRAFT'
    };
    
    const response = await request.post('http://localhost:8080/api/quotes', {
      data: newQuote
    });
    
    expect(response.status()).toBe(201);
    const createdQuote = await response.json();
    expect(createdQuote.id).toBeDefined();
    expect(createdQuote.quoteNumber).toBeDefined();
    expect(createdQuote.businessInformation.name).toBe('API Test Business');
  });

  test('should validate required fields', async ({ request }) => {
    const invalidQuote = {
      businessInformation: {
        name: '', // Invalid - empty name
        businessType: 'TECHNOLOGY',
        industry: 'SOFTWARE',
        state: 'INVALID' // Invalid state format
      },
      coverageOptions: [],
      totalPremium: 0,
      status: 'DRAFT'
    };
    
    const response = await request.post('http://localhost:8080/api/quotes', {
      data: invalidQuote
    });
    
    expect(response.status()).toBe(400);
    const errorResponse = await response.json();
    expect(errorResponse.validationErrors).toBeDefined();
  });

  test('should retrieve all quotes', async ({ request }) => {
    const response = await request.get('http://localhost:8080/api/quotes');
    expect(response.status()).toBe(200);
    
    const quotesPage = await response.json();
    expect(quotesPage.content).toBeDefined();
    expect(quotesPage.totalElements).toBeGreaterThanOrEqual(0);
    expect(quotesPage.pageable).toBeDefined();
  });

  test('should handle quote not found', async ({ request }) => {
    const response = await request.get('http://localhost:8080/api/quotes/99999');
    expect(response.status()).toBe(404);
    
    const errorResponse = await response.json();
    expect(errorResponse.message).toContain('not found');
  });
});