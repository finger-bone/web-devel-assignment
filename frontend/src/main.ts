import { createApp } from 'vue'
import '@/styles/index.less'
import App from './App.vue'
import { setupRouter } from './router'
import { setupStore } from './store'
import { setupI18n } from './locales/setupI18n'
import * as echarts from 'echarts'
import VueECharts from 'vue-echarts'

echarts
const bootstrap = async () => {
  const app = createApp(App)
  app.component('v-chart', VueECharts)
  setupRouter(app)
  setupStore(app)
  await setupI18n(app)
  app.mount('#app')
}

bootstrap()
