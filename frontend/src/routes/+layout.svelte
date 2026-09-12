<script>
	import { onMount } from 'svelte';
	import { page } from '$app/state';
	import { session } from '../stores/session.svelte.js';
	import UserMenu from '../components/UserMenu.svelte';
	import notebookUrl from '$lib/images/notebook.webp';
	import brownPaperUrl from '$lib/images/brownPaper.jpg';

	let { children } = $props();

	let bgUrl = $derived(session.isLoggedIn ? brownPaperUrl : notebookUrl);
	let dashboardHref = $derived(session.isAdmin ? '/admin' : '/user');
	let onDashboard = $derived(page.url.pathname === dashboardHref);

	onMount(() => {
		session.refresh();
	});
</script>

<svelte:head>
	<link rel="icon" type="image/webp" href={notebookUrl} />
</svelte:head>

<div class="app-bg" style="background-image: url({bgUrl})">
	<header class="topbar">
		<div class="side">
			{#if session.loaded && session.isLoggedIn && !onDashboard}
				<a class="back" href={dashboardHref}>← Back to Dashboard</a>
			{/if}
		</div>

		<a class="brand" href="/">Task Manager Pro</a>

		<div class="side end">
			{#if session.loaded && session.isLoggedIn}
				<UserMenu />
			{/if}
		</div>
	</header>

	{@render children()}
</div>

<style>
	.app-bg {
		min-height: 100vh;
		background-size: cover;
		background-position: center;
		background-attachment: fixed;
		background-repeat: no-repeat;
	}

	.topbar {
		display: grid;
		grid-template-columns: 1fr auto 1fr;
		align-items: center;
		padding: 14px 24px;
		background: rgba(255, 255, 255, 0.85);
		backdrop-filter: blur(4px);
		border-bottom: 1px solid #eee;
	}

	.side {
		display: flex;
		align-items: center;
	}

	.side.end {
		justify-content: flex-end;
	}

	.back {
		color: #1a56db;
		text-decoration: none;
		font-weight: 600;
		font-size: 0.95rem;
	}

	.back:hover {
		color: #123f9e;
		text-decoration: underline;
	}

	.brand {
		font-weight: 700;
		font-size: 1.1rem;
		text-decoration: none;
		color: #222;
		justify-self: center;
	}
</style>
