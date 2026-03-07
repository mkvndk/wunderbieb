import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const apiProxyTarget = process.env.VITE_API_PROXY_TARGET ?? 'http://localhost:18080';
const oidcProxyTarget = process.env.VITE_OIDC_PROXY_TARGET ?? 'http://localhost:18081';

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    proxy: {
      '/api': {
        target: apiProxyTarget,
        changeOrigin: true
      },
      '/actuator': {
        target: apiProxyTarget,
        changeOrigin: true
      },
      '/realms': {
        target: oidcProxyTarget,
        changeOrigin: false
      },
      '/resources': {
        target: oidcProxyTarget,
        changeOrigin: false
      }
    }
  }
});
