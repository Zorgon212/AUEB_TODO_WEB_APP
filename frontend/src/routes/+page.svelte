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
	<p>Loading...</p>
{:else if !session.isLoggedIn}
	<h1>Welcome</h1>

	<p>
		Sign in to manage your todos.
	</p>

	<button onclick={openLogin}>Login</button>
	<button onclick={openRegister}>Register</button>

	{#if showLogin}
		<LoginForm />
	{/if}

	{#if showRegister}
		<RegisterForm />
	{/if}
{/if}
