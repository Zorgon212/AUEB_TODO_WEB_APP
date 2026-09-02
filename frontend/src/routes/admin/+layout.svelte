<script>
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { session } from '../../stores/session.svelte.js';

	let { children } = $props();

	onMount(async () => {
		if (!session.loaded) await session.refresh();

		if (!session.isLoggedIn) {
			goto('/');
		} else if (!session.isAdmin) {
			goto('/user');
		}
	});
</script>

{#if session.isAdmin}
	{@render children()}
{/if}
