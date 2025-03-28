import { test, expect } from '@playwright/test';

test.describe('Review Sorting Functionality', () => {
  
  test.beforeEach(async ({ page }) => {
    console.log("Opening login page...");
    await page.goto('http://localhost:5173/signin');

    await page.fill('input[placeholder="Username..."]', 'admin'); 
    await page.fill('input[placeholder="Password..."]', '11111111'); 
    await page.click('button:has-text("Sign In")');

    console.log("Login successful, navigating to review page...");
    await page.waitForTimeout(2000); // Ensure login processing
    await page.goto('http://localhost:5173/reviewlistview');
  });

  const extractRatings = async (page) => {
    return await page.$$eval('.review-card', (reviews) =>
      reviews.map((review) => {
        return review.querySelectorAll('.text-yellow-500').length; // Count filled stars
      })
    );
  };

  test('should sort reviews from High to Low rating', async ({ page }) => {
    await page.click('button:has-text("All Review")');
    await page.click('li:has-text("High-Low")');

    await page.waitForTimeout(2000);

    const ratings = await extractRatings(page);
    console.log('Extracted Ratings (High to Low):', ratings);

    expect(ratings).toEqual([...ratings].sort((a, b) => b - a));
  });

  test('should sort reviews from Low to High rating', async ({ page }) => {
    await page.click('button:has-text("All Review")');
    await page.click('li:has-text("Low-High")');

    await page.waitForTimeout(2000);

    const ratings = await extractRatings(page);
    console.log('Extracted Ratings (Low to High):', ratings);

    expect(ratings).toEqual([...ratings].sort((a, b) => a - b));
  });

  test('should default to "All Review" when entering the review page', async ({ page }) => {
    await page.waitForTimeout(2000); // Ensure page is loaded
  
    const selectedOption = await page.textContent('button:has-text("All Review")');
    console.log('Default selected option:', selectedOption);
  
    expect(selectedOption).toContain('All Review');
  });

});
