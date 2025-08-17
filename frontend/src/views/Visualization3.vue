<template>
  <div class="system-monitoring">
    <div class="header">
      <h1>ChIP-Seq Workflow Performance Analysis Report</h1>
      <p class="subtitle">Overall workflow performance, task resource consumption, and optimization suggestions</p>
    </div>

    <div class="section summary">
      <h2>Workflow Performance Overview</h2>
      <div class="summary-cards">
        <div class="card">
          <div class="card-icon"><i class="fas fa-clock"></i></div>
          <div class="card-content">
            <h3>Total execution time</h3>
            <p>{{ summary.totalExecutionTime.toFixed(1) }} hours</p>
          </div>
        </div>
        <div class="card">
          <div class="card-icon"><i class="fas fa-microchip"></i></div>
          <div class="card-content">
            <h3>Average CPU utilization</h3>
            <p>{{ summary.avgCpu.toFixed(1) }}%</p>
            <span>(Peak: {{ summary.maxCpu.toFixed(1) }}%)</span>
          </div>
        </div>
        <div class="card">
          <div class="card-icon"><i class="fas fa-memory"></i></div>
          <div class="card-content">
            <h3>Average memory usage</h3>
            <p>{{ summary.avgMem.toFixed(2) }} GB</p>
            <span>(Peak: {{ summary.maxMem.toFixed(2) }} GB)</span>
          </div>
        </div>
        <div class="card">
          <div class="card-icon"><i class="fas fa-database"></i></div>
          <div class="card-content">
            <h3>Data reuse rate</h3>
            <p>{{ summary.dataReuseEfficiency }}%</p>
            <span>(Useless data copy: {{ summary.uselessDataCopies }}%)</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="section">
      <h2>Task Resource Consumption</h2>
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
    
    <div class="section">
      <h2>Task I/O Analysis (GB)</h2>
      <div class="chart-container">
        <canvas id="ioChart" ref="ioChart"></canvas>
      </div>
    </div>
    
    <div class="section recommendations">
      <h2>Optimization Suggestions</h2>
      <ul>
        <li v-for="(rec, index) in recommendations" :key="index">
          <i class="fas fa-lightbulb"></i> {{ rec }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import Chart from 'chart.js/auto';

export default {
  name: 'SystemMonitoring',
  data() {
    return {
      activeMetric: 'cpu',
      metrics: [
        { id: 'cpu', name: 'CPU Usage' },
        { id: 'mem', name: 'Memory Usage' },
        { id: 'duration', name: 'Task Duration' }
      ],
      summary: {
        totalExecutionTime: 62.09,
        avgCpu: 232.0,
        maxCpu: 793.8,
        avgMem: 2.04,
        maxMem: 11.10,
        totalRead: 478.47,
        totalWrite: 502.40,
        dataReuseEfficiency: 92.87,
        uselessDataCopies: 7.13
      },
      processData: [
        { process: 'BIGWIG', avgCpu: 120.4, avgMem: 1.87, maxMem: 1.88, readGb: 11.45, writeGb: 128.22, avgDuration: 96.5 },
        { process: 'BWA_MEM', avgCpu: 644.3, avgMem: 7.90, maxMem: 9.17, readGb: 80.74, writeGb: 58.20, avgDuration: 318.2 },
        { process: 'FASTQC', avgCpu: 121.5, avgMem: 0.70, maxMem: 1.03, readGb: 44.58, writeGb: 0.08, avgDuration: 53.7 },
        { process: 'MACS2', avgCpu: 104.0, avgMem: 0.60, maxMem: 4.02, readGb: 20.73, writeGb: 33.04, avgDuration: 114.9 },
        { process: 'MACS2_ANNOTATE', avgCpu: 99.7, avgMem: 1.25, maxMem: 1.36, readGb: 0.17, writeGb: 3.92, avgDuration: 44.9 },
        { process: 'MERGED_BAM', avgCpu: 132.1, avgMem: 6.64, maxMem: 11.10, readGb: 85.26, writeGb: 53.56, avgDuration: 213.9 },
        { process: 'MERGED_BAM_FILTER', avgCpu: 127.0, avgMem: 0.02, maxMem: 0.02, readGb: 25.12, writeGb: 28.99, avgDuration: 88.9 },
        { process: 'PHANTOMPEAKQUALTOOLS', avgCpu: 52.9, avgMem: 0.73, maxMem: 5.88, readGb: 20.93, writeGb: 46.48, avgDuration: 176.3 },
        { process: 'PICARD_METRICS', avgCpu: 122.4, avgMem: 0.99, maxMem: 1.41, readGb: 8.95, writeGb: 0.03, avgDuration: 34.7 },
        { process: 'PLOTFINGERPRINT', avgCpu: 460.9, avgMem: 1.08, maxMem: 1.39, readGb: 9.01, writeGb: 0.05, avgDuration: 53.1 },
        { process: 'PLOTPROFILE', avgCpu: 637.1, avgMem: 1.48, maxMem: 1.61, readGb: 3.18, writeGb: 13.62, avgDuration: 194.6 },
        { process: 'PRESEQ', avgCpu: 103.5, avgMem: 0.01, maxMem: 0.02, readGb: 30.77, writeGb: 0.02, avgDuration: 96.4 },
        { process: 'SORT_BAM', avgCpu: 159.4, avgMem: 2.24, maxMem: 5.35, readGb: 93.46, writeGb: 93.39, avgDuration: 74.9 },
        { process: 'TRIMGALORE', avgCpu: 346.9, avgMem: 1.44, maxMem: 3.96, readGb: 44.12, writeGb: 42.81, avgDuration: 97.6 }
      ],
      recommendations: [
        "Parallelize the 'BWA_MEM' stage to reduce bottlenecks (current duration: 318.2 hours)",
        "Optimize memory allocation for 'BWA_MEM' (current average: 7.9 GB, peak: 9.17 GB)",
        "Implement compressed intermediate formats to reduce I/O overhead (total I/O: 980.87 GB)",
        "Review data management for 'SORT_BAM' to minimize redundant transfers (186.85 GB I/O)",
        "Consider CPU affinity tuning for high-CPU tasks like 'BWA_MEM' (644.3% CPU) and 'PLOTPROFILE' (637.1% CPU)"
      ],
      performanceChart: null,
      ioChart: null
    }
  },
  computed: {
    sortedProcesses() {
      return [...this.processData].sort((a, b) => {
        if (this.activeMetric === 'cpu') return b.avgCpu - a.avgCpu;
        if (this.activeMetric === 'mem') return b.avgMem - a.avgMem;
        return b.avgDuration - a.avgDuration;
      });
    },
    metricLabel() {
      return this.metrics.find(m => m.id === this.activeMetric).name;
    }
  },
  mounted() {
    this.renderPerformanceChart();
    this.renderIOChart();
    window.addEventListener('resize', this.handleResize);
  },
  watch: {
    activeMetric() {
      this.renderPerformanceChart();
    }
  },
  methods: {
    renderPerformanceChart() {
      const ctx = this.$refs.performanceChart;
      
      if (this.performanceChart) {
        this.performanceChart.destroy();
      }
      
      const data = this.sortedProcesses.map(p => {
        if (this.activeMetric === 'cpu') return p.avgCpu;
        if (this.activeMetric === 'mem') return p.avgMem;
        return p.avgDuration; // Already in hours
      });
      
      const labels = this.sortedProcesses.map(p => p.process);
      const colors = labels.map((_, i) => i % 2 === 0 ? '#0097a7' : '#4dd0e1');
      
      let unit = '';
      if (this.activeMetric === 'cpu') {
        unit = '%';
      } else if (this.activeMetric === 'mem') {
        unit = 'GB';
      } else if (this.activeMetric === 'duration') {
        unit = 'hours';
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
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                title: (items) => items[0].label,
                label: (context) => {
                  let value = context.parsed.x;
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
                text: `${this.metricLabel} (${unit})`
              }
            }
          }
        }
      });
    },
    renderIOChart() {
      const ctx = this.$refs.ioChart;
      
      if (this.ioChart) {
        this.ioChart.destroy();
      }
      
      const topProcesses = [...this.processData]
        .sort((a, b) => (b.readGb + b.writeGb) - (a.readGb + a.writeGb))
        .slice(0, 10);
      
      const labels = topProcesses.map(p => p.process);
      const readData = topProcesses.map(p => p.readGb);
      const writeData = topProcesses.map(p => p.writeGb);
      
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
          plugins: {
            tooltip: {
              callbacks: {
                label: (context) => {
                  const datasetLabel = context.dataset.label;
                  const value = context.parsed.y.toFixed(2);
                  return `${datasetLabel}: ${value} GB`;
                }
              }
            }
          },
          scales: {
            y: {
              title: {
                display: true,
                text: 'I/O Volume (GB)'
              }
            }
          }
        }
      });
    },
    handleResize() {
      if (this.performanceChart) this.performanceChart.resize();
      if (this.ioChart) this.ioChart.resize();
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
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

.card-content span {
  font-size: 0.9rem;
  color: #b7b9cc;
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

.recommendations ul {
  list-style-type: none;
  padding: 0;
}

.recommendations li {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #555;
}

.recommendations li:last-child {
  border-bottom: none;
}

.recommendations i {
  color: #0097a7;
  font-size: 1.2rem;
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