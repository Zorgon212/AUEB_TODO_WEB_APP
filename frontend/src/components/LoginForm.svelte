<script>
	import { goto } from '$app/navigation';
	import { session } from '../stores/session.svelte.js';

	let email = $state('');
	let password = $state('');
	let message = $state('');
	let loading = $state(false);

	async function handleLogin() {
		if (!email || !password) {
			message = 'Please enter your email and password.';
			return;
		}

		loading = true;
		message = '';

		try {
			await session.login(email, password);
			await goto(session.isAdmin ? '/admin' : '/user');
		} catch (err) {
			message = err.message || 'Login failed.';
		} finally {
			loading = false;
		}
	}
</script>


<div class="login-container">
	<h2>Login</h2>

	<form onsubmit={(event) => {
		event.preventDefault();
		handleLogin();
	}}>
		<div class="form-group">
			<label for="email">Email</label>
			<input
				id="email"
				type="email"
				bind:value={email}
				placeholder="Enter your email"
			/>
		</div>

		<div class="form-group">
			<label for="password">Password</label>
			<input
				id="password"
				type="password"
				bind:value={password}
				placeholder="Enter your password"
			/>
		</div>

		<button type="submit" disabled={loading}>{loading ? 'Logging in...' : 'Login'}</button>
	</form>

	{#if message}
		<p class="message">{message}</p>
	{/if}
</div>

<style>
	.login-container {
		width: 350px;
		margin: 2rem auto;
		padding: 2rem;
		border: 1px solid #ccc;
		border-radius: 10px;
		background: white;
		box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
	}

	h2 {
		margin-bottom: 1.5rem;
	}

	.form-group {
		margin-bottom: 1rem;
	}

	label {
		display: block;
		margin-bottom: 0.4rem;
	}

	input {
		width: 100%;
		padding: 0.6rem;
		box-sizing: border-box;
		border: 1px solid #aaa;
		border-radius: 5px;
	}

	button {
		width: 100%;
		padding: 0.7rem;
		border: none;
		border-radius: 5px;
		background: #333;
		color: white;
		cursor: pointer;
	}

	button:hover {
		background: #555;
	}

	button:disabled {
		opacity: 0.6;
		cursor: default;
	}

	p.message {
		margin-top: 1rem;
		color: #c0392b;
	}
</style>
