import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  
  // This proxy is ONLY for local development (npm run dev)
  // The Docker container uses Nginx for proxying.
  server: {
    port: 5173, 
    proxy: {
      '/api': {
        target: 'http://localhost:8080', 
        changeOrigin: true,
      }
    }
  }
})