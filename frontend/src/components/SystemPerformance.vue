<template>
  <div class="dashboard-section">
    <div class="section-header">
      <div class="section-title">System Performance Monitoring</div>
      <div class="time-selector">
        <button 
          v-for="period in timePeriods" 
          :key="period.id"
          class="period-btn"
          :class="{ active: activePeriod === period.id }"
          @click="activePeriod = period.id"
        >
          {{ period.label }}
        </button>
      </div>
    </div>
    <div class="section-content">
      <div class="performance-dashboard">
        <div class="metric-board">
          <div 
            v-for="(metric, index) in performanceMetrics" 
            :key="index" 
            class="metric-card"
          >
            <div class="metric-header">
              <div class="metric-title">{{ metric.title }}</div>
              <div class="metric-unit">{{ metric.unit }}</div>
            </div>
            <div class="metric-value-container">
              <div class="metric-value">{{ metric.value }}</div>
              <div class="metric-trend" 
                :class="metric.trend.type === 'increase' ? 'trend-up' : 'trend-down'"
              >
                <i :class="metric.trend.icon"></i>
                {{ metric.trend.value }}%
              </div>
            </div>
            <div class="metric-progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: `${metric.progress}%` }"
              ></div>
            </div>
          </div>
        </div>

        <div class="performance-chart-container">
          <canvas ref="performanceChart"></canvas>
        </div>
      </div>
      
      <div class="carbon-intensity">
        <div class="intensity-header">
          <i class="fas fa-leaf"></i>
          <h4>Current Carbon Intensity: <span>{{ currentCarbonIntensity }} gCO₂eq/kWh</span></h4>
        </div>
        <div class="intensity-level" :class="carbonIntensityClass">
          {{ carbonIntensityLabel }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { Chart } from 'chart.js/auto';

// 模拟从Ichnos脚本输出的数据
const ichnosData = {
  labels: ['BWA_MEM', 'SAMTOOLS_SORT', 'MACS2_CALLPEAK', 'TRIMGALORE', 'FASTQC'],
  datasets: [{
    label: 'Energy consumption (kWh)',
    data: [0.00003521, 0.00005207, 0.00002145, 0.00001832, 0.00001256],
    backgroundColor: [
      'rgba(255, 107, 107, 0.7)',
      'rgba(255, 193, 7, 0.7)',
      'rgba(40, 167, 69, 0.7)',
      'rgba(108, 117, 125, 0.7)',
      'rgba(0, 151, 167, 0.7)'
    ],
    borderColor: [
      'rgba(255, 107, 107, 1)',
      'rgba(255, 193, 7, 1)',
      'rgba(40, 167, 69, 1)',
      'rgba(108, 117, 125, 1)',
      'rgba(0, 151, 167, 1)'
    ],
    borderWidth: 1
  }]
};

const basicConfig = {
  type: 'bar',
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      beginAtZero: true,
      title: {
        display: true,
        text: 'Energy Consumption (kWh)',
        font: {
          weight: 'bold'
        }
      }
    },
    x: {
      title: {
        display: true,
        text: 'Task Name',
        font: {
          weight: 'bold'
        }
      }
    }
  },
  plugins: {
    legend: { 
      position: 'top',
      labels: { 
        font: { 
          weight: 'bold',
          size: 12
        } 
      }
    },
    tooltip: {
      callbacks: {
        label: (context) => {
          const label = context.dataset.label || '';
          const value = context.raw || 0;
          return `${label}: ${value.toFixed(8)} kWh`;
        }
      }
    }
  }
};

export default {
  setup() {
    const timePeriods = [
      { id: 'hour', label: 'Last Hour' },
      { id: 'day', label: 'Last 24 Hours' },
      { id: 'week', label: 'Last Week' }
    ];
    
    const activePeriod = ref('day');
    const performanceMetrics = ref([
      {
        title: 'CPU Energy Efficiency',
        unit: 'GFLOPS/W',
        value: '3.2',
        trend: { type: 'increase', value: 8, icon: 'fas fa-arrow-up' },
        progress: 75
      },
      {
        title: 'Memory Usage',
        unit: '%',
        value: '72',
        trend: { type: 'decrease', value: 5, icon: 'fas fa-arrow-down' },
        progress: 72
      },
      {
        title: 'Task Throughput',
        unit: 'tasks/min',
        value: '12.4',
        trend: { type: 'increase', value: 15, icon: 'fas fa-arrow-up' },
        progress: 82
      },
      {
        title: 'Carbon Efficiency',
        unit: 'gCO₂eq/task',
        value: '0.042',
        trend: { type: 'decrease', value: 12, icon: 'fas fa-arrow-down' },
        progress: 68
      }
    ]);

    const performanceChart = ref(null);
    let chart = null;
    
    const currentCarbonIntensity = ref(182.4);
    const carbonIntensityClass = ref('medium-intensity');
    const carbonIntensityLabel = ref('Medium');

    onMounted(() => {
      if (performanceChart.value) {
        const ctx = performanceChart.value.getContext('2d');
        if (ctx) {
          chart = new Chart(ctx, {
            ...basicConfig,
            data: ichnosData
          });
        }
      }
      
      // 设置碳强度级别
      if (currentCarbonIntensity.value < 100) {
        carbonIntensityClass.value = 'low-intensity';
        carbonIntensityLabel.value = 'Low';
      } else if (currentCarbonIntensity.value >= 100 && currentCarbonIntensity.value < 300) {
        carbonIntensityClass.value = 'medium-intensity';
        carbonIntensityLabel.value = 'Medium';
      } else {
        carbonIntensityClass.value = 'high-intensity';
        carbonIntensityLabel.value = 'High';
      }
    });

    return {
      timePeriods,
      activePeriod,
      performanceMetrics,
      performanceChart,
      currentCarbonIntensity,
      carbonIntensityClass,
      carbonIntensityLabel
    };
  }
}
</script>

<style scoped>
.performance-dashboard {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

.metric-board {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 15px;
}

.metric-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
  border-left: 4px solid #2c7873;
}

.metric-card:hover {
  transform: translateY(-5px);
}

.metric-header {
  margin-bottom: 15px;
}

.metric-title {
  font-size: 1rem;
  font-weight: 600;
  color: #2c7873;
}

.metric-unit {
  font-size: 0.9rem;
  color: #6b7280;
}

.metric-value-container {
  position: relative;
  height: 45px;
}

.metric-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #38455b;
}

.metric-trend {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.9rem;
  font-weight: 600;
}

.trend-up {
  color: #28a745;
}

.trend-down {
  color: #dc3545;
}

.metric-progress-bar {
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  margin-top: 15px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #2c7873;
  transition: width 0.3s ease;
}

.performance-chart-container {
  height: 300px;
  background: white;
  border-radius: 8px;
  padding: 15px;
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

.time-selector {
  display: flex;
  gap: 10px;
}

.period-btn {
  padding: 6px 12px;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #4a5568;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.period-btn:hover {
  background: #edf2f7;
}

.period-btn.active {
  background: #2c7873;
  color: white;
  border-color: #2c7873;
}

.carbon-intensity {
  margin-top: 20px;
  background: white;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.intensity-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.intensity-header i {
  color: #28a745;
  font-size: 1.2rem;
}

.intensity-header h4 {
  margin: 0;
  font-size: 1.1rem;
  color: #38455b;
}

.intensity-header span {
  font-weight: 700;
  color: #2c7873;
}

.intensity-level {
  padding: 8px 15px;
  border-radius: 20px;
  font-weight: 600;
  text-align: center;
  max-width: 150px;
}

.low-intensity {
  background: rgba(40, 167, 69, 0.2);
  color: #28a745;
}

.medium-intensity {
  background: rgba(255, 193, 7, 0.2);
  color: #d4a017;
}

.high-intensity {
  background: rgba(220, 53, 69, 0.2);
  color: #dc3545;
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .time-selector {
    width: 100%;
  }
  
  .period-btn {
    flex: 1;
    text-align: center;
  }
}
</style>