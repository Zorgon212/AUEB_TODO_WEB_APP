// Small fetch wrapper for talking to the Spring backend.
// Requests go through the Vite dev proxy (see vite.config.js) so the session
// cookie set by Spring Security stays same-origin - no CORS needed in dev.

async function request(path, options = {}) {
	const res = await fetch(path, {
		credentials: 'include',
		headers: options.body ? { 'Content-Type': 'application/json', ...options.headers } : options.headers,
		...options
	});

	if (res.status === 204) return null;

	const text = await res.text();
	let data = null;

	if (text) {
		try {
			data = JSON.parse(text);
		} catch {
			data = text;
		}
	}

	if (!res.ok) {
		const error = new Error((data && data.message) || res.statusText || 'Request failed');
		error.status = res.status;
		error.data = data;
		throw error;
	}

	return data;
}

export const api = {
	get: (path) => request(path),
	post: (path, body) => request(path, { method: 'POST', body: body !== undefined ? JSON.stringify(body) : undefined }),
	put: (path, body) => request(path, { method: 'PUT', body: body !== undefined ? JSON.stringify(body) : undefined }),
	del: (path) => request(path, { method: 'DELETE' })
};

// Spring Security's default form login reads "username"/"password" as
// application/x-www-form-urlencoded parameters, not JSON.
export async function login(email, password) {
	const form = new URLSearchParams();
	form.set('username', email);
	form.set('password', password);

	const res = await fetch('/login', {
		method: 'POST',
		credentials: 'include',
		headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
		body: form
	});

	if (!res.ok) {
		let message = 'Invalid email or password.';
		try {
			const data = await res.json();
			if (data && data.message) message = data.message;
		} catch {
			// ignore - keep default message
		}
		const error = new Error(message);
		error.status = res.status;
		throw error;
	}
}

export async function logout() {
	await fetch('/logout', { method: 'POST', credentials: 'include' });
}
