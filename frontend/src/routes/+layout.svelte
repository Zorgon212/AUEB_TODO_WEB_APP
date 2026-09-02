<script>
	import { onMount } from 'svelte';
	import { session } from '../stores/session.svelte.js';
	import UserMenu from '../components/UserMenu.svelte';

	let { children } = $props();

	onMount(() => {
		session.refresh();
	});
</script>

<header class="topbar">
	<a class="brand" href="/">Todo App</a>

	{#if session.loaded && session.isLoggedIn}
		<UserMenu />
	{/if}
</header>

{@render children()}

<style>
	.topbar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 14px 24px;
		border-bottom: 1px solid #eee;
	}

	.brand {
		font-weight: 700;
		text-decoration: none;
		color: #222;
	}
</style>
