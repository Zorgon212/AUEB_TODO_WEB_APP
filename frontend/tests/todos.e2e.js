import { test, expect } from '@playwright/test';

// Registers and logs in a fresh throwaway user directly via the API (fast,
// and keeps each test's todos isolated from every other test's) rather than
// re-filling the register/login forms - that flow already has its own
// dedicated coverage in auth.e2e.js.
async function registerAndLogIn(page) {
	const email = `e2e-todo-${Date.now()}-${Math.floor(Math.random() * 10000)}@example.com`;
	const password = 'Password123!';

	const registerRes = await page.request.post('/register', {
		data: { fullName: 'E2E Todo User', email, password }
	});
	if (!registerRes.ok()) throw new Error(`setup: register failed (${registerRes.status()})`);

	const loginRes = await page.request.post('/login', {
		form: { username: email, password }
	});
	if (!loginRes.ok()) throw new Error(`setup: login failed (${loginRes.status()})`);
}

test.describe('Todo management', () => {
	test.beforeEach(async ({ page }) => {
		await registerAndLogIn(page);
		await page.goto('/user/todos');
	});

	test('adding a todo shows it in the list', async ({ page }) => {
		const description = `Buy milk ${Date.now()}`;

		await page.getByPlaceholder('Add a new todo...').fill(description);
		await page.getByRole('button', { name: 'Add' }).click();

		await expect(page.getByText(description)).toBeVisible();
	});

	test('checking a todo marks it as completed', async ({ page }) => {
		const description = `Walk the dog ${Date.now()}`;

		await page.getByPlaceholder('Add a new todo...').fill(description);
		await page.getByRole('button', { name: 'Add' }).click();

		const item = page.locator('li', { hasText: description });
		await item.getByRole('checkbox').check();

		await expect(item.getByText(description)).toHaveClass(/completed/);
	});

	test('deleting a todo removes it from the list', async ({ page }) => {
		const description = `Temporary todo ${Date.now()}`;

		await page.getByPlaceholder('Add a new todo...').fill(description);
		await page.getByRole('button', { name: 'Add' }).click();
		await expect(page.getByText(description)).toBeVisible();

		const item = page.locator('li', { hasText: description });
		await item.getByRole('button', { name: 'Delete' }).click();

		await expect(page.getByText(description)).not.toBeVisible();
	});
});
