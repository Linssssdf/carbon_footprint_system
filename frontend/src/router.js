import { createRouter, createWebHashHistory } from 'vue-router'
import Dashboard from './views/Dashboard.vue'
import DataSources from './views/DataSources.vue'
import HelpCenter from './views/HelpCenter.vue'
import Settings from './views/Settings.vue'
import TaskAnalysis from './views/TaskAnalysis.vue'
import Visualization from './views/Visualization.vue'

const routes = [
  {
    path: '/',
    name: 'dashboard',
    component: Dashboard
  },
  {
    path: '/data-sources',
    name: 'data-sources',
    component: DataSources
  },
  {
    path: '/help-center',
    name: 'help-center',
    component: HelpCenter
  },
  {
    path: '/settings',
    name: 'settings',
    component: Settings
  },
  {
    path: '/task-analysis',
    name: 'task-analysis',
    component: TaskAnalysis
  },
  {
    path: '/visualization',
    name: 'visualization',
    component: Visualization
  }
]

export default createRouter({
  history: createWebHashHistory(),
  routes
})