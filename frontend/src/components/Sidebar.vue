<template>
  <div class="sidebar">
    <div class="logo">
      <h1>Carbon Footprint Analysis System</h1>
    </div>
    
    <router-link 
      v-for="(item, index) in navItems" 
      :key="index" 
      :to="item.path"
      class="nav-item"
      :class="{ active: $route.path === item.path }"
    >
      <i :class="item.icon"></i>
      <span class="nav-text">{{ item.text }}</span>
    </router-link>
    
    <!-- Visualization 分组 -->
    <div 
      class="nav-item-group"
      :class="{ active: isVisualizationActive }"
      @click="toggleVisualizationMenu"
    >
      <div class="group-header">
        <i class="fas fa-chart-bar"></i>
        <span class="nav-text">Visualization</span>
        <i :class="['arrow-icon', showVisualizationMenu ? 'fas fa-angle-down' : 'fas fa-angle-right']"></i>
      </div>
      
      <div v-show="showVisualizationMenu" class="sub-menu">
        <router-link 
          to="/visualization2"
          class="sub-item"
          :class="{ active: $route.path === '/visualization2' }"
        >
          <i class="fas fa-chart-line"></i>
          <span class="nav-text">Visualization</span>
        </router-link>
      </div>
    </div>
    
    <div class="help-section">
      <router-link to="/help-center" class="nav-item">
        <span class="nav-text"> Help Center</span>
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      showVisualizationMenu: false,
      navItems: [
        { icon: 'fas fa-home', text: 'Dashboard', path: '/' },
        { icon: 'fas fa-tasks', text: 'Task Analysis', path: '/task-analysis' },
        { icon: 'fas fa-database', text: 'Data Source', path: '/data-sources' },
        { icon: 'fas fa-cog', text: 'System Settings', path: '/settings' }
      ],
      // visualizationItems: [
      //   { icon: 'fas fa-chart-line', text: 'Task 1', path: '/visualization1' },
      //   { icon: 'fas fa-chart-line', text: 'Task 2', path: '/visualization2' },
      //   { icon: 'fas fa-chart-line', text: 'Task 3', path: '/visualization3' }
      // ]
    }
  },
  computed: {
    isVisualizationActive() {
      return this.$route.path === '/visualization2';
    }
  },
  methods: {
    toggleVisualizationMenu() {
      this.showVisualizationMenu = !this.showVisualizationMenu;
    }
  },
  watch: {
    $route(to) {
      // 当路由变化到可视化页面时，确保菜单展开
      if (to.path === '/visualization2') {
        this.showVisualizationMenu = true;
      }
    }
  }
}
</script>

<style>
/* 保留原有样式 */
.sidebar {
  width: 280px;
  background: linear-gradient(180deg, #2c7873, #1a535c);
  color: white;
  padding: 20px 0;
}

.logo {
  display: flex;
  align-items: center;
  padding: 0 20px 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  margin-bottom: 20px;
}

.logo h1 {
  font-size: 1.5rem;
  margin-left: 12px;
}

.nav-item {
  padding: 12px 25px;
  display: flex;
  align-items: center;
  color: white;
  text-decoration: none;
}

.nav-item i {
  margin-right: 12px;
}

/* 新增分组样式 */
.nav-item-group {
  cursor: pointer;
  padding: 0;
}

.group-header {
  padding: 12px 25px;
  display: flex;
  align-items: center;
  position: relative;
}

.arrow-icon {
  position: absolute;
  right: 20px;
  transition: transform 0.3s ease;
}

.sub-menu {
  background-color: rgba(0, 0, 0, 0.1);
  border-left: 4px solid #0097a7;
  margin-left: 25px;
}

.sub-item {
  padding: 10px 25px 10px 45px;
  display: flex;
  align-items: center;
  color: #e0e0e0;
  text-decoration: none;
  font-size: 0.9rem;
}

.sub-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

/* 活动状态样式 */
.active {
  background-color: rgba(255, 255, 255, 0.15);
  border-left: 4px solid #4dd0e1;
}
</style>