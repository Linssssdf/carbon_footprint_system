<template>
  <div class="system-monitoring">
    <div class="header">
      <h1>ichnos emap Workflow Performance Analysis Report</h1>
      <p class="subtitle">carbon Workflow Analysis</p>
    </div>

    <div class="section">
      <h2>Average Hourly Carbon Intensity (2023)</h2>
      <div class="chart-container">
        <div ref="carbonChart" style="width: 100%; height: 400px;"></div>
      </div>
    </div>
    
    <div class="section">
      <h2>Task Performance Analysis</h2>
      <div class="controls">
        <div class="metric-selector">
          <button 
            v-for="metric in metrics" 
            :key="metric.id"
            class="metric-btn"
            :class="{ active: activeMetric === metric.id }"
            @click="activeMetric = metric.id"
          >
            {{ metric.name }}
          </button>
        </div>
      </div>
      <div class="chart-container">
        <canvas ref="taskChart"></canvas>
      </div>
    </div>
    
    <div class="section conclusions">
      <h2>Key Findings</h2>
      <div class="findings-grid">
        <div class="finding-card">
          <h4><i class="fas fa-fire"></i> Highest Carbon Footprint</h4>
          <p>
            <strong>fastqc (Task 4)</strong> had the highest emissions (0.0053 gCO₂e):
            <br>- Highest energy consumption: 5.213e-05 kWh
            <br>- Longest runtime among top tasks: 3000 ms
            <br>- Consistent 4GB memory usage across all tasks
          </p>
        </div>
        <div class="finding-card">
          <h4><i class="fas fa-bolt"></i> CPU Utilization Patterns</h4>
          <p>
            Significant CPU usage variations:
            <br>- Task 1: 150% CPU usage (highest)
            <br>- Task 4: 111% CPU usage
            <br>- Task 2: 107% CPU usage
            <br>- Task 3: 106% CPU usage (lowest)
          </p>
        </div>
        <div class="finding-card">
          <h4><i class="fas fa-leaf"></i> Carbon Emission Profile</h4>
          <p>
            Total workflow emissions: 0.02096 gCO₂e
            <br>- Operational carbon: 0.01964 gCO₂e (93.7%)
            <br>- Embodied carbon: 0.00132 gCO₂e (6.3%)
            <br>- PUE-adjusted energy: 0.000191 kWh
          </p>
        </div>
        <div class="finding-card">
          <h4><i class="fas fa-lightbulb"></i> Optimization Opportunities</h4>
          <p>
            Potential efficiency improvements:
            <br>- Reduce Task 1's high CPU usage (150%)
            <br>- Balance runtime across fastqc instances
            <br>- Investigate energy variance in identical tasks
          </p>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import * as echarts from 'echarts';
import Chart from 'chart.js/auto';

export default {
  data() {
    return {
      activeMetric: 'cpu',
      metrics: [
        { id: 'cpu', name: 'CPU Usage (%)' },
        { id: 'mem', name: 'Memory (GB)' },
        { id: 'duration', name: 'Duration (ms)' },
        { id: 'rss', name: 'RSS (MB)' }
      ],
      taskData: [
        {
          task_id: "2",
          process: "fastqc",
          cpus: 4,
          duration: "19592",
          memory: 4294967296,
          cpu_usage: "107.5",
          rss: 238440448
        },
        {
          task_id: "4",
          process: "fastqc",
          cpus: 4,
          duration: "19733",
          memory: 4294967296,
          cpu_usage: "111.7",
          rss: 240570368
        },
        {
          task_id: "3",
          process: "fastqc",
          cpus: 4,
          duration: "19900",
          memory: 4294967296,
          cpu_usage: "106.3",
          rss: 254971904
        },
        {
          task_id: "1",
          process: "fastqc",
          cpus: 4,
          duration: "2990",
          memory: 4294967296,
          cpu_usage: "150.9",
          rss: 234872832
        }
      ],
      taskChart: null,
      carbonChart: null
    };
  },
  computed: {
    sortedTasks() {
      return [...this.taskData].sort((a, b) => {
        if (this.activeMetric === 'cpu') return b.cpu_usage - a.cpu_usage;
        if (this.activeMetric === 'mem') return (b.memory / 1024 / 1024 / 1024) - (a.memory / 1024 / 1024 / 1024);
        if (this.activeMetric === 'duration') return b.duration - a.duration;
        return (b.rss / 1024 / 1024) - (a.rss / 1024 / 1024);
      });
    },
    metricLabel() {
      return this.metrics.find(m => m.id === this.activeMetric).name;
    },
    chartData() {
      return this.sortedTasks.map(task => {
        let value;
        switch (this.activeMetric) {
          case 'cpu': 
            value = parseFloat(task.cpu_usage);
            break;
          case 'mem':
            value = task.memory / 1024 / 1024 / 1024;
            break;
          case 'duration':
            value = parseInt(task.duration);
            break;
          case 'rss':
            value = task.rss / 1024 / 1024;
            break;
          default:
            value = 0;
        }
        return {
          label: `Task ${task.task_id}`,
          value: value,
          color: this.getColorForTask(task.task_id)
        };
      });
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initCarbonChart();
      this.renderTaskChart();
    });
    window.addEventListener('resize', this.handleResize);
  },
  watch: {
    activeMetric() {
      this.renderTaskChart();
    }
  },
  methods: {
    getColorForTask(taskId) {
      const colors = ['#0097a7', '#4dd0e1', '#b2ebf2', '#ff7043'];
      const index = parseInt(taskId) % colors.length;
      return colors[index];
    },
    initCarbonChart() {
      const chartDom = this.$refs.carbonChart;
      if (!chartDom) return;
      
      this.carbonChart = echarts.init(chartDom);
      this.carbonChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            let res = `Hour: ${params[0].name}:00<br>`;
            params.forEach(param => {
              res += `${param.seriesName}: ${param.value} gCO₂eq/kWh<br>`;
            });
            return res;
          }
        },
        legend: {
          data: ['DE (Germany)', 'FR (France)', 'GB (UK)', 'US-CAL (California)'],
          bottom: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          name: 'Hour of Day',
          data: Array.from({length: 24}, (_, i) => `${i}`),
          nameTextStyle: {
            color: '#666'
          }
        },
        yAxis: {
          type: 'value',
          name: 'Carbon Intensity (gCO₂eq/kWh)',
          nameTextStyle: {
            color: '#666'
          }
        },
        series: [
          {
            name: 'DE (Germany)',
            type: 'line',
            data: [342.0, 340.5, 341.3, 346.0, 350.3, 349.8, 340.1, 319.4, 293.6, 272.2, 260.6, 255.9, 258.8, 271.0, 293.1, 324.8, 357.0, 377.2, 384.0, 380.9, 374.7, 367.4, 355.6, 346.1],
            smooth: true,
            lineStyle: { width: 3, color: '#0097a7' }
          },
          {
            name: 'FR (France)',
            type: 'line',
            data: [30.0, 29.4, 28.4, 28.3, 30.0, 31.8, 32.9, 32.3, 30.7, 28.5, 27.1, 25.7, 24.8, 24.8, 25.3, 27.3, 31.1, 35.3, 36.8, 35.9, 34.9, 34.7, 33.3, 30.8],
            smooth: true,
            lineStyle: { width: 3, color: '#4dd0e1' }
          },
          {
            name: 'GB (UK)',
            type: 'line',
            data: [124.8, 123.5, 122.7, 124.4, 129.4, 137.7, 144.7, 142.9, 136.2, 129.3, 123.7, 120.2, 118.6, 120.2, 126.9, 138.6, 152.6, 161.5, 163.7, 161.9, 157.1, 147.3, 131.8, 124.8],
            smooth: true,
            lineStyle: { width: 3, color: '#b2ebf2' }
          },
          {
            name: 'US-CAL (California)',
            type: 'line',
            data: [180.4, 209.2, 228.1, 231.9, 232.0, 232.8, 232.5, 232.4, 231.2, 230.9, 231.2, 231.6, 234.1, 229.3, 206.0, 173.4, 147.2, 136.2, 133.5, 131.9, 130.6, 130.8, 135.0, 150.7],
            smooth: true,
            lineStyle: { width: 3, color: '#ff7043' }
          }
        ]
      });
    },
    renderTaskChart() {
      const ctx = this.$refs.taskChart;
      
      if (this.taskChart) {
        this.taskChart.destroy();
      }
      
      const labels = this.chartData.map(d => d.label);
      const data = this.chartData.map(d => d.value);
      const colors = this.chartData.map(d => d.color);
      
      let unit = '';
      if (this.activeMetric === 'cpu') {
        unit = '%';
      } else if (this.activeMetric === 'mem') {
        unit = 'GB';
      } else if (this.activeMetric === 'duration') {
        unit = 'ms';
      } else if (this.activeMetric === 'rss') {
        unit = 'MB';
      }
      
      this.taskChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: this.metricLabel,
            data: data,
            backgroundColor: colors,
            borderWidth: 1
          }]
        },
        options: {
          indexAxis: 'y',
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                title: (items) => items[0].label,
                label: (context) => {
                  const value = context.parsed.x;
                  return `${this.metricLabel}: ${value.toFixed(2)} ${unit}`;
                }
              }
            }
          },
          scales: {
            x: {
              beginAtZero: true,
              title: {
                display: true,
                text: `${this.metricLabel}`
              }
            }
          }
        }
      });
    },
    handleResize() {
      if (this.carbonChart) this.carbonChart.resize();
      if (this.taskChart) this.taskChart.resize();
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
    if (this.carbonChart) this.carbonChart.dispose();
    if (this.taskChart) this.taskChart.destroy();
  }
};
</script>

<style scoped>
.system-monitoring {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 30px;
  display: flex;
  flex-direction: column;
  gap: 30px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  color: #333;
}

.header {
  text-align: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.header h1 {
  font-size: 2.2rem;
  margin-bottom: 10px;
  color: #4a4a4a;
  font-weight: 600;
}

.subtitle {
  font-size: 1.1rem;
  color: #666666;
  max-width: 700px;
  margin: 0 auto;
  line-height: 1.6;
}

.section {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.05);
  border: 1px solid #eee;
}

.section h2 {
  font-size: 1.5rem;
  margin-bottom: 20px;
  color: #555;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
  font-weight: 500;
}

.chart-container {
  position: relative;
  height: 400px;
  width: 100%;
  background: #ffffff;
  border-radius: 8px;
  padding: 15px;
  border: 1px solid #f0f0f0;
}

.controls {
  margin-bottom: 20px;
}

.metric-selector {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.metric-btn {
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid #0097a7;
  background: #ffffff;
  color: #0097a7;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.metric-btn:hover {
  background: #f0f8ff;
}

.metric-btn.active {
  background: #0097a7;
  color: white;
}

.conclusions h2 {
  font-size: 1.5rem;
  margin-bottom: 20px;
  color: #555;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
  font-weight: 500;
}

.findings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.finding-card {
  background: #fdfdfd;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.05);
  border-left: 4px solid #0097a7;
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.finding-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.finding-card h4 {
  color: #555;
  font-size: 1.1rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.finding-card h4 i {
  color: #0097a7;
  font-size: 1.2rem;
}

.finding-card p {
  color: #666;
  line-height: 1.6;
  font-size: 0.95rem;
  margin: 0;
}

.finding-card strong {
  color: #333;
  font-weight: 700;
}

@media (max-width: 768px) {
  .system-monitoring {
    padding: 20px 15px;
  }
  .chart-container {
    height: 350px;
    padding: 10px;
  }
  .header h1 {
    font-size: 1.8rem;
  }
  .section h2 {
    font-size: 1.3rem;
  }
  .metric-btn {
    padding: 6px 12px;
    font-size: 0.85rem;
  }
  .findings-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .system-monitoring {
    padding: 20px 15px;
  }
  .chart-container {
    height: 300px;
  }
  .header h1 {
    font-size: 1.5rem;
  }
  .section {
    padding: 15px;
  }
}
</style>