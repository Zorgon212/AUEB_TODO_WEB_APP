<script>
	import { goto } from '$app/navigation';
	import LoginForm from '../components/LoginForm.svelte';
	import RegisterForm from '../components/RegisterForm.svelte';
	import { session } from '../stores/session.svelte.js';

	let showLogin = $state(false);
	let showRegister = $state(false);

	function openLogin() {
		showLogin = true;
		showRegister = false;
	}

	function openRegister() {
		showRegister = true;
		showLogin = false;
	}

	// once we know who's logged in, send them straight to their dashboard -
	// login/register only make sense for a signed-out visitor
	$effect(() => {
		if (session.loaded && session.isLoggedIn) {
			goto(session.isAdmin ? '/admin' : '/user');
		}
	});
</script>

{#if !session.loaded}
	<p class="status">Loading...</p>
{:else if !session.isLoggedIn}
	<div class="welcome">
		<h1>Welcome</h1>

		<p>
			Sign in to manage your todos.
		</p>

		<div class="actions">
			<button onclick={openLogin}>Login</button>
			<button onclick={openRegister}>Register</button>
		</div>

		{#if showLogin}
			<LoginForm />
		{/if}

		{#if showRegister}
			<RegisterForm />
		{/if}
	</div>
{/if}

<style>
	.status {
		text-align: center;
		margin-top: 3rem;
	}

	.welcome {
		max-width: 420px;
		margin: 4rem auto 2rem;
		padding: 2.5rem 2rem;
		text-align: center;
		background: rgba(255, 255, 255, 0.9);
		border-radius: 14px;
		box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
	}

	.welcome h1 {
		margin-top: 0;
	}

	.actions {
		display: flex;
		justify-content: center;
		gap: 10px;
		margin-top: 1.25rem;
	}

	.actions button {
		padding: 0.6rem 1.4rem;
		border: none;
		border-radius: 6px;
		background: #333;
		color: white;
		cursor: pointer;
	}

	.actions button:hover {
		background: #555;
	}
</style>
