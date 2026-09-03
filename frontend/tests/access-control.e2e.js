import { test, expect } from '@playwright/test';

test('a logged-out visitor only sees Login/Register, not the account menu', async ({ page }) => {
	await page.goto('/');

	await expect(page.locator('.actions').getByRole('button', { name: 'Login' })).toBeVisible();
	await expect(page.locator('.actions').getByRole('button', { name: 'Register' })).toBeVisible();
	await expect(page.locator('.user-menu')).toHaveCount(0);
});

test('the default admin can log in and reach the admin dashboard', async ({ page }) => {
	await page.goto('/');
	await page.locator('.actions').getByRole('button', { name: 'Login' }).click();
	await page.locator('#email').fill('admin@todoApp.gr');
	await page.locator('#password').fill('P@ssw0rd');
	await page.locator('.login-container').getByRole('button', { name: 'Login' }).click();

	await expect(page).toHaveURL(/\/admin$/);
	await expect(page.getByRole('link', { name: /Users/ })).toBeVisible();
});

test('a regular user is redirected away from /admin', async ({ page }) => {
	const email = `e2e-guard-${Date.now()}@example.com`;
	const password = 'Password123!';

	await page.request.post('/register', {
		data: { fullName: 'E2E Guard User', email, password }
	});
	await page.request.post('/login', { form: { username: email, password } });

	await page.goto('/admin');

	await expect(page).toHaveURL(/\/user$/);
});
