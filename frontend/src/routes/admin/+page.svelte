<script>
	const links = [
		{ href: '/admin/myinfo', label: 'My Info', icon: '👤' },
		{ href: '/admin/todos', label: 'Todos', icon: '✓' },
		{ href: '/admin/users', label: 'Users', icon: '👥' }
	];
</script>

<svelte:head>
	<title>Admin Dashboard</title>
</svelte:head>

<div class="dashboard">
	<h1>Admin Dashboard</h1>
	<p class="subtitle">Manage your application</p>

	<div class="links">
		{#each links as link}
			<a href={link.href} class="card">
				<span class="icon">{link.icon}</span>
				<span class="label">{link.label}</span>
				<!-- <span class="arrow">→</span> -->
			</a>
		{/each}
	</div>
</div>

<style>
	.dashboard {
		max-width: 900px;
		margin: 80px auto;
		padding: 0 20px;
		font-family: Arial, sans-serif;
	}

	h1 {
		margin-bottom: 8px;
		color: #222;
	}

	.subtitle {
		margin-bottom: 30px;
		color: #777;
	}

	.links {
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
		gap: 20px;
	}

	.card {
		display: flex;
		align-items: center;
		gap: 15px;
		padding: 22px;
		background: white;
		border: 1px solid #e5e5e5;
		border-radius: 12px;
		text-decoration: none;
		color: #222;
		box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
		transition:
			transform 0.2s,
			box-shadow 0.2s,
			border-color 0.2s;
	}

	.card:hover {
		transform: translateY(-3px);
		border-color: #333;
		box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
	}

	.icon {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 45px;
		height: 45px;
		border-radius: 10px;
		background: #f0f0f0;
		font-size: 20px;
	}

	.label {
		flex: 1;
		font-size: 18px;
		font-weight: 600;
	}

	.arrow {
		font-size: 24px;
		color: #888;
		transition: transform 0.2s;
	}

	.card:hover .arrow {
		transform: translateX(5px);
		color: #222;
	}
</style>
