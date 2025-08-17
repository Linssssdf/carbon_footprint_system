<template>
  <div class="content">
    <h2 style="margin-bottom: 20px;">Carbon Footprint Analysis Dashboard</h2>

    <div class="dashboard-grid">
      <div class="dashboard-card">
        <div class="card-header">
          <h3>Hardware Energy Tracking</h3>
        </div>
        <div class="card-content">
          <ContextTrackingData />
        </div>
      </div>

      <div class="dashboard-card">
        <div class="card-header">
          <h3>Performance Indicators</h3>
        </div>
        <div class="card-content">
          <SystemPerformance />
        </div>
      </div>

      <div class="dashboard-card">
        <div class="card-header">
          <h3>Workflow Visualization</h3>
        </div>
        <div class="card-content">
          <WorkflowVisualization />
        </div>
      </div>

      <div class="dashboard-card">
        <div class="card-header">
          <h3>Custom task analysis</h3>
        </div>
        <div class="card-content">
          <CustomTaskAnalysis />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ContextTrackingData from '@/components/ContextTrackingData.vue'
import CustomTaskAnalysis from '@/components/CustomTaskAnalysis.vue'
import WorkflowVisualization from '@/components/WorkflowVisualization.vue'
import SystemPerformance from '@/components/SystemPerformance.vue'

export default {
  components: {
    ContextTrackingData,
    CustomTaskAnalysis,
    WorkflowVisualization,
    SystemPerformance
  }
}
</script>

<style scoped>
.content {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-grid {
  display: flex;
  flex-direction: column;
  gap: 30px;
  margin-bottom: 20px;
}

.dashboard-card {
  background: rgb(255, 255, 255);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.dashboard-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.card-header {
  background-color: #e0e2e5;
  padding: 20px 25px;
  border-bottom: 1px solid #e2e8f0;
}

.card-header h3 {
  margin: 0;
  font-size: 1.4rem;
  color: #38455b;
  font-weight: 600;
}

.card-content {
  padding: 25px;
}
</style>