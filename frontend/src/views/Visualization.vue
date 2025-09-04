<template>
  <div class="system-monitoring">
    <div class="header">
      <h1>System Monitoring and Performance Analysis</h1>
      <p class="subtitle">Task performance analysis</p>
    </div>

    <div class="section summary" v-if="summary">
      <h2>Workflow Performance Overview</h2>
      <div class="summary-cards">
        <div class="card">
          <div class="card-icon"><i class="fas fa-bolt"></i></div>
          <div class="card-content">
            <h3>Total energy consumption</h3>
            <p>{{ summary.totalEnergy ? summary.totalEnergy.toFixed(1) : 0 }} kWh</p>
          </div>
        </div>
        <div class="card">
          <div class="card-icon"><i class="fas fa-cloud"></i></div>
          <div class="card-content">
            <h3>Carbon emissions</h3>
            <p>{{ summary.totalCarbonFootprint ? summary.totalCarbonFootprint.toFixed(1) : 0 }} kgCO₂eq</p>
          </div>
        </div>
        <div class="card">
          <div class="card-icon"><i class="fas fa-microchip"></i></div>
          <div class="card-content">
            <h3>CPU efficiency</h3>
            <p>{{ summary.avgCpuUtilization ? summary.avgCpuUtilization.toFixed(1) : 0 }}%</p>
          </div>
        </div>
        <div class="card">
          <div class="card-icon"><i class="fas fa-clock"></i></div>
          <div class="card-content">
            <h3>Total runtime</h3>
            <p>{{ summary.totalRuntime ? summary.totalRuntime.toFixed(1) : 0 }} min</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 主机功耗分布图表 -->
    <div class="section" v-if="hostData && hostData.hosts && hostData.hosts.length > 0">
      <h2>Host power consumption distribution (Watt-hours)</h2>
      <div class="chart-container">
        <canvas id="powerChart" ref="powerChart"></canvas>
      </div>
    </div>
    
    <!-- 进程性能指标图表 -->
    <div class="section" v-if="formattedProcessData && formattedProcessData.length > 0">
      <h2>Process performance indicators</h2>
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
        <canvas id="performanceChart" ref="performanceChart"></canvas>
      </div>
    </div>

    <!-- 进程I/O分析图表 -->
    <div class="section" v-if="formattedProcessData && formattedProcessData.length > 0">
      <h2>Process I/O Analysis (GB)</h2>
      <div class="chart-container">
        <canvas id="ioChart" ref="ioChart"></canvas>
      </div>
    </div>

    <!-- CPU和内存平均值显示 -->
    <div class="section" v-if="formattedProcessData && formattedProcessData.length > 0">
      <h2>Average Resource Usage</h2>
      <div class="summary-cards">
        <div class="card">
          <div class="card-icon"><i class="fas fa-microchip"></i></div>
          <div class="card-content">
            <h3>Average CPU Usage</h3>
            <p>{{ avgCpuUsage.toFixed(1) }}%</p>
          </div>
        </div>
        <div class="card">
          <div class="card-icon"><i class="fas fa-memory"></i></div>
          <div class="card-content">
            <h3>Average Memory Usage</h3>
            <p>{{ avgMemoryUsage.toFixed(1) }} GB</p>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
      <p>Loading analysis data...</p>
    </div>
    
    <div v-if="error" class="error-message">
      <i class="fas fa-exclamation-triangle"></i>
      <p>{{ error }}</p>
      <button @click="fetchAnalysisData" class="retry-btn">Retry</button>
    </div>
  </div>
</template>

<script>
import Chart from 'chart.js/auto';
import axios from 'axios';

export default {
  name: 'SystemMonitoring',
  data() {
    return {
      resultId: null,
      loading: false,
      error: null,
      activeMetric: 'runtime',
      metrics: [
        { id: 'runtime', name: 'Runtime' },
        { id: 'cpu_usage', name: 'CPU usage' },
        { id: 'memory_allocated', name: 'Memory usage' },
        { id: 'energy_consumption', name: 'Energy consumption' },
        { id: 'carbon_footprint', name: 'Carbon footprint' }
      ],
      summary: null,
      tasks: null,
      hostData: null,
      processData: null,
      powerChart: null,
      performanceChart: null,
      ioChart: null
    }
  },
  computed: {
    // 格式化数据以确保一致性
// 在 formattedProcessData 计算属性中，确保正确处理字段名
  formattedProcessData() {
    if (!this.processData || this.processData.length === 0) {
      // 如果processData为空但tasks有数据，使用tasks作为processData
      if (this.tasks && this.tasks.length > 0) {
        return this.tasks.map(task => ({
          process: task.process || 'Unknown',
          tasks: task.tasks || 0,
          runtime: task.runtime || 0,
          cpu_usage: task.cpuUsage || task.cpu_usage || 0,
          memory_allocated: task.memoryAllocated || task.memory_allocated || 0,
          io_volume: task.ioVolume || task.io_volume || 0,
          energy_consumption: task.energyConsumption || task.energy_consumption || 0,
          carbon_footprint: task.carbonFootprint || task.carbon_footprint || 0,
          read_gb: task.readGb || task.read_gb || 0,
          write_gb: task.writeGb || task.write_gb || 0,
          hardware: task.hardware || 'default'
        }));
      }
      return [];
    }
    return this.processData.map(process => ({
      process: process.process || 'Unknown',
      tasks: process.tasks || 0,
      runtime: process.runtime || 0,
      cpu_usage: process.cpuUsage || process.cpu_usage || 0,
      memory_allocated: process.memoryAllocated || process.memory_allocated || 0,
      io_volume: process.ioVolume || process.io_volume || 0,
      energy_consumption: process.energyConsumption || process.energy_consumption || 0,
      carbon_footprint: process.carbonFootprint || process.carbon_footprint || 0,
      read_gb: process.readGb || process.read_gb || 0,
      write_gb: process.writeGb || process.write_gb || 0,
      hardware: process.hardware || 'default'
    }));
  },
    
    sortedProcesses() {
      if (!this.formattedProcessData) return [];
      
      return [...this.formattedProcessData].sort((a, b) => {
        if (this.activeMetric === 'runtime') return (b.runtime || 0) - (a.runtime || 0);
        if (this.activeMetric === 'cpu_usage') return (b.cpu_usage || 0) - (a.cpu_usage || 0);
        if (this.activeMetric === 'memory_allocated') return (b.memory_allocated || 0) - (a.memory_allocated || 0);
        if (this.activeMetric === 'energy_consumption') return (b.energy_consumption || 0) - (a.energy_consumption || 0);
        return (b.carbon_footprint || 0) - (a.carbon_footprint || 0);
      });
    },
    
    metricLabel() {
      const metric = this.metrics.find(m => m.id === this.activeMetric);
      return metric ? metric.name : '';
    },
    
    // 计算加权平均CPU使用率（按运行时间和任务数量加权）
    avgCpuUsage() {
      if (!this.formattedProcessData || this.formattedProcessData.length === 0) return 0;
      
      let totalWeightedCpu = 0;
      let totalWeight = 0;
      
      this.formattedProcessData.forEach(process => {
        const weight = (process.runtime || 0) * (process.tasks || 1); // 组合权重
        totalWeightedCpu += (process.cpu_usage || process.cpuUsage || 0) * weight;
        totalWeight += weight;
      });
      
      return totalWeight > 0 ? totalWeightedCpu / totalWeight : 0;
    },

    // 计算加权平均内存使用（按运行时间和任务数量加权）
    avgMemoryUsage() {
      if (!this.formattedProcessData || this.formattedProcessData.length === 0) return 0;
      
      let totalWeightedMemory = 0;
      let totalWeight = 0;
      
      this.formattedProcessData.forEach(process => {
        const weight = (process.runtime || 0) * (process.tasks || 1); // 组合权重
        totalWeightedMemory += (process.memory_allocated || process.memoryAllocated || 0) * weight;
        totalWeight += weight;
      });
      
      return totalWeight > 0 ? totalWeightedMemory / totalWeight : 0;
    },
    
    // 检查是否有IO数据
    hasIOData() {
      if (!this.formattedProcessData || this.formattedProcessData.length === 0) return false;
      return this.formattedProcessData.some(process => 
        (process.read_gb !== undefined && process.read_gb > 0) || 
        (process.write_gb !== undefined && process.write_gb > 0)
      );
    }
  },
  mounted() {
    this.resultId = this.$route.query.resultId;
    if (this.resultId) {
      this.fetchAnalysisData();
    } else {
      this.error = "No analysis result ID provided. Please analyze a trace file first.";
    }
    window.addEventListener('resize', this.handleResize);
  },
  activated() {
    if (this.resultId) {
      this.fetchAnalysisData();
    }
  },
  watch: {
    '$route'(to, from) {
      if (to.query.resultId !== from.query.resultId) {
        this.resultId = to.query.resultId;
        if (this.resultId) {
          this.fetchAnalysisData();
        } else {
          this.error = "No analysis result ID provided. Please analyze a trace file first.";
        }
      }
    },
    activeMetric() {
      this.renderPerformanceChart();
    }
  },
  methods: {
    async fetchAnalysisData() {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await axios.get(`/api/visualization/${this.resultId}`);
        const data = response.data;
        console.log("API Response:", data);
        
        // 确保数据结构正确
        this.summary = data.summary || {};
        this.tasks = data.tasks || [];
        this.hostData = data.hostData || {};
        this.processData = data.processData || [];
        
        // 如果processData为空但tasks有数据，使用tasks作为processData
        if (this.processData.length === 0 && this.tasks.length > 0) {
          this.processData = this.tasks;
        }
        
        // 检查数据是否异常
        if (this.summary && this.summary.totalEnergy > 1e9) {
          this.error = "数据异常: 能源消耗值过高，可能是单位转换错误";
          this.loading = false;
          return;
        }
        
        // 等待DOM更新完成后渲染图表
        this.$nextTick(() => {
          setTimeout(() => {
            if (this.hostData && this.hostData.hosts && this.hostData.hosts.length > 0) {
              this.renderPowerChart();
            }
            if (this.formattedProcessData && this.formattedProcessData.length > 0) {
              this.renderPerformanceChart();
            }
            if (this.hasIOData) {
              this.renderIOChart();
            }
          }, 100); // 添加小延迟确保DOM完全渲染
        });
        
      } catch (error) {
        console.error("Error loading analysis data:", error);
        if (error.response && error.response.status === 404) {
          this.error = "Analysis result not found. It may have been deleted or the ID is incorrect.";
        } else {
          this.error = "Failed to load analysis data. Please try again later.";
        }
      } finally {
        this.loading = false;
      }
    },
    
    // 渲染主机功耗图表
    renderPowerChart() {
      const ctx = this.$refs.powerChart;
      const self = this;
      
      if (!ctx) {
        console.error("Power chart canvas element not found");
        return;
      }
      
      if (this.powerChart) {
        this.powerChart.destroy();
      }
      
      // 确保数据存在
      if (!this.hostData.hosts || !this.hostData.energyConsumptions) {
        console.error("Host data is incomplete");
        return;
      }
      
      // 确保数据是数组格式
      const hosts = Array.isArray(this.hostData.hosts) ? this.hostData.hosts : [];
      const energyData = Array.isArray(this.hostData.energyConsumptions) ? 
        this.hostData.energyConsumptions : [];
      
      this.powerChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: hosts,
          datasets: [{
            label: 'Energy Consumption (Watt-hours)',
            data: energyData,
            backgroundColor: '#0097a7',
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  const host = context.label;
                  const processes = self.hostData.processes && self.hostData.processes[host] ? 
                    self.hostData.processes[host] : "No process data";
                  return [
                    `Energy: ${context.parsed.y.toFixed(1)} Watt-hours`,
                    `Processes: ${processes}`
                  ];
                }
              }
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              title: {
                display: true,
                text: 'Energy Consumption (Watt-hours)'
              }
            },
            x: {
              title: {
                display: true,
                text: 'Host name'
              }
            }
          }
        }
      });
    },
    
    renderPerformanceChart() {
      const ctx = this.$refs.performanceChart;
      if (!ctx || !this.formattedProcessData || this.formattedProcessData.length === 0) {
        console.error("Performance chart canvas element not found or no process data");
        return;
      }
      
      if (this.performanceChart) {
        this.performanceChart.destroy();
      }
      
      const displayProcesses = this.sortedProcesses.slice(0, 15);
      
      const data = displayProcesses.map(process => {
        if (this.activeMetric === 'runtime') return process.runtime || 0;
        if (this.activeMetric === 'cpu_usage') return process.cpu_usage || 0;
        if (this.activeMetric === 'memory_allocated') return process.memory_allocated || 0;
        if (this.activeMetric === 'energy_consumption') return process.energy_consumption || 0;
        if (this.activeMetric === 'carbon_footprint') return process.carbon_footprint || 0;
        return 0;
      });
      
      const labels = displayProcesses.map(process => process.process);
      const colors = labels.map((_, i) => i % 2 === 0 ? '#0097a7' : '#4dd0e1');
      
      let unit = '';
      if (this.activeMetric === 'runtime') {
        unit = 'min';
      } else if (this.activeMetric === 'cpu_usage') {
        unit = '%';
      } else if (this.activeMetric === 'memory_allocated') {
        unit = 'GB';
      } else if (this.activeMetric === 'energy_consumption') {
        unit = 'Wh';
      } else if (this.activeMetric === 'carbon_footprint') {
        unit = 'kgCO₂eq';
      }
      
      this.performanceChart = new Chart(ctx, {
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
                  const process = displayProcesses[context.dataIndex];
                  let value = context.parsed.x;
                  return `${this.metricLabel}: ${value.toFixed(1)} ${unit} | Tasks: ${process.tasks || 0}`;
                }
              }
            }
          },
          scales: {
            x: {
              beginAtZero: true,
              title: {
                display: true,
                text: this.metricLabel + (unit ? ` (${unit})` : '')
              }
            }
          }
        }
      });
    },
    
    // 渲染IO图表
    renderIOChart() {
      const ctx = this.$refs.ioChart;
      if (!ctx || !this.formattedProcessData || this.formattedProcessData.length === 0) {
        console.error("IO chart canvas element not found or no process data");
        return;
      }
      
      if (this.ioChart) {
        this.ioChart.destroy();
      }
      
      // 获取IO数据，使用默认值0如果字段不存在
      const ioData = this.formattedProcessData.map(process => {
        const read = process.read_gb || 0;
        const write = process.write_gb || 0;
        
        return {
          process: process.process || 'Unknown',
          read: read,
          write: write,
          total: read + write
        };
      });
      
      // 按总IO量排序并取前10个
      const topProcesses = [...ioData]
        .sort((a, b) => b.total - a.total)
        .slice(0, 10);
      
      console.log("Top IO processes:", topProcesses);
      
      // 检查是否有非零的IO数据
      const hasNonZeroIO = topProcesses.some(p => p.read > 0 || p.write > 0);
      if (!hasNonZeroIO) {
        console.log("No non-zero IO data found");
        return;
      }
      
      const labels = topProcesses.map(p => p.process);
      const readData = topProcesses.map(p => p.read);
      const writeData = topProcesses.map(p => p.write);
      
      this.ioChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [
            {
              label: 'Read (GB)',
              data: readData,
              backgroundColor: '#0097a7'
            },
            {
              label: 'Write (GB)',
              data: writeData,
              backgroundColor: '#4dd0e1'
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            tooltip: {
              callbacks: {
                label: (context) => {
                  const datasetLabel = context.dataset.label;
                  const value = context.parsed.y.toFixed(3);
                  return `${datasetLabel}: ${value} GB`;
                }
              }
            }
          },
          scales: {
            x: {
              stacked: true
            },
            y: {
              stacked: true,
              title: {
                display: true,
                text: 'I/O (GB)'
              }
            }
          }
        }
      });
    },
    
    handleResize() {
      if (this.powerChart) this.powerChart.resize();
      if (this.performanceChart) this.performanceChart.resize();
      if (this.ioChart) this.ioChart.resize();
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
    if (this.powerChart) this.powerChart.destroy();
    if (this.performanceChart) this.performanceChart.destroy();
    if (this.ioChart) this.ioChart.destroy();
  }
}
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
  position: relative;
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

.section.summary {
  padding: 15px;
}

.section h2 {
  font-size: 1.5rem;
  margin-bottom: 20px;
  color: #555;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
  font-weight: 500;
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #ffffff;
  border-left: 5px solid;
  border-radius: 8px;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}
.card:hover { transform: translateY(-3px); }

.card:nth-child(1) { border-color: #0097a7; }
.card:nth-child(2) { border-color: #4dd0e1; }
.card:nth-child(3) { border-color: #b2ebf2; }
.card:nth-child(4) { border-color: #e0f7fa; }

.card-icon {
  font-size: 2.5rem;
  color: #e0e0e0;
}

.card-content h3 {
  font-size: 1.1rem;
  margin-bottom: 5px;
  color: #858796;
}

.card-content p {
  font-size: 1.6rem;
  font-weight: bold;
  color: #5a5c69;
  margin: 0;
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

.chart-container {
  position: relative;
  height: 400px;
  width: 100%;
  background: #ffffff;
  border-radius: 8px;
  padding: 15px;
  border: 1px solid #f0f0f0;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.spinner {
  border: 4px solid rgba(0, 151, 167, 0.3);
  border-radius: 50%;
  border-top: 4px solid #0097a7;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  background: #ffebee;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  color: #b71c1c;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.error-message i {
  font-size: 2rem;
  color: #d32f2f;
}

.retry-btn {
  padding: 8px 16px;
  background: #d32f2f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.retry-btn:hover {
  background: #b71c1c;
}

@media (max-width: 768px) {
  .chart-container {
    height: 350px;
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
  .summary-cards {
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