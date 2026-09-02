<script>
	import { onMount } from 'svelte';
	import { session } from '../stores/session.svelte.js';
	import UserMenu from '../components/UserMenu.svelte';
	import notebookUrl from '$lib/images/notebook.webp';

	let { children } = $props();

	onMount(() => {
		session.refresh();
	});
</script>

<svelte:head>
	<link rel="icon" type="image/webp" href={notebookUrl} />
</svelte:head>

<div class="app-bg" style="background-image: url({notebookUrl})">
	<header class="topbar">
		<div class="side"></div>

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

	.brand {
		font-weight: 700;
		font-size: 1.1rem;
		text-decoration: none;
		color: #222;
		justify-self: center;
	}
</style>
