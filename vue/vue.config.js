const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/user': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        pathRewrite: { '^/user': '/user' },
        secure: false,
        ws: true
      }
    }
  }
});
