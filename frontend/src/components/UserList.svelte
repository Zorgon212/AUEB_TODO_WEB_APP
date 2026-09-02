<script>
	import { onMount } from 'svelte';
	import { api } from '../lib/api.js';

	let users = $state([]);
	let loading = $state(true);
	let error = $state('');

	let showCreate = $state(false);
	let newName = $state('');
	let newEmail = $state('');
	let newPassword = $state('');
	let newRole = $state('USER');
	let creating = $state(false);
	let createError = $state('');

	// admin "edit info" for another user - inline in the row
	let editingId = $state(null);
	let editName = $state('');
	let editEmail = $state('');
	let editRole = $state('USER');
	let editPassword = $state('');
	let saving = $state(false);
	let editError = $state('');

	async function loadUsers() {
		loading = true;
		error = '';
		try {
			users = await api.get('/users');
		} catch (err) {
			error = err.message || 'Could not load users.';
		} finally {
			loading = false;
		}
	}

	onMount(loadUsers);

	async function createUser() {
		if (!newName || !newEmail || !newPassword) {
			createError = 'Please fill in all fields.';
			return;
		}

		creating = true;
		createError = '';

		try {
			await api.post('/users', {
				fullName: newName,
				email: newEmail,
				password: newPassword,
				type: newRole
			});
			newName = '';
			newEmail = '';
			newPassword = '';
			newRole = 'USER';
			showCreate = false;
			await loadUsers();
		} catch (err) {
			createError =
				err.status === 409
					? 'This email is already in use.'
					: err.message || 'Could not create user.';
		} finally {
			creating = false;
		}
	}

	async function deleteUser(event, id) {
		event.preventDefault();
		event.stopPropagation();

		if (!confirm('Delete this user? All of their todos will be deleted too.')) {
			return;
		}

		try {
			await api.del(`/users/${id}`);
			users = users.filter((u) => u.id !== id);
		} catch (err) {
			error = err.message || 'Could not delete user.';
		}
	}

	function startEdit(event, user) {
		event.preventDefault();
		event.stopPropagation();

		editingId = user.id;
		editName = user.fullName;
		editEmail = user.email;
		editRole = user.type;
		editPassword = '';
		editError = '';
	}

	function cancelEdit(event) {
		event?.preventDefault();
		event?.stopPropagation();
		editingId = null;
		editError = '';
	}

	async function saveEdit(event, user) {
		event.preventDefault();
		event.stopPropagation();

		saving = true;
		editError = '';

		try {
			const updated = await api.put(`/users/${user.id}`, {
				...user,
				fullName: editName,
				email: editEmail,
				type: editRole,
				password: editPassword || undefined
			});
			users = users.map((u) => (u.id === user.id ? updated : u));
			editingId = null;
		} catch (err) {
			editError =
				err.status === 409
					? 'This email is already in use.'
					: err.message || 'Could not save changes.';
		} finally {
			saving = false;
		}
	}
</script>

<div class="user-list">
	<div class="toolbar">
		<button type="button" onclick={() => (showCreate = !showCreate)}>
			{showCreate ? 'Cancel' : '+ Create user'}
		</button>
	</div>

	{#if showCreate}
		<form
			class="create-form"
			onsubmit={(event) => {
				event.preventDefault();
				createUser();
			}}
		>
			<input type="text" bind:value={newName} placeholder="Full name" />
			<input type="email" bind:value={newEmail} placeholder="Email" />
			<input type="password" bind:value={newPassword} placeholder="Password" />

			<select bind:value={newRole}>
				<option value="USER">User</option>
				<option value="ADMIN">Admin</option>
			</select>

			<button type="submit" disabled={creating}>
				{creating ? 'Creating...' : 'Create'}
			</button>

			{#if createError}
				<p class="error">{createError}</p>
			{/if}
		</form>
	{/if}

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
					{#if editingId === user.id}
						<form class="edit-form" onsubmit={(event) => saveEdit(event, user)}>
							<input type="text" bind:value={editName} placeholder="Full name" />
							<input type="email" bind:value={editEmail} placeholder="Email" />

							<select bind:value={editRole}>
								<option value="USER">User</option>
								<option value="ADMIN">Admin</option>
							</select>

							<input
								type="password"
								bind:value={editPassword}
								placeholder="New password (leave blank to keep)"
							/>

							<div class="edit-actions">
								<button type="submit" disabled={saving}>
									{saving ? 'Saving...' : 'Save'}
								</button>
								<button type="button" class="cancel" onclick={cancelEdit}>Cancel</button>
							</div>

							{#if editError}
								<p class="error">{editError}</p>
							{/if}
						</form>
					{:else}
						<button type="button" class="edit" onclick={(event) => startEdit(event, user)}>
							Edit info
						</button>

						<a href={`/admin/users/${user.id}`}>
							<span class="name">{user.fullName}</span>
							<span class="email">{user.email}</span>
							<span class="role" class:admin={user.type === 'ADMIN'}>{user.type}</span>
						</a>

						<button type="button" class="delete" onclick={(event) => deleteUser(event, user.id)}>
							Delete
						</button>
					{/if}
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

	.toolbar {
		display: flex;
		justify-content: flex-end;
		margin-bottom: 14px;
	}

	.toolbar button {
		padding: 8px 14px;
		border: none;
		border-radius: 6px;
		background: #333;
		color: white;
		cursor: pointer;
	}

	.create-form,
	.edit-form {
		display: flex;
		flex-wrap: wrap;
		align-items: flex-start;
		gap: 10px;
		padding: 16px;
		margin-bottom: 16px;
		background: white;
		border: 1px solid #e5e5e5;
		border-radius: 10px;
	}

	.edit-form {
		flex: 1;
		margin-bottom: 0;
	}

	.create-form input,
	.create-form select,
	.edit-form input,
	.edit-form select {
		flex: 1;
		min-width: 140px;
		padding: 8px 10px;
		border: 1px solid #aaa;
		border-radius: 5px;
	}

	.create-form button,
	.edit-actions button {
		padding: 8px 16px;
		border: none;
		border-radius: 5px;
		background: #333;
		color: white;
		cursor: pointer;
	}

	.edit-actions {
		display: flex;
		gap: 8px;
	}

	.edit-actions .cancel {
		background: #eee;
		color: #333;
	}

	.create-form .error,
	.edit-form .error {
		width: 100%;
	}

	ul {
		padding: 0;
		margin: 0;
		list-style: none;
		display: flex;
		flex-direction: column;
		gap: 10px;
	}

	li {
		display: flex;
		align-items: center;
		gap: 10px;
	}

	li a {
		flex: 1;
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

	.edit {
		border: none;
		background: #eee;
		color: #333;
		padding: 10px 14px;
		border-radius: 8px;
		cursor: pointer;
		white-space: nowrap;
	}

	.edit:hover {
		background: #ddd;
	}

	.delete {
		border: none;
		background: #dc3545;
		color: white;
		padding: 10px 14px;
		border-radius: 8px;
		cursor: pointer;
		white-space: nowrap;
	}

	.error {
		color: #c0392b;
	}
</style>
