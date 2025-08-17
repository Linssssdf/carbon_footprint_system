import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/global.css'
import axios from 'axios'

// 设置后端 API 基础 URL
axios.defaults.baseURL = 'http://localhost:8080' // 后端地址

axios.defaults.withCredentials = true;

// 全局设置
axios.defaults.headers.common['Content-Type'] = 'application/json';

const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.mount('#app')

// 将 axios 挂载到 Vue 实例
app.config.globalProperties.$axios = axios;