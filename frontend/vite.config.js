import { defineConfig } from 'vite'

// Simple Vite config to ensure requests to `/` serve `index.html` and
// to fix the dev server port so the URL is predictable.
export default defineConfig({
  server: {
    host: true,
    port: 5174
  },
  plugins: [
    {
      name: 'root-rewrite',
      configureServer(server) {
        server.middlewares.use((req, res, next) => {
          if (req.url === '/' || req.url === '') {
            req.url = '/index.html'
          }
          next()
        })
      }
    }
  ]
})
