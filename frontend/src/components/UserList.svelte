<script>
	import { onMount } from 'svelte';
	import { api } from '../lib/api.js';

	let users = $state([]);
	let loading = $state(true);
	let error = $state('');

	onMount(async () => {
		try {
			users = await api.get('/users');
		} catch (err) {
			error = err.message || 'Could not load users.';
		} finally {
			loading = false;
		}
	});
</script>

<div class="user-list">
	{#if error}
		<p class="error">{error}</p>
	{/if}

	{#if loading}
		<p>Loading...</p>
	{:else if users.length === 0}
		<p>No users found.</p>
	{:else}
		<ul>
			{#each users as user (user.id)}
				<li>
					<a href={`/admin/users/${user.id}`}>
						<span class="name">{user.fullName}</span>
						<span class="email">{user.email}</span>
						<span class="role" class:admin={user.type === 'ADMIN'}>{user.type}</span>
					</a>
				</li>
			{/each}
		</ul>
	{/if}
</div>

<style>
	.user-list {
		max-width: 700px;
		margin: 2rem auto;
	}

	ul {
		padding: 0;
		margin: 0;
		list-style: none;
		display: flex;
		flex-direction: column;
		gap: 10px;
	}

	li a {
		display: flex;
		align-items: center;
		gap: 16px;
		padding: 16px 20px;
		background: white;
		border: 1px solid #e5e5e5;
		border-radius: 10px;
		text-decoration: none;
		color: #222;
		box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
	}

	li a:hover {
		border-color: #333;
	}

	.name {
		font-weight: 600;
		flex: 1;
	}

	.email {
		color: #777;
		flex: 1;
	}

	.role {
		padding: 4px 10px;
		border-radius: 20px;
		background: #eee;
		color: #333;
		font-size: 12px;
	}

	.role.admin {
		background: #333;
		color: white;
	}

	.error {
		color: #c0392b;
	}
</style>
