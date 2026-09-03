import { defineConfig } from '@playwright/test';

// The dev server (not the production preview build) is used here on purpose -
// only `vite dev` applies the /login,/logout,/register,/me,/users,/clients
// proxy rules in vite.config.js that keep the session cookie same-origin.
// The backend (and its database) must already be running on localhost:8080
// before these tests are run - see TESTING.txt in the project root.
export default defineConfig({
	testDir: './tests',
	testMatch: '**/*.e2e.{ts,js}',
	timeout: 30_000,
	webServer: {
		command: 'npm run dev -- --port 4173 --strictPort',
		port: 4173,
		reuseExistingServer: !process.env.CI
	},
	use: {
		baseURL: 'http://localhost:4173'
	}
});
