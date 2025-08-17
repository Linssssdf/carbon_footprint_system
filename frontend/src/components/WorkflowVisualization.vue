<template>
  <div class="workflow-visualization">
    <div class="section-header">
      <h3 class="section-title">Task Performance Visualization</h3>
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
    
    <div class="visualization-container">
      <div class="metrics-display">
        <div class="metric-card" v-for="(metric, index) in activeMetrics" :key="index">
          <div class="metric-icon">
            <i :class="metric.icon"></i>
          </div>
          <div class="metric-info">
            <div class="metric-label">{{ metric.label }}</div>
            <div class="metric-value">{{ metric.value }}</div>
            <div class="metric-unit">{{ metric.unit }}</div>
          </div>
        </div>
      </div>
      
      <div class="chart-container">
        <canvas ref="performanceChart"></canvas>
      </div>
      
      <div class="task-recommendations">
        <div class="recommendation-header">
          <i class="fas fa-lightbulb"></i>
          <h4>Optimization Suggestions</h4>
        </div>
        <ul class="recommendation-list">
          <li v-for="(rec, index) in activeRecommendations" :key="index">
            <i class="fas fa-check-circle"></i> {{ rec }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue';
import { Chart } from 'chart.js/auto';

export default {
  setup() {
    const tasks = ref([
      { 
        id: 'task1', 
        name: 'BWA_MEM', 
        metrics: { cpu: 990.7, mem: 32.0, duration: 34.4, io: 164.4 },
        recommendations: [
          "Parallelize alignment stages",
          "Optimize memory allocation",
          "Use compressed data formats"
        ]
      },
      { 
        id: 'task2', 
        name: 'SAMTOOLS_SORT', 
        metrics: { cpu: 412.8, mem: 5.9, duration: 9.7, io: 406.9 },
        recommendations: [
          "Optimize sorting algorithm",
          "Implement data chunking",
          "Use SSD storage"
        ]
      },
      { 
        id: 'task3', 
        name: 'MACS2_CALLPEAK', 
        metrics: { cpu: 97.3, mem: 1.8, duration: 4.9, io: 286.4 },
        recommendations: [
          "Combine with similar tasks",
          "Schedule during low-carbon periods",
          "Monitor for performance degradation"
        ]
      },
      { 
        id: 'task4', 
        name: 'TRIMGALORE', 
        metrics: { cpu: 276.5, mem: 2.9, duration: 9.0, io: 86.4 },
        recommendations: [
          "Batch smaller tasks",
          "Optimize I/O patterns",
          "Use faster storage"
        ]
      },
      { 
        id: 'task5', 
        name: 'FASTQC', 
        metrics: { cpu: 86.4, mem: 1.6, duration: 2.6, io: 0.5 },
        recommendations: [
          "Reduce quality check frequency",
          "Use lightweight alternatives",
          "Parallelize across samples"
        ]
      }
    ]);
    
    const activeTask = ref('task1');
    const performanceChart = ref(null);
    let chart = null;
    
    const activeMetrics = ref([]);
    const activeRecommendations = ref([]);
    
    const selectTask = (taskId) => {
      activeTask.value = taskId;
      updateMetrics();
    };
    
    const updateMetrics = () => {
      const task = tasks.value.find(t => t.id === activeTask.value);
      if (task) {
        activeMetrics.value = [
          { 
            label: 'CPU Usage', 
            value: task.metrics.cpu, 
            unit: '%', 
            icon: 'fas fa-microchip' 
          },
          { 
            label: 'Memory Usage', 
            value: task.metrics.mem, 
            unit: 'GB', 
            icon: 'fas fa-memory' 
          },
          { 
            label: 'Duration', 
            value: task.metrics.duration, 
            unit: 'min', 
            icon: 'fas fa-clock' 
          },
          { 
            label: 'Total I/O', 
            value: task.metrics.io, 
            unit: 'GB', 
            icon: 'fas fa-database' 
          }
        ];
        
        activeRecommendations.value = task.recommendations;
      }
    };
    
    const initChart = () => {
      if (performanceChart.value) {
        const ctx = performanceChart.value.getContext('2d');
        if (chart) chart.destroy();
        
        const task = tasks.value.find(t => t.id === activeTask.value);
        if (!task) return;
        
        const data = {
          labels: ['CPU', 'Memory', 'Duration', 'I/O'],
          datasets: [{
            label: 'Resource Usage',
            data: [
              task.metrics.cpu / 10, // 缩放以便在图表中更好地显示
              task.metrics.mem * 10,
              task.metrics.duration * 5,
              task.metrics.io
            ],
            backgroundColor: [
              'rgba(0, 151, 167, 0.6)',
              'rgba(77, 208, 225, 0.6)',
              'rgba(178, 235, 242, 0.6)',
              'rgba(255, 112, 67, 0.6)'
            ],
            borderColor: [
              'rgba(0, 151, 167, 1)',
              'rgba(77, 208, 225, 1)',
              'rgba(178, 235, 242, 1)',
              'rgba(255, 112, 67, 1)'
            ],
            borderWidth: 1
          }]
        };
        
        const options = {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true,
              grid: {
                color: 'rgba(0, 0, 0, 0.05)'
              },
              title: {
                display: true,
                text: 'Resource Usage',
                font: {
                  weight: 'bold'
                }
              }
            },
            x: {
              grid: {
                display: false
              },
              title: {
                display: true,
                text: 'Resource Type',
                font: {
                  weight: 'bold'
                }
              }
            }
          },
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: (context) => {
                  const label = context.dataset.label || '';
                  const value = context.raw || 0;
                  let unit = '';
                  let realValue = value;
                  
                  if (context.label === 'CPU') {
                    unit = '%';
                    realValue = task.metrics.cpu;
                  } else if (context.label === 'Memory') {
                    unit = 'GB';
                    realValue = task.metrics.mem;
                  } else if (context.label === 'Duration') {
                    unit = 'min';
                    realValue = task.metrics.duration;
                  } else if (context.label === 'I/O') {
                    unit = 'GB';
                    realValue = task.metrics.io;
                  }
                  
                  return `${label}: ${realValue} ${unit}`;
                }
              }
            }
          }
        };
        
        chart = new Chart(ctx, {
          type: 'bar',
          data: data,
          options: options
        });
      }
    };
    
    onMounted(() => {
      updateMetrics();
      initChart();
    });
    
    watch(activeTask, () => {
      updateMetrics();
      initChart();
    });
    
    return {
      tasks,
      activeTask,
      performanceChart,
      activeMetrics,
      activeRecommendations,
      selectTask
    };
  }
}
</script>

<style scoped>
.workflow-visualization {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
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
  background: #0097a7;
  color: white;
  border-color: #0097a7;
}

.visualization-container {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.metrics-display {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 15px;
}

.metric-card {
  background: white;
  border-radius: 8px;
  padding: 15px;
  display: flex;
  gap: 15px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  border-left: 4px solid #0097a7;
  transition: transform 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-3px);
}

.metric-icon {
  width: 40px;
  height: 40px;
  background: rgba(0, 151, 167, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.metric-icon i {
  color: #0097a7;
  font-size: 1.2rem;
}

.metric-label {
  font-size: 0.95rem;
  color: #4a5568;
  margin-bottom: 2px;
}

.metric-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #0097a7;
  line-height: 1.2;
}

.metric-unit {
  font-size: 0.9rem;
  color: #718096;
}

.chart-container {
  height: 300px;
  background: white;
  border-radius: 8px;
  padding: 15px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.task-recommendations {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.recommendation-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.recommendation-header i {
  color: #ffc107;
  font-size: 1.5rem;
}

.recommendation-header h4 {
  margin: 0;
  font-size: 1.2rem;
  color: #38455b;
}

.recommendation-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.recommendation-list li {
  padding: 10px 0;
  border-bottom: 1px dashed #e2e8f0;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.recommendation-list li:last-child {
  border-bottom: none;
}

.recommendation-list i {
  color: #28a745;
  font-size: 0.9rem;
  margin-top: 4px;
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