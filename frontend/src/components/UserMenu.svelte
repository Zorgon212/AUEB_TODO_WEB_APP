<script>
	import { goto } from '$app/navigation';
	import { session } from '../stores/session.svelte.js';

	let open = $state(false);
	let container = $state(null);

	function toggle() {
		open = !open;
	}

	function handleWindowClick(event) {
		if (open && container && !container.contains(event.target)) {
			open = false;
		}
	}

	async function handleLogout() {
		open = false;
		await session.logout();
		goto('/');
	}

	function goHome() {
		open = false;
		goto('/');
	}
</script>

<svelte:window onclick={handleWindowClick} />

<div class="user-menu" bind:this={container}>
	<span class="name">{session.user?.fullName}</span>

	<button class="dots" onclick={toggle} aria-label="Open menu">⋮</button>

	{#if open}
		<div class="menu">
			<button onclick={goHome}>Home</button>
			<button onclick={handleLogout}>Logout</button>
		</div>
	{/if}
</div>

<style>
	.user-menu {
		position: relative;
		display: flex;
		align-items: center;
		gap: 8px;
	}

	.name {
		font-weight: 600;
		color: #222;
	}

	.dots {
		background: none;
		border: none;
		font-size: 20px;
		line-height: 1;
		cursor: pointer;
		padding: 4px 8px;
		border-radius: 6px;
		color: #333;
	}

	.dots:hover {
		background: #eee;
	}

	.menu {
		position: absolute;
		top: 100%;
		right: 0;
		margin-top: 6px;
		background: white;
		border: 1px solid #ddd;
		border-radius: 8px;
		box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
		overflow: hidden;
		z-index: 10;
		min-width: 130px;
	}

	.menu button {
		display: block;
		width: 100%;
		text-align: left;
		padding: 10px 14px;
		border: none;
		background: white;
		cursor: pointer;
		font-size: 14px;
	}

	.menu button:hover {
		background: #f5f5f5;
	}
</style>
