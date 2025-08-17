import { createRouter, createWebHashHistory } from 'vue-router'
import Dashboard from './views/Dashboard.vue'
import DataSources from './views/DataSources.vue'
import HelpCenter from './views/HelpCenter.vue'
import Settings from './views/Settings.vue'
import TaskAnalysis from './views/TaskAnalysis.vue'
import Visualization1 from './views/Visualization1.vue'
import Visualization2 from './views/Visualization2.vue'
import Visualization3 from './views/Visualization3.vue'
import WorkflowPage from './views/WorkflowPage.vue'

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
    redirect: '/visualization1' // 添加重定向到Task1
  },
  {
    path: '/visualization1',
    name: 'visualization1',
    component: Visualization1
  },
  {
    path: '/visualization2',
    name: 'visualization2',
    component: Visualization2
  },
  {
    path: '/visualization3',
    name: 'visualization3',
    component: Visualization3
  },
  {
    path: '/workflow',
    name: 'workflow',
    component: WorkflowPage
  }
]

export default createRouter({
  history: createWebHashHistory(),
  routes
})