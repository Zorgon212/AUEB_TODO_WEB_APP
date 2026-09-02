<script>
	import { page } from '$app/state';
	import { api } from '../../../../lib/api.js';
	import TodoList from '../../../../components/TodoList.svelte';

	const userId = $derived(Number(page.params.userId));

	let user = $state(null);
	let error = $state('');

	$effect(() => {
		const id = userId;
		error = '';
		user = null;

		api.get(`/users/${id}`)
			.then((u) => (user = u))
			.catch((err) => (error = err.message || 'Could not load user.'));
	});
</script>

<a href="/admin/users" class="back">← Back to Users</a>

{#if error}
	<p class="error">{error}</p>
{:else if user}
	<h1>{user.fullName}'s Todos</h1>
	<p class="email">{user.email}</p>

	<TodoList userId={user.id} detailBase="/admin/todos" />
{:else}
	<p>Loading...</p>
{/if}

<style>
	.back {
		display: inline-block;
		margin: 1rem 0 0 1rem;
		color: #555;
		text-decoration: none;
	}

	.back:hover {
		color: #000;
	}

	.email {
		color: #777;
		margin-left: 0;
		text-align: center;
	}

	h1 {
		text-align: center;
	}

	.error {
		color: #c0392b;
		text-align: center;
	}
</style>
