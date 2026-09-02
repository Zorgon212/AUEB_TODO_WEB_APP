import adapter from '@sveltejs/adapter-node';
import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

// The Spring backend runs on localhost:8080 (see backend/src/main/resources/application.properties -
// no server.port set, so it uses Spring Boot's default). These paths are proxied to it so that in dev
// the browser only ever talks to one origin (this Vite server) and the session cookie Spring Security
// sets on /login just works, with no CORS/SameSite configuration needed.
const BACKEND = 'http://localhost:8080';

export default defineConfig({
	plugins: [
		sveltekit({
			compilerOptions: {
				// Force runes mode for the project, except for libraries. Can be removed in svelte 6.
				runes: ({ filename }) => filename.split(/[/\\]/).includes('node_modules') ? undefined : true
			},
			adapter: adapter()
		})
	],
	server: {
		proxy: {
			'/login': BACKEND,
			'/logout': BACKEND,
			'/register': BACKEND,
			'/me': BACKEND,
			'/users': BACKEND,
			'/clients': BACKEND
		}
	}
});
