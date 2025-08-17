<template>
  <div class="dashboard-section">
    <div class="section-header">
      <div class="section-title">Task Analysis & Recommendations</div>
      <div class="task-selector">
        <button 
          v-for="task in tasks" 
          :key="task.id"
          class="task-btn"
          :class="{ active: activeTask === task.id }"
          @click="selectTask(task.id)"
        >
          {{ task.name }}
        </button>
      </div>
    </div>
    
    <div class="section-content">
      <div class="analysis-container">
        <div class="analysis-card">
          <div class="analysis-header">
            <i class="fas fa-chart-line"></i>
            <h4>Performance Analysis</h4>
          </div>
          <ul class="analysis-points">
            <li v-for="(point, index) in activeAnalysis.performance" :key="'p-'+index">
              <i class="fas fa-circle"></i> {{ point }}
            </li>
          </ul>
        </div>
        
        <div class="analysis-card">
          <div class="analysis-header">
            <i class="fas fa-bolt"></i>
            <h4>Energy Consumption</h4>
          </div>
          <ul class="analysis-points">
            <li v-for="(point, index) in activeAnalysis.energy" :key="'e-'+index">
              <i class="fas fa-circle"></i> {{ point }}
            </li>
          </ul>
        </div>
        
        <div class="analysis-card">
          <div class="analysis-header">
            <i class="fas fa-lightbulb"></i>
            <h4>Optimization Suggestions</h4>
          </div>
          <ul class="recommendations">
            <li v-for="(rec, index) in activeAnalysis.recommendations" :key="'r-'+index">
              <i class="fas fa-check-circle"></i> {{ rec }}
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  setup() {
    const tasks = ref([
      { 
        id: 'task1', 
        name: 'Task 1: BWA_MEM', 
        analysis: {
          performance: [
            'High CPU utilization (95-100%) sustained throughout execution',
            'Significant memory demand (32-35GB) for alignment operations',
            'Long runtime averaging 42 minutes per task',
            'I/O wait times observed during peak processing'
          ],
          energy: [
            'Energy consumption: 0.00003521 kWh per task',
            'Carbon footprint: 0.00361099 gCO2e per task',
            'Highest energy consumer in the workflow',
            'Energy efficiency rating: Medium'
          ],
          recommendations: [
            'Implement parallel processing for alignment stages',
            'Optimize memory allocation to reduce overhead',
            'Use compressed data formats to reduce I/O operations',
            'Schedule during low carbon intensity periods'
          ]
        }
      },
      { 
        id: 'task2', 
        name: 'Task 2: SAMTOOLS_SORT', 
        analysis: {
          performance: [
            'Moderate CPU utilization averaging 65%',
            'Memory usage peaks at 8GB during sorting operations',
            'Runtime varies significantly (15-62 minutes)',
            'High I/O operations during file sorting'
          ],
          energy: [
            'Energy consumption: 0.00005205 kWh per task',
            'Carbon footprint: 0.00534022 gCO2e per task',
            'Second highest energy consumer in the workflow',
            'Energy efficiency rating: Low'
          ],
          recommendations: [
            'Optimize sorting algorithm for better efficiency',
            'Implement data chunking to reduce memory spikes',
            'Use SSD storage for faster I/O operations',
            'Batch smaller tasks to reduce overhead'
          ]
        }
      },
      { 
        id: 'task3', 
        name: 'Task 3: MACS2_CALLPEAK', 
        analysis: {
          performance: [
            'Low CPU utilization averaging 25%',
            'Minimal memory requirements (1-2GB)',
            'Consistent runtime of 8-10 minutes',
            'Efficient I/O patterns observed'
          ],
          energy: [
            'Energy consumption: 0.00002145 kWh per task',
            'Carbon footprint: 0.00219815 gCO2e per task',
            'Most energy-efficient task in the workflow',
            'Energy efficiency rating: High'
          ],
          recommendations: [
            'Maintain current efficient implementation',
            'Combine with similar low-resource tasks',
            'Schedule during peak efficiency periods',
            'Monitor for any future performance degradation'
          ]
        }
      }
    ]);
    
    const activeTask = ref('task1');
    const activeAnalysis = ref({});
    
    const selectTask = (taskId) => {
      activeTask.value = taskId;
      const task = tasks.value.find(t => t.id === taskId);
      if (task) {
        activeAnalysis.value = task.analysis;
      }
    };
    
    onMounted(() => {
      // 初始化显示第一个任务的分析
      selectTask('task1');
    });
    
    return {
      tasks,
      activeTask,
      activeAnalysis,
      selectTask
    };
  }
}
</script>

<style scoped>
.dashboard-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #4a4a4a;
}

.task-selector {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.task-btn {
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #4a5568;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.task-btn:hover {
  background: #edf2f7;
}

.task-btn.active {
  background: #2c7873;
  color: white;
  border-color: #2c7873;
}

.analysis-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.analysis-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  border-left: 4px solid #2c7873;
  transition: transform 0.3s ease;
  height: 100%;
}

.analysis-card:hover {
  transform: translateY(-5px);
}

.analysis-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  color: #2c7873;
}

.analysis-header i {
  font-size: 1.5rem;
}

.analysis-header h4 {
  margin: 0;
  font-size: 1.2rem;
}

.analysis-points, .recommendations {
  list-style: none;
  padding: 0;
  margin: 0;
}

.analysis-points li, .recommendations li {
  padding: 8px 0;
  border-bottom: 1px dashed #e2e8f0;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.analysis-points li:last-child, .recommendations li:last-child {
  border-bottom: none;
}

.analysis-points i {
  color: #4a5568;
  font-size: 0.6rem;
  margin-top: 8px;
}

.recommendations i {
  color: #28a745;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .task-selector {
    width: 100%;
  }
  
  .task-btn {
    flex: 1;
    text-align: center;
  }
}
</style>