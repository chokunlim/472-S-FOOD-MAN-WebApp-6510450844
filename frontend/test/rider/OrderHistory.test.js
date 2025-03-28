import { test, expect } from '@playwright/test';

test.beforeEach(async ({ page }) => {
    console.log("Opening login page...");
    await page.goto('http://localhost:5173/signin');

    await page.fill('input[placeholder="Username..."]', 'rider'); 
    await page.fill('input[placeholder="Password..."]', '11111111'); 
    await page.click('button:has-text("Sign In")');

    console.log("Login successful, navigating to order history page...");
    await page.waitForTimeout(2000); 
    await page.goto('http://localhost:5173/orderhistory');
});

test('Rider should be able to access the order history page', async ({ page }) => {
    await expect(page).toHaveURL('http://localhost:5173/orderhistory');
    await expect(page.locator('span.font-bold.text-3xl')).toHaveText('Order');
});

test('Order history should display only DELIVERED orders', async ({ page }) => {
    const orders = await page.locator('.order-card');
    const orderCount = await orders.count();

    for (let i = 0; i < orderCount; i++) {
        const status = await orders.nth(i).locator('.status').textContent();
        expect(status).toBe('DELIVERED');
    }
});

test('Rider cannot change status into SUCCESS', async ({ page }) => {
    const changeStatusButtons = await page.locator('button:has-text("Mark as Success")');
    await expect(changeStatusButtons).toHaveCount(0);
});