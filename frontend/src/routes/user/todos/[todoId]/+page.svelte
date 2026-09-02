<script>
	import { page } from '$app/state';
	import { goto } from '$app/navigation';
	import { api } from '../../../../lib/api.js';

	const todoId = $derived(Number(page.params.todoId));

	let todo = $state(null);
	let description = $state('');
	let completed = $state(false);
	let error = $state('');
	let saving = $state(false);

	$effect(() => {
		const id = todoId;
		error = '';

		api.get(`/users/tasks/${id}`)
			.then((t) => {
				todo = t;
				description = t.description;
				completed = t.status;
			})
			.catch((err) => (error = err.message || 'Could not load todo.'));
	});

	async function save() {
		saving = true;
		error = '';

		try {
			await api.put(`/users/tasks/${todo.id}`, {
				...todo,
				description,
				status: completed
			});
			goto('/user/todos');
		} catch (err) {
			error = err.message || 'Could not save todo.';
		} finally {
			saving = false;
		}
	}

	async function remove() {
		error = '';
		try {
			await api.del(`/users/tasks/${todo.id}`);
			goto('/user/todos');
		} catch (err) {
			error = err.message || 'Could not delete todo.';
		}
	}
</script>

<a href="/user/todos" class="back">← Back to Todos</a>

<div class="page">
	{#if error}
		<p class="error">{error}</p>
	{/if}

	{#if todo}
		<h1>Edit Todo</h1>

		<div class="card">
			<label>
				Description
				<textarea bind:value={description} rows="4"></textarea>
			</label>

			<label class="checkbox">
				<input type="checkbox" bind:checked={completed} />
				Completed
			</label>

			<div class="dates">
				<p>Declared: {todo.creationDT ? new Date(todo.creationDT).toLocaleString() : '—'}</p>
				<p>Completed: {todo.completionDT ? new Date(todo.completionDT).toLocaleString() : '—'}</p>
			</div>

			<div class="actions">
				<button class="save" onclick={save} disabled={saving}>
					{saving ? 'Saving...' : 'Save'}
				</button>
				<button class="delete" onclick={remove}>Delete</button>
			</div>
		</div>
	{:else if !error}
		<p>Loading...</p>
	{/if}
</div>

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

	.page {
		max-width: 600px;
		margin: 1.5rem auto;
		padding: 0 20px;
	}

	.card {
		padding: 24px;
		background: white;
		border: 1px solid #e5e5e5;
		border-radius: 12px;
		box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
		display: flex;
		flex-direction: column;
		gap: 18px;
	}

	label {
		display: flex;
		flex-direction: column;
		gap: 6px;
		font-weight: 600;
		font-size: 14px;
		color: #555;
	}

	textarea {
		font-family: inherit;
		font-size: 15px;
		padding: 10px;
		border: 1px solid #aaa;
		border-radius: 6px;
		resize: vertical;
	}

	label.checkbox {
		flex-direction: row;
		align-items: center;
		gap: 8px;
	}

	.dates {
		color: #777;
		font-size: 14px;
	}

	.dates p {
		margin: 4px 0;
	}

	.actions {
		display: flex;
		gap: 10px;
		justify-content: flex-end;
	}

	button {
		padding: 10px 16px;
		border: none;
		border-radius: 7px;
		cursor: pointer;
		font-size: 14px;
	}

	.save {
		background: #333;
		color: white;
	}

	.save:hover {
		background: #555;
	}

	.save:disabled {
		opacity: 0.6;
		cursor: default;
	}

	.delete {
		background: #dc3545;
		color: white;
	}

	.error {
		color: #c0392b;
		text-align: center;
	}
</style>
