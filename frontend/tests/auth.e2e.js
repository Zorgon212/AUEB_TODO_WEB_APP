import { test, expect } from '@playwright/test';

function uniqueEmail(prefix) {
	return `${prefix}-${Date.now()}-${Math.floor(Math.random() * 10000)}@example.com`;
}

async function fillAndSubmitRegister(page, { name, email, password }) {
	await page.locator('#name').fill(name);
	await page.locator('#email').fill(email);
	await page.locator('#password').fill(password);
	await page.locator('#confirm-password').fill(password);
	await page.locator('.register-container').getByRole('button', { name: 'Register' }).click();
}

test.describe('Registration', () => {
	test('registering with a new email succeeds', async ({ page }) => {
		await page.goto('/');
		await page.locator('.actions').getByRole('button', { name: 'Register' }).click();

		await fillAndSubmitRegister(page, {
			name: 'E2E Test User',
			email: uniqueEmail('e2e-register'),
			password: 'Password123!'
		});

		await expect(page.getByText('Registration successful')).toBeVisible();
	});

	test('registering with an email already in use shows an error', async ({ page }) => {
		const email = uniqueEmail('e2e-dupe');

		await page.goto('/');
		await page.locator('.actions').getByRole('button', { name: 'Register' }).click();
		await fillAndSubmitRegister(page, { name: 'E2E Test User', email, password: 'Password123!' });
		await expect(page.getByText('Registration successful')).toBeVisible();

		await fillAndSubmitRegister(page, { name: 'E2E Test User', email, password: 'Password123!' });

		await expect(page.getByText('already being used')).toBeVisible();
	});
});

test.describe('Login', () => {
	test('logging in with the wrong password shows an error', async ({ page }) => {
		await page.goto('/');
		await page.locator('.actions').getByRole('button', { name: 'Login' }).click();

		await page.locator('#email').fill('nobody-e2e@example.com');
		await page.locator('#password').fill('wrong-password');
		await page.locator('.login-container').getByRole('button', { name: 'Login' }).click();

		await expect(page.getByText(/invalid email or password/i)).toBeVisible();
	});

	test('logging in with valid credentials redirects to the user dashboard', async ({ page }) => {
		const email = uniqueEmail('e2e-login');
		const password = 'Password123!';

		await page.goto('/');
		await page.locator('.actions').getByRole('button', { name: 'Register' }).click();
		await fillAndSubmitRegister(page, { name: 'E2E Test User', email, password });
		await expect(page.getByText('Registration successful')).toBeVisible();

		await page.locator('.actions').getByRole('button', { name: 'Login' }).click();
		await page.locator('#email').fill(email);
		await page.locator('#password').fill(password);
		await page.locator('.login-container').getByRole('button', { name: 'Login' }).click();

		await expect(page).toHaveURL(/\/user$/);
		await expect(page.getByRole('heading', { name: 'My Todos' })).toBeVisible();
	});
});
