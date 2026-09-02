<script>
	import { session } from '../stores/session.svelte.js';

	let name = $state('');
	let email = $state('');
	let password = $state('');
	let confirmPassword = $state('');
	let message = $state('');
	let success = $state(false);
	let loading = $state(false);

	async function handleRegister() {
		if (!name || !email || !password || !confirmPassword) {
			message = 'Please fill in all fields.';
			success = false;
			return;
		}

		if (password !== confirmPassword) {
			message = 'Passwords do not match.';
			success = false;
			return;
		}

		loading = true;
		message = '';

		try {
			await session.register(name, email, password);
			message = 'Registration successful! You can now log in.';
			success = true;
		} catch (err) {
			if (err.status === 409) {
				message = 'This email is already being used. Please log in instead.';
			} else {
				message = err.message || 'Registration failed.';
			}
			success = false;
		} finally {
			loading = false;
		}
	}
</script>

<div class="register-container">
	<h2>Create Account</h2>

	<form
		onsubmit={(event) => {
			event.preventDefault();
			handleRegister();
		}}
	>
		<div class="form-group">
			<label for="name">Name</label>
			<input
				id="name"
				type="text"
				bind:value={name}
				placeholder="Enter your name"
			/>
		</div>

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
				placeholder="Create a password"
			/>
		</div>

		<div class="form-group">
			<label for="confirm-password">Confirm Password</label>
			<input
				id="confirm-password"
				type="password"
				bind:value={confirmPassword}
				placeholder="Confirm your password"
			/>
		</div>

		<button type="submit" disabled={loading}>{loading ? 'Creating account...' : 'Register'}</button>
	</form>

	{#if message}
		<p class="message" class:error={!success}>{message}</p>
	{/if}
</div>

<style>
	.register-container {
		width: 350px;
		margin: 2rem auto;
		padding: 2rem;
		border: 1px solid #ccc;
		border-radius: 10px;
		background: white;
		box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
	}

	h2 {
		text-align: center;
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

	.message {
		text-align: center;
		margin-top: 1rem;
		color: #2e7d32;
	}

	.message.error {
		color: #c0392b;
	}
</style>
