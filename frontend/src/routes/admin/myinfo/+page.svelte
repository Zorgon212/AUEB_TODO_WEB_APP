<script>
	import { api } from '../../../lib/api.js';
	import { session } from '../../../stores/session.svelte.js';

	let fullName = $state(session.user?.fullName ?? '');
	let email = $state(session.user?.email ?? '');

	let editing = $state(false);
	let saving = $state(false);
	let message = $state('');
	let error = $state('');

	function startEditing() {
		fullName = session.user.fullName;
		email = session.user.email;
		editing = true;
	}

	async function saveChanges() {
		saving = true;
		error = '';

		try {
			const updated = await api.put(`/users/${session.user.id}`, {
				...session.user,
				fullName,
				email
			});
			await session.refresh();
			editing = false;
			message = 'Your information has been updated.';
			setTimeout(() => (message = ''), 3000);
		} catch (err) {
			error = err.message || 'Could not save changes.';
		} finally {
			saving = false;
		}
	}
</script>

<svelte:head>
	<title>Admin - My Info</title>
</svelte:head>

<div class="page">
	<div class="header">
		<a href="/admin" class="back">← Back to Dashboard</a>

		<h1>My Information</h1>
		<p>View and manage your administrator account.</p>
	</div>

	{#if session.user}
		<div class="card">
			<div class="profile">
				<div class="avatar">👤</div>

				<div>
					<h2>{session.user.fullName}</h2>
					<p>{session.user.type}</p>
				</div>
			</div>

			<div class="divider"></div>

			<div class="info">
				<div class="field">
					<label for="name">Name</label>

					{#if editing}
						<input id="name" type="text" bind:value={fullName} />
					{:else}
						<div class="value">{session.user.fullName}</div>
					{/if}
				</div>

				<div class="field">
					<label for="email">Email</label>

					{#if editing}
						<input id="email" type="email" bind:value={email} />
					{:else}
						<div class="value">{session.user.email}</div>
					{/if}
				</div>

				<div class="field">
					<label>Role</label>
					<div class="value">
						<span class="role">{session.user.type}</span>
					</div>
				</div>
			</div>

			<div class="actions">
				{#if editing}
					<button class="cancel" onclick={() => (editing = false)}>
						Cancel
					</button>

					<button class="save" onclick={saveChanges} disabled={saving}>
						{saving ? 'Saving...' : 'Save Changes'}
					</button>
				{:else}
					<button class="edit" onclick={startEditing}>
						Edit Information
					</button>
				{/if}
			</div>

			{#if error}
				<p class="message error">{error}</p>
			{/if}

			{#if message}
				<p class="message">{message}</p>
			{/if}
		</div>
	{/if}
</div>

<style>
	.page {
		max-width: 700px;
		margin: 60px auto;
		padding: 0 20px;
		font-family: Arial, sans-serif;
	}

	.header {
		margin-bottom: 25px;
	}

	.back {
		display: inline-block;
		margin-bottom: 20px;
		color: #555;
		text-decoration: none;
	}

	.back:hover {
		color: #000;
	}

	h1 {
		margin: 0 0 8px;
		color: #222;
	}

	.header p {
		margin: 0;
		color: #777;
	}

	.card {
		padding: 30px;
		background: white;
		border: 1px solid #e5e5e5;
		border-radius: 14px;
		box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
	}

	.profile {
		display: flex;
		align-items: center;
		gap: 18px;
	}

	.avatar {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 65px;
		height: 65px;
		border-radius: 50%;
		background: #f0f0f0;
		font-size: 28px;
	}

	.profile h2 {
		margin: 0 0 5px;
		color: #222;
	}

	.profile p {
		margin: 0;
		color: #777;
	}

	.divider {
		height: 1px;
		background: #eee;
		margin: 25px 0;
	}

	.info {
		display: flex;
		flex-direction: column;
		gap: 20px;
	}

	.field label {
		display: block;
		margin-bottom: 7px;
		font-size: 14px;
		font-weight: 600;
		color: #555;
	}

	.value {
		padding: 11px 13px;
		border: 1px solid #eee;
		border-radius: 7px;
		background: #f8f8f8;
		color: #222;
	}

	input {
		width: 100%;
		box-sizing: border-box;
		padding: 11px 13px;
		border: 1px solid #aaa;
		border-radius: 7px;
		font-size: 15px;
	}

	input:focus {
		outline: none;
		border-color: #333;
	}

	.role {
		display: inline-block;
		padding: 5px 10px;
		border-radius: 20px;
		background: #333;
		color: white;
		font-size: 13px;
	}

	.actions {
		display: flex;
		justify-content: flex-end;
		gap: 10px;
		margin-top: 25px;
	}

	button {
		padding: 10px 16px;
		border: none;
		border-radius: 7px;
		cursor: pointer;
		font-size: 14px;
	}

	.edit,
	.save {
		background: #333;
		color: white;
	}

	.edit:hover,
	.save:hover {
		background: #555;
	}

	.save:disabled {
		opacity: 0.6;
		cursor: default;
	}

	.cancel {
		background: #eee;
		color: #333;
	}

	.cancel:hover {
		background: #ddd;
	}

	.message {
		margin: 20px 0 0;
		padding: 10px;
		border-radius: 7px;
		background: #e8f5e9;
		color: #2e7d32;
		text-align: center;
	}

	.message.error {
		background: #fdecea;
		color: #c0392b;
	}
</style>
