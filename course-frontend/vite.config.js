import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  // 【新增这段配置】：让前端允许局域网内的所有设备访问
  server: {
    host: '0.0.0.0', 
    port: 5173
  },
  build: {
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks: {
          'vue-vendor': ['vue', 'axios'],
          'element-plus': ['element-plus']
        }
      }
    }
  }
})
