<script>
	import { goto } from '$app/navigation';
	import { api } from '../lib/api.js';
	import { session } from '../stores/session.svelte.js';

	// userId: whose todos to show - defaults to the logged-in user's own.
	// detailBase: where clicking a todo navigates to (differs for /admin vs /user).
	let { userId = null, detailBase = '/user/todos' } = $props();

	const targetUserId = $derived(userId ?? session.user?.id);

	let todos = $state([]);
	let newDescription = $state('');
	let loading = $state(true);
	let error = $state('');

	async function loadTodos(id) {
		loading = true;
		error = '';
		try {
			todos = await api.get(`/users/${id}/tasks`);
		} catch (err) {
			error = err.message || 'Could not load todos.';
		} finally {
			loading = false;
		}
	}

	$effect(() => {
		if (targetUserId) loadTodos(targetUserId);
	});

	async function addTodo() {
		if (newDescription.trim() === '' || !targetUserId) return;

		try {
			const created = await api.post(`/users/${targetUserId}/tasks`, {
				description: newDescription
			});
			todos = [...todos, created];
			newDescription = '';
		} catch (err) {
			error = err.message || 'Could not create todo.';
		}
	}

	async function toggleTodo(todo) {
		try {
			const updated = await api.put(`/users/tasks/${todo.id}`, {
				...todo,
				status: !todo.status
			});
			todos = todos.map((t) => (t.id === updated.id ? updated : t));
		} catch (err) {
			error = err.message || 'Could not update todo.';
		}
	}

	async function deleteTodo(id) {
		try {
			await api.del(`/users/tasks/${id}`);
			todos = todos.filter((t) => t.id !== id);
		} catch (err) {
			error = err.message || 'Could not delete todo.';
		}
	}

	function openTodo(id) {
		goto(`${detailBase}/${id}`);
	}
</script>

<div class="todo-container">
	<h2>Todo List</h2>

	<form
		onsubmit={(event) => {
			event.preventDefault();
			addTodo();
		}}
	>
		<input
			type="text"
			bind:value={newDescription}
			placeholder="Add a new todo..."
		/>

		<button type="submit">Add</button>
	</form>

	{#if error}
		<p class="error">{error}</p>
	{/if}

	{#if loading}
		<p>Loading...</p>
	{:else if todos.length === 0}
		<p>No todos yet.</p>
	{:else}
		<ul>
			{#each todos as todo (todo.id)}
				<li>
					<input
						type="checkbox"
						checked={todo.status}
						onchange={() => toggleTodo(todo)}
					/>

					<button
						type="button"
						class="title"
						class:completed={todo.status}
						onclick={() => openTodo(todo.id)}
					>
						{todo.description}
					</button>

					<button
						type="button"
						class="delete"
						onclick={() => deleteTodo(todo.id)}
					>
						Delete
					</button>
				</li>
			{/each}
		</ul>
	{/if}
</div>

<style>
	.todo-container {
		max-width: 500px;
		margin: 2rem auto;
		padding: 2rem;
		border: 1px solid #ccc;
		border-radius: 10px;
		background: white;
	}

	h2 {
		margin-top: 0;
	}

	form {
		display: flex;
		gap: 10px;
		margin-bottom: 1.5rem;
	}

	form input {
		flex: 1;
		padding: 0.7rem;
		border: 1px solid #aaa;
		border-radius: 5px;
	}

	form button {
		padding: 0.7rem 1rem;
		border: none;
		border-radius: 5px;
		background: #333;
		color: white;
		cursor: pointer;
	}

	ul {
		padding: 0;
		margin: 0;
		list-style: none;
	}

	li {
		display: flex;
		align-items: center;
		gap: 10px;
		padding: 10px 0;
		border-bottom: 1px solid #eee;
	}

	.title {
		flex: 1;
		text-align: left;
		background: none;
		border: none;
		cursor: pointer;
		font-size: 1rem;
		padding: 0;
	}

	.title:hover {
		text-decoration: underline;
	}

	.completed {
		text-decoration: line-through;
		color: #888;
	}

	.delete {
		border: none;
		background: #dc3545;
		color: white;
		padding: 5px 10px;
		border-radius: 4px;
		cursor: pointer;
	}

	.error {
		color: #c0392b;
	}
</style>
