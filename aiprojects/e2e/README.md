# End-to-End Test Suite for Insurance Quote Application

## Overview
This test suite uses Playwright to test the Insurance Quote Application's functionality, including form validation, data persistence, and user interactions.

## Test Categories

### 1. Positive Scenarios (`positive-scenarios.spec.ts`)
Tests that verify the application works correctly under normal conditions:

- **Application Loading**: Verifies all components render properly
- **Quote Creation**: Tests successful quote creation and saving
- **Quote Editing**: Tests editing existing quotes
- **Premium Calculation**: Verifies premium updates when coverage options change
- **Coverage Expansion**: Tests accordion-style coverage section functionality
- **Data Persistence**: Verifies quotes persist across page refreshes
- **Auto-Save**: Tests automatic form data saving

### 2. Negative Scenarios (`negative-scenarios.spec.ts`)
Tests that verify proper error handling and validation:

- **Required Field Validation**: Tests empty field validation
- **Business Name Validation**: Tests minimum length requirements
- **State Format Validation**: Tests 2-letter state code requirement
- **Dropdown Validation**: Tests required dropdown selections
- **Form State Validation**: Tests visual error feedback
- **Partial Form Submission**: Tests preventing saves with incomplete data
- **Form Reset**: Tests cancel functionality
- **Character Limits**: Tests input length restrictions
- **Edit Mode Validation**: Tests validation during edit operations
- **Special Characters**: Tests handling of special characters in inputs
- **Rapid Interactions**: Tests handling of quick user actions

### 3. Edge Cases (`edge-cases.spec.ts`)
Tests that cover unusual or boundary conditions:

- **Rapid Form Interactions**: Tests quick field changes
- **Coverage Toggle**: Tests repeated selection/deselection
- **State Preservation**: Tests form state during coverage interactions
- **Navigation**: Tests browser back/forward behavior
- **Responsive Design**: Tests different screen sizes
- **Keyboard Navigation**: Tests accessibility via keyboard
- **Storage Unavailability**: Tests localStorage failures
- **Multiple Quotes**: Tests quote lifecycle management
- **Focus/Blur Events**: Tests field validation triggers
- **Concurrent Operations**: Tests simultaneous user actions

## Running Tests

### Prerequisites
1. Ensure the Angular application is built and can be served
2. Install dependencies: `npm install`
3. Install Playwright browsers: `npx playwright install`

### Test Commands

```bash
# Run all tests
npm run test:e2e

# Run tests with UI mode (interactive)
npm run test:e2e:ui

# Run tests in headed mode (see browser)
npm run test:e2e:headed

# Run tests in debug mode
npm run test:e2e:debug

# Run specific test file
npx playwright test positive-scenarios.spec.ts

# Run tests in specific browser
npx playwright test --project=chromium
```

### Test Configuration
The tests are configured in `playwright.config.ts` with:
- Base URL: `http://localhost:4200`
- Automatic web server startup
- Cross-browser testing (Chrome, Firefox, Safari)
- Test retries and parallel execution
- HTML reporting

## Test Data and Scenarios

### Valid Test Data
- **Business Names**: "Test Business LLC", "Updated Business LLC", etc.
- **Business Types**: retail, restaurant, technology, manufacturing, healthcare, professional
- **Industries**: food-service, retail-trade, software, healthcare, consulting, manufacturing
- **States**: CA, NY, TX, FL, WA, OR (2-letter codes)

### Invalid Test Data
- **Short Names**: "A" (less than 2 characters)
- **Invalid States**: "C", "CAL", "ca", "12", "C1"
- **Empty Fields**: Testing required field validation

### Coverage Options
- **General Liability**: $500 premium
- **Property**: $750 premium  
- **Additional Coverage**: $300 premium

## Expected Behaviors

### Successful Operations
- Form saves with valid data show success message
- Premium updates automatically when coverage changes
- Data persists across page refreshes
- Edit mode toggles between Save and Edit buttons
- Coverage sections expand/collapse properly

### Error Handling
- Invalid data shows appropriate error messages
- Required fields prevent form submission when empty
- Visual feedback (red borders) for invalid fields
- Error messages clear when fields are corrected
- Form remains in edit state when validation fails

## Accessibility Features Tested
- Keyboard navigation through form fields
- Tab order and focus management
- Error message associations with form fields
- Button state changes and accessibility

## Performance Considerations
- Tests include rapid interaction scenarios
- Browser responsiveness testing
- localStorage performance under load
- Concurrent operation handling

## Browser Compatibility
Tests run across multiple browsers:
- **Chromium**: Latest stable version
- **Firefox**: Latest stable version
- **WebKit**: Safari engine testing

## Reporting
Test results are available in:
- Console output during test runs
- HTML report (generated after test completion)
- Screenshots on failures
- Video recordings of test runs (optional)

## Maintenance Notes
- Update test data when application requirements change
- Add new test scenarios for new features
- Review and update selectors if UI changes
- Monitor test execution times and optimize as needed