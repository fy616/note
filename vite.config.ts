import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'
import { noteFilePlugin } from './vite-plugin-note.ts'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), noteFilePlugin()],
  server: {
    host: '127.0.0.1',
    port: 4173,
    strictPort: true,
  },
})
