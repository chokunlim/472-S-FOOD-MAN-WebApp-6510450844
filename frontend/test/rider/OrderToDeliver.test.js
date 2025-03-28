import { test, expect } from '@playwright/test';

test.beforeEach(async ({ page }) => {
    console.log("Opening login page...");
    await page.goto('http://localhost:5173/signin');

    await page.fill('input[placeholder="Username..."]', 'rider'); 
    await page.fill('input[placeholder="Password..."]', '11111111'); 
    await page.click('button:has-text("Sign In")');

    console.log("Login successful, navigating to rider order to deliver page...");
    await page.waitForTimeout(2000); 
    await page.goto('http://localhost:5173/orderforrider');
  });

test('Rider should see only PAID and DELIVERING orders', async ({ page }) => {

    const orders = await page.locator('.order-card');
    const orderCount = await orders.count();

    for (let i = 0; i < orderCount; i++) {
        const status = await orders.nth(i).locator('.status').textContent();
        expect(['PAID', 'DELIVERING']).toContain(status);
    }
});

test('Rider should only click Mark as Delivering once per order', async ({ page }) => {

    const deliveringButton = page.locator('button', { hasText: 'Mark as Delivering' });
    if (await deliveringButton.count() > 0) {
        await deliveringButton.first().click();
        await expect(deliveringButton.first()).not.toBeVisible();
        await expect(page.locator('button', { hasText: 'Mark as Delivered' })).toBeVisible();
    }
});

test('Order status should update to DELIVERING after clicking Mark as Delivering', async ({ page }) => {

    const orderCard = page.locator('.order-card').first();
    const markAsDeliveringButton = orderCard.locator('button', { hasText: 'Mark as Delivering' });
    
    if (await markAsDeliveringButton.count() > 0) {
        await markAsDeliveringButton.click();
        await expect(orderCard.locator('.status')).toHaveText('DELIVERING');
    }
});