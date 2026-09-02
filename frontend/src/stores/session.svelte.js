import { api, login as apiLogin, logout as apiLogout } from '../lib/api.js';

/**
 * @typedef {Object} SessionUser
 * @property {number} id
 * @property {string} fullName
 * @property {string} email
 * @property {'USER' | 'ADMIN' | 'GUEST'} type
 * @property {boolean} status
 */

/** @type {SessionUser | null} */
let user = $state(null);
let loaded = $state(false);

export const session = {
	get user() {
		return user;
	},
	get loaded() {
		return loaded;
	},
	get isLoggedIn() {
		return user !== null;
	},
	get isAdmin() {
		return user !== null && user.type === 'ADMIN';
	},

	// pull the current user from the backend session cookie, if any
	async refresh() {
		try {
			user = await api.get('/me');
		} catch {
			user = null;
		} finally {
			loaded = true;
		}
	},

	/**
	 * @param {string} email
	 * @param {string} password
	 */
	async login(email, password) {
		await apiLogin(email, password);
		await session.refresh();
	},

	/**
	 * @param {string} fullName
	 * @param {string} email
	 * @param {string} password
	 */
	async register(fullName, email, password) {
		await api.post('/register', { fullName, email, password });
	},

	async logout() {
		await apiLogout();
		user = null;
	}
};
