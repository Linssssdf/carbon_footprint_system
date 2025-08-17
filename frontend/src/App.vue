<template>
  <div id="app">
    <Sidebar />
    <div class="main-content">
      <Header />
      <router-view :images="currentImages" @images-updated="updateImages"/>
    </div>
  </div>
</template>

<script>
import Sidebar from './components/Sidebar.vue'
import Header from './components/Header.vue'

export default {
  components: {
    Sidebar,
    Header
  },
  data() {
    return {
      currentImages: [] // 存储当前上传的图表数据
    }
  },
  methods: {
    updateImages(images) {
      // 更新图表数据并传递给WorkflowVisualization
      this.currentImages = images;
    }
  }
}
</script>

<style scoped>
#app {
  display: flex;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}
/* 添加全局样式确保按钮可用 */
.el-button {
  cursor: pointer !important;
  z-index: 1 !important;
  position: relative;
}

/* 防止任何元素覆盖上传区域 */
.visualization {
  position: relative;
  z-index: 10;
}
</style>