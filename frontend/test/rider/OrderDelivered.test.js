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


test('Rider should see only DELIVERED orders', async ({ page }) => {

    const orders = await page.locator('.order-card');
    const orderCount = await orders.count();

    for (let i = 0; i < orderCount; i++) {
        const status = await orders.nth(i).locator('.status').textContent();
        expect('DELIVERED').toContain(status);
    }
});


test('Rider should only click Mark as Delivered once per order', async ({ page }) => {

    const deliveredButton = page.locator('button', { hasText: 'Mark as Delivered' });

    if (await deliveredButton.count() > 0) {
        await deliveredButton.first().click();
        await expect(deliveredButton.first()).not.toBeVisible();
        await expect(page.locator('button', { hasText: 'Mark as Delivered' })).toBeVisible();
    }
});

test('When Rider updates status, system should update order to DELIVERED', async ({ page }) => {
    const orderCard = page.locator('.order-card');
    const orderCount = await orderCard.count();

    if (orderCount > 0) {
        const firstOrder = orderCard.first();
        const deliveredButton = firstOrder.locator('button:has-text("Mark as Delivered")');

        if (await deliveredButton.count() > 0) {
            await deliveredButton.click();
            await expect(firstOrder.locator('.status')).toHaveText('DELIVERED');
        }
    }
});