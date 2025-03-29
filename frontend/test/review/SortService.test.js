import { test, expect } from '@playwright/test';

test.describe('Review Sorting Functionality', () => {
  
  test.beforeEach(async ({ page }) => {
    console.log("Mocking authentication for RIDER user...");
  
    // Step 1: Create a mock JWT payload
    const fakeTokenPayload = {
      user_id: "f7c095e6-3b82-4f32-842e-897f982101e9",
      email: "test123@example.com",
      phone: "0999999999",
      user_role: "RIDER",  
      username: "rider",
      exp: Math.floor(Date.now() / 1000) + 3600 // Token valid for 1 hour
    };
  
    // Step 2: Convert payload to Base64 (simulate JWT token)
    const base64UrlEncode = (obj) => Buffer.from(JSON.stringify(obj)).toString('base64');
    const fakeToken = `header.${base64UrlEncode(fakeTokenPayload)}.signature`;
  
    // Step 3: Store in localStorage
    await page.addInitScript((token) => {
      localStorage.setItem('authToken', token); 
    }, fakeToken);
  
    console.log("Authentication mocked, setting up API responses...");
  
    // Step 4: Mock API response for reviews
    await page.route('**/api/reviews', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            review_id: "3e7d3636-9fce-4726-8508-7c1d408d5a8c",
            comment: "This product is amazing!",
            review_date: "2025-03-28 22:11:55",
            rating: 5,
            order_id: "a64b63d9-e14a-47ff-94d0-13ab88a2a952",
            customer_id: "f7c095e6-3b82-4f32-842e-897f982101e9"
          }
        ])
      });
    });
  
    // Step 5: Mock API response for customer details
    await page.route('**/api/customers/f7c095e6-3b82-4f32-842e-897f982101e9', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          customer_id: "f7c095e6-3b82-4f32-842e-897f982101e9",
          name: "John Doe",
          email: "johndoe@example.com"
        })
      });
    });
  
    // Step 6: Mock API response for order details
    await page.route('**/api/orders/a64b63d9-e14a-47ff-94d0-13ab88a2a952', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          order_id: "a64b63d9-e14a-47ff-94d0-13ab88a2a952",
          product: "Wireless Headphones",
          price: 99.99
        })
      });
    });
  
    console.log("Mocks ready, navigating to review page...");
    await page.goto('http://localhost:5173/reviewlistview'); // Navigating only once
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
