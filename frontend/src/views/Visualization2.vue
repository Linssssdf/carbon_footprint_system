<!-- <template>
  <div class="system-monitoring">
    <div class="header">
      <h1>System Monitoring and Performance Analysis</h1>
      <p class="subtitle">Host power consumption, process performance, and I/O analysis</p>
    </div>

    <div class="section summary">
      <h2>Workflow Performance Overview</h2>
      <div class="summary-cards">
        <div class="card">
          <div class="card-icon"><i class="fas fa-bolt"></i></div>
          <div class="card-content">
            <h3>Total energy consumption</h3>
            <p>{{ summary.totalEnergy.toFixed(1) }} kWh</p>
          </div>
        </div>
        <div class="card">
          <div class="card-icon"><i class="fas fa-cloud"></i></div>
          <div class="card-content">
            <h3>Carbon emissions</h3>
            <p>{{ summary.carbonEmission.toFixed(1) }} kgCO₂eq</p>
          </div>
        </div>
        <div class="card">
          <div class="card-icon"><i class="fas fa-microchip"></i></div>
          <div class="card-content">
            <h3>CPU efficiency</h3>
            <p>{{ summary.cpuEfficiency }}%</p>
          </div>
        </div>
      </div>
    </div>
    
    <div class="section">
      <h2>Host power consumption distribution (Watt-hours)</h2>
      <div class="chart-container">
        <canvas id="powerChart" ref="powerChart"></canvas>
      </div>
    </div>
    
    <div class="section">
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
    
    <div class="section">
      <h2>Process I/O analysis (GB)</h2>
      <div class="chart-container">
        <canvas id="ioChart" ref="ioChart"></canvas>
      </div>
    </div>
    
    <div class="section recommendations">
      <h2>Optimization suggestions</h2>
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
        { id: 'cpu', name: 'CPU usage' },
        { id: 'mem', name: 'Memory usage' },
        { id: 'duration', name: 'Task duration' },
        { id: 'io', name: 'Total I/O' }
      ],
      summary: {
        totalEnergy: 64.1,
        carbonEmission: 22.4,
        cpuEfficiency: 66.0,
        dataTransfer: 0.0
      },
      powerData: {
        hosts: ['cpu01', 'cpu02', 'cpu03', 'cpu06', 'cpu07', 'cpu08', 'cpu09', 'cpu10'],
        consumption: [
          7150.85, 9212.23, 6247.05, 8777.83, 
          8006.59, 10263.75, 7703.86, 6720.55
        ],
        // 添加主机进程数据
        processes: {
          cpu01: "TRIMGALORE, BWA_MEM, SAMTOOLS_SORT, SAMTOOLS_INDEX, SAMTOOLS_FLAGSTAT, SAMTOOLS_IDXSTATS, SAMTOOLS_STATS, PICARD_MERGESAMFILES, PICARD_MARKDUPLICATES, BAM_FILTER, PRESEQ_LCEXTRAP, BAM_REMOVE_ORPHANS, BEDTOOLS_GENOMECOV, UCSC_BEDGRAPHTOBIGWIG, PICARD_COLLECTMULTIPLEMETRICS, MACS2_CALLPEAK, PHANTOMPEAKQUALTOOLS, MULTIQC_CUSTOM_PEAKS, FRIP_SCORE, HOMER_ANNOTATEPEAKS_MACS2, DEEPTOOLS_PLOTFINGERPRINT, SUBREAD_FEATURECOUNTS, DESEQ2_QC, DEEPTOOLS_COMPUTEMATRIX, DEEPTOOLS_PLOTPROFILE, DEEPTOOLS_PLOTHEATMAP, MULTIQC_CUSTOM_PHANTOMPEAKQUALTOOLS",
          cpu02: "FASTQC, TRIMGALORE, BWA_MEM, SAMTOOLS_SORT, SAMTOOLS_INDEX, SAMTOOLS_IDXSTATS, SAMTOOLS_FLAGSTAT, SAMTOOLS_STATS, PICARD_MERGESAMFILES, PICARD_MARKDUPLICATES, PRESEQ_LCEXTRAP, BAM_FILTER, BAM_REMOVE_ORPHANS, PICARD_COLLECTMULTIPLEMETRICS, BEDTOOLS_GENOMECOV, MACS2_CALLPEAK, PHANTOMPEAKQUALTOOLS, FRIP_SCORE, MULTIQC_CUSTOM_PEAKS, HOMER_ANNOTATEPEAKS_MACS2, UCSC_BEDGRAPHTOBIGWIG, DEEPTOOLS_PLOTFINGERPRINT, SUBREAD_FEATURECOUNTS, DESEQ2_QC, DEEPTOOLS_COMPUTEMATRIX, DEEPTOOLS_PLOTPROFILE, DEEPTOOLS_PLOTHEATMAP, MULTIQC_CUSTOM_PHANTOMPEAKQUALTOOLS",
          cpu03: "TRIMGALORE, BWA_MEM, SAMTOOLS_SORT, SAMTOOLS_INDEX, SAMTOOLS_FLAGSTAT, SAMTOOLS_IDXSTATS, SAMTOOLS_STATS, PICARD_MERGESAMFILES, PICARD_MARKDUPLICATES, PRESEQ_LCEXTRAP, BAM_FILTER, BAM_REMOVE_ORPHANS, DEEPTOOLS_PLOTFINGERPRINT, PICARD_COLLECTMULTIPLEMETRICS, MACS2_CALLPEAK, BEDTOOLS_GENOMECOV, FRIP_SCORE, UCSC_BEDGRAPHTOBIGWIG, PHANTOMPEAKQUALTOOLS, MULTIQC_CUSTOM_PEAKS, HOMER_ANNOTATEPEAKS_MACS2, MACS2_CONSENSUS, PLOT_MACS2_QC, HOMER_ANNOTATEPEAKS_CONSENSUS, ANNOTATE_BOOLEAN_PEAKS, DEEPTOOLS_COMPUTEMATRIX, DEEPTOOLS_PLOTPROFILE, DEEPTOOLS_PLOTHEATMAP, MULTIQC_CUSTOM_PHANTOMPEAKQUALTOOLS",
          cpu06: "TRIMGALORE, BWA_MEM, SAMTOOLS_SORT, SAMTOOLS_INDEX, SAMTOOLS_IDXSTATS, SAMTOOLS_FLAGSTAT, SAMTOOLS_STATS, PICARD_MERGESAMFILES, PICARD_MARKDUPLICATES, PRESEQ_LCEXTRAP, BAM_FILTER, BAM_REMOVE_ORPHANS, PICARD_COLLECTMULTIPLEMETRICS, UCSC_BEDGRAPHTOBIGWIG, BEDTOOLS_GENOMECOV, MACS2_CALLPEAK, FRIP_SCORE, PHANTOMPEAKQUALTOOLS, DEEPTOOLS_PLOTFINGERPRINT, MULTIQC_CUSTOM_PEAKS, HOMER_ANNOTATEPEAKS_MACS2, DESEQ2_QC, HOMER_ANNOTATEPEAKS_CONSENSUS, ANNOTATE_BOOLEAN_PEAKS, DEEPTOOLS_COMPUTEMATRIX, DEEPTOOLS_PLOTPROFILE, DEEPTOOLS_PLOTHEATMAP, MULTIQC_CUSTOM_PHANTOMPEAKQUALTOOLS",
          cpu07: "FASTQC, TRIMGALORE, BWA_MEM, SAMTOOLS_SORT, SAMTOOLS_INDEX, SAMTOOLS_FLAGSTAT, SAMTOOLS_STATS, SAMTOOLS_IDXSTATS, PICARD_MERGESAMFILES, PICARD_MARKDUPLICATES, PRESEQ_LCEXTRAP, BAM_FILTER, BAM_REMOVE_ORPHANS, PICARD_COLLECTMULTIPLEMETRICS, MACS2_CALLPEAK, BEDTOOLS_GENOMECOV, UCSC_BEDGRAPHTOBIGWIG, FRIP_SCORE, PHANTOMPEAKQUALTOOLS, MULTIQC_CUSTOM_PEAKS, HOMER_ANNOTATEPEAKS_MACS2, DEEPTOOLS_PLOTFINGERPRINT, MACS2_CONSENSUS, SUBREAD_FEATURECOUNTS, DESEQ2_QC, HOMER_ANNOTATEPEAKS_CONSENSUS, ANNOTATE_BOOLEAN_PEAKS, DEEPTOOLS_COMPUTEMATRIX, DEEPTOOLS_PLOTPROFILE, DEEPTOOLS_PLOTHEATMAP, MULTIQC_CUSTOM_PHANTOMPEAKQUALTOOLS",
          cpu08: "FASTQC, TRIMGALORE, BWA_MEM, SAMTOOLS_INDEX, SAMTOOLS_SORT, SAMTOOLS_IDXSTATS, SAMTOOLS_FLAGSTAT, SAMTOOLS_STATS, PICARD_MERGESAMFILES, PICARD_MARKDUPLICATES, PRESEQ_LCEXTRAP, BAM_FILTER, BAM_REMOVE_ORPHANS, PICARD_COLLECTMULTIPLEMETRICS, MACS2_CALLPEAK, BEDTOOLS_GENOMECOV, FRIP_SCORE, PHANTOMPEAKQUALTOOLS, MULTIQC_CUSTOM_PEAKS, HOMER_ANNOTATEPEAKS_MACS2, UCSC_BEDGRAPHTOBIGWIG, DEEPTOOLS_PLOTFINGERPRINT, DESEQ2_QC, SUBREAD_FEATURECOUNTS, DEEPTOOLS_COMPUTEMATRIX, DEEPTOOLS_PLOTPROFILE, DEEPTOOLS_PLOTHEATMAP, MULTIQC_CUSTOM_PHANTOMPEAKQUALTOOLS",
          cpu09: "FASTQC, TRIMGALORE, BWA_MEM, SAMTOOLS_SORT, SAMTOOLS_INDEX, SAMTOOLS_IDXSTATS, SAMTOOLS_FLAGSTAT, SAMTOOLS_STATS, PICARD_MERGESAMFILES, PICARD_MARKDUPLICATES, BAM_FILTER, PRESEQ_LCEXTRAP, BAM_REMOVE_ORPHANS, PICARD_COLLECTMULTIPLEMETRICS, BEDTOOLS_GENOMECOV, PHANTOMPEAKQUALTOOLS, UCSC_BEDGRAPHTOBIGWIG, MACS2_CALLPEAK, FRIP_SCORE, MULTIQC_CUSTOM_PEAKS, DEEPTOOLS_PLOTFINGERPRINT, HOMER_ANNOTATEPEAKS_MACS2, SUBREAD_FEATURECOUNTS, DESEQ2_QC, IGV, HOMER_ANNOTATEPEAKS_CONSENSUS, DEEPTOOLS_COMPUTEMATRIX, DEEPTOOLS_PLOTPROFILE, DEEPTOOLS_PLOTHEATMAP, MULTIQC_CUSTOM_PHANTOMPEAKQUALTOOLS",
          cpu10: "SAMPLESHEET_CHECK, GTF2BED, FASTQC, CUSTOM_GETCHROMSIZES, GENOME_BLACKLIST_REGIONS, TRIMGALORE, BWA_MEM, SAMTOOLS_SORT, SAMTOOLS_INDEX, SAMTOOLS_FLAGSTAT, SAMTOOLS_IDXSTATS, SAMTOOLS_STATS, PICARD_MERGESAMFILES, PICARD_MARKDUPLICATES, PRESEQ_LCEXTRAP, BAM_FILTER, BAM_REMOVE_ORPHANS, PICARD_COLLECTMULTIPLEMETRICS, BEDTOOLS_GENOMECOV, UCSC_BEDGRAPHTOBIGWIG, MACS2_CALLPEAK, FRIP_SCORE, HOMER_ANNOTATEPEAKS_MACS2, PHANTOMPEAKQUALTOOLS, MULTIQC_CUSTOM_PEAKS, SUBREAD_FEATURECOUNTS, DEEPTOOLS_PLOTFINGERPRINT, PLOT_HOMER_ANNOTATEPEAKS, ANNOTATE_BOOLEAN_PEAKS, DEEPTOOLS_COMPUTEMATRIX, DEEPTOOLS_PLOTPROFILE, CUSTOM_DUMPSOFTWAREVERSIONS, DEEPTOOLS_PLOTHEATMAP, MULTIQC_CUSTOM_PHANTOMPEAKQUALTOOLS"
        }
      },
      processData: [
        { process: 'MULTIQC_CUSTOM_PHANTOMPEAKQUALTOOLS', tasks: 105, avgCpu: 864.5, avgMem: 4.0, avgDuration: 79.4, totalIo: 0.2, readGb: 0.172, writeGb: 0.003 },
        { process: 'DEEPTOOLS_COMPUTEMATRIX', tasks: 105, avgCpu: 695.8, avgMem: 5.3, avgDuration: 57.9, totalIo: 97.8, readGb: 5.620, writeGb: 92.199 },
        { process: 'BWA_MEM', tasks: 105, avgCpu: 990.7, avgMem: 32.0, avgDuration: 34.4, totalIo: 164.4, readGb: 34.449, writeGb: 129.925 },
        { process: 'DEEPTOOLS_PLOTFINGERPRINT', tasks: 70, avgCpu: 591.7, avgMem: 4.0, avgDuration: 58.9, totalIo: 20.3, readGb: 20.063, writeGb: 0.252 },
        { process: 'SAMTOOLS_SORT', tasks: 210, avgCpu: 412.8, avgMem: 5.9, avgDuration: 9.7, totalIo: 406.9, readGb: 105.814, writeGb: 301.114 },
        { process: 'TRIMGALORE', tasks: 105, avgCpu: 276.5, avgMem: 2.9, avgDuration: 9.0, totalIo: 86.4, readGb: 6.026, writeGb: 80.365 },
        { process: 'PICARD_MARKDUPLICATES', tasks: 105, avgCpu: 106.6, avgMem: 19.7, avgDuration: 8.0, totalIo: 161.9, readGb: 57.189, writeGb: 104.729 },
        { process: 'SAMTOOLS_STATS', tasks: 315, avgCpu: 132.2, avgMem: 1.0, avgDuration: 1.6, totalIo: 1.9, readGb: 1.846, writeGb: 0.014 },
        { process: 'BEDTOOLS_GENOMECOV', tasks: 105, avgCpu: 121.7, avgMem: 4.3, avgDuration: 5.1, totalIo: 98.6, readGb: 0.363, writeGb: 98.228 },
        { process: 'SAMTOOLS_FLAGSTAT', tasks: 315, avgCpu: 146.3, avgMem: 1.0, avgDuration: 0.8, totalIo: 4.8, readGb: 4.787, writeGb: 0.004 },
        { process: 'PRESEQ_LCEXTRAP', tasks: 105, avgCpu: 100.8, avgMem: 1.0, avgDuration: 2.6, totalIo: 0.7, readGb: 0.651, writeGb: 0.049 },
        { process: 'FASTQC', tasks: 105, avgCpu: 86.4, avgMem: 1.6, avgDuration: 2.6, totalIo: 0.5, readGb: 0.235, writeGb: 0.253 },
        { process: 'PICARD_COLLECTMULTIPLEMETRICS', tasks: 105, avgCpu: 109.9, avgMem: 2.4, avgDuration: 2.1, totalIo: 0.6, readGb: 0.204, writeGb: 0.410 },
        { process: 'SUBREAD_FEATURECOUNTS', tasks: 10, avgCpu: 470.5, avgMem: 1.0, avgDuration: 2.6, totalIo: 3.1, readGb: 3.037, writeGb: 0.015 },
        { process: 'UCSC_BEDGRAPHTOBIGWIG', tasks: 105, avgCpu: 99.4, avgMem: 1.0, avgDuration: 1.2, totalIo: 30.0, readGb: 8.902, writeGb: 21.129 }
      ],
      recommendations: [
        'Optimizing Data Locality: Improving data locality strategies for ALIGN and PEAK_CALL tasks.',
        'Memory Allocation Optimization: Reallocating memory resources from tasks with low memory utilization (current average utilization is 23%).',
        'Reducing Scheduling Latency: Reducing scheduling latency by an average of 389 seconds through batched task submission.',
        'Data Compression: Implementing data compression for network-intensive operations (total I/O 980.8 GB).',
        'Load Balancing: Balancing workloads across hosts to reduce power consumption peaks (highest power consumption host: cpu08).',
        'Improving CPU Utilization: Addressing the issue of 6.6% of tasks with CPU utilization below 40%.',
        'Low-Carbon Scheduling: Scheduling compute-intensive tasks during low-carbon periods (current carbon emissions 22.4 kgCO₂eq).'
      ],
      powerChart: null,
      performanceChart: null,
      ioChart: null
    }
  },
  computed: {
    sortedProcesses() {
      return [...this.processData].sort((a, b) => {
        if (this.activeMetric === 'cpu') return b.avgCpu - a.avgCpu;
        if (this.activeMetric === 'mem') return b.avgMem - a.avgMem;
        if (this.activeMetric === 'duration') return b.avgDuration - a.avgDuration;
        return b.totalIo - a.totalIo;
      });
    },
    metricLabel() {
      return this.metrics.find(m => m.id === this.activeMetric).name;
    }
  },
  mounted() {
    this.renderPowerChart();
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
    renderPowerChart() {
      const ctx = this.$refs.powerChart;
      const self = this;
      
      if (this.powerChart) {
        this.powerChart.destroy();
      }
      
      this.powerChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: this.powerData.hosts,
          datasets: [{
            label: 'Energy Consumption (Watt-hours)',
            data: this.powerData.consumption,
            backgroundColor: '#0097a7',
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  const host = context.label;
                  const processes = self.powerData.processes[host];
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
      
      if (this.performanceChart) {
        this.performanceChart.destroy();
      }
      
      const data = this.sortedProcesses.map(p => {
        if (this.activeMetric === 'cpu') return p.avgCpu;
        if (this.activeMetric === 'mem') return p.avgMem;
        if (this.activeMetric === 'duration') return p.avgDuration;
        if (this.activeMetric === 'io') return p.totalIo;
        return 0;
      });
      
      const labels = this.sortedProcesses.map(p => p.process);
      const colors = labels.map((_, i) => i % 2 === 0 ? '#0097a7' : '#4dd0e1');
      
      let unit = '';
      if (this.activeMetric === 'cpu') {
        unit = '%';
      } else if (this.activeMetric === 'mem') {
        unit = 'GB';
      } else if (this.activeMetric === 'duration') {
        unit = 'min';
      } else if (this.activeMetric === 'io') {
        unit = 'GB';
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
                  const process = this.sortedProcesses[context.dataIndex];
                  let value = context.parsed.x;
                  return `${this.metricLabel}: ${value.toFixed(1)} ${unit} | Tasks: ${process.tasks}`;
                }
              }
            }
          },
          scales: {
            x: {
              beginAtZero: true,
              title: {
                display: true,
                text: this.metricLabel
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
</style> -->

<template>
  <div class="system-monitoring">
    <div class="header">
      <h1>System Monitoring and Performance Analysis</h1>
      <p class="subtitle">Host power consumption, process performance, and I/O analysis</p>
    </div>

    <div class="section summary" v-if="summary">
      <h2>Workflow Performance Overview</h2>
      <div class="summary-cards">
        <div class="card">
          <div class="card-icon"><i class="fas fa-bolt"></i></div>
          <div class="card-content">
            <h3>Total energy consumption</h3>
            <p>{{ summary.totalEnergy.toFixed(1) }} kWh</p>
          </div>
        </div>
        <div class="card">
          <div class="card-icon"><i class="fas fa-cloud"></i></div>
          <div class="card-content">
            <h3>Carbon emissions</h3>
            <p>{{ summary.carbonEmission.toFixed(1) }} kgCO₂eq</p>
          </div>
        </div>
        <div class="card">
          <div class="card-icon"><i class="fas fa-microchip"></i></div>
          <div class="card-content">
            <h3>CPU efficiency</h3>
            <p>{{ summary.cpuEfficiency }}%</p>
          </div>
        </div>
      </div>
    </div>
    
    <div class="section" v-if="powerData && powerData.hosts.length > 0">
      <h2>Host power consumption distribution (Watt-hours)</h2>
      <div class="chart-container">
        <canvas id="powerChart" ref="powerChart"></canvas>
      </div>
    </div>
    
    <div class="section" v-if="processData && processData.length > 0">
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
    
    <div class="section" v-if="processData && processData.length > 0">
      <h2>Process I/O analysis (GB)</h2>
      <div class="chart-container">
        <canvas id="ioChart" ref="ioChart"></canvas>
      </div>
    </div>
    
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
      <p>Loading analysis data...</p>
    </div>
    
    <div v-if="error" class="error-message">
      <i class="fas fa-exclamation-triangle"></i>
      <p>{{ error }}</p>
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
      activeMetric: 'cpu',
      metrics: [
        { id: 'cpu', name: 'CPU usage' },
        { id: 'mem', name: 'Memory usage' },
        { id: 'duration', name: 'Task duration' },
        { id: 'io', name: 'Total I/O' }
      ],
      summary: null,
      powerData: null,
      processData: null,
      powerChart: null,
      performanceChart: null,
      ioChart: null
    }
  },
  computed: {
    sortedProcesses() {
      if (!this.processData) return [];
      
      return [...this.processData].sort((a, b) => {
        if (this.activeMetric === 'cpu') return b.avgCpu - a.avgCpu;
        if (this.activeMetric === 'mem') return b.avgMem - a.avgMem;
        if (this.activeMetric === 'duration') return b.avgDuration - a.avgDuration;
        return b.totalIo - a.totalIo;
      });
    },
    metricLabel() {
      const metric = this.metrics.find(m => m.id === this.activeMetric);
      return metric ? metric.name : '';
    }
  },
  mounted() {
    this.resultId = this.$route.query.resultId;
    if (this.resultId) {
      this.fetchAnalysisData();
    } else {
      this.error = "No analysis result ID provided. Please analyze a trace file first.";
    }
  },
  watch: {
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
        
        // 更新组件数据
        this.summary = data.summary || {
          totalEnergy: 0,
          carbonEmission: 0,
          cpuEfficiency: 0
        };
        
        this.powerData = data.powerData || {
          hosts: [],
          consumption: [],
          processes: {}
        };
        
        this.processData = data.processData || [];
        
        // 渲染图表
        this.$nextTick(() => {
          this.renderPowerChart();
          this.renderPerformanceChart();
          this.renderIOChart();
        });
        
      } catch (error) {
        console.error("Error loading analysis data:", error);
        this.error = "Failed to load analysis data. Please try again later.";
      } finally {
        this.loading = false;
      }
    },
    renderPowerChart() {
      const ctx = this.$refs.powerChart;
      if (!ctx || !this.powerData || this.powerData.hosts.length === 0) return;
      
      const self = this;
      
      if (this.powerChart) {
        this.powerChart.destroy();
      }
      
      this.powerChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: this.powerData.hosts,
          datasets: [{
            label: 'Energy Consumption (Watt-hours)',
            data: this.powerData.consumption,
            backgroundColor: '#0097a7',
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  const host = context.label;
                  const processes = self.powerData.processes[host] || 'No process data';
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
      if (!ctx || !this.processData || this.processData.length === 0) return;
      
      if (this.performanceChart) {
        this.performanceChart.destroy();
      }
      
      const data = this.sortedProcesses.map(p => {
        if (this.activeMetric === 'cpu') return p.avgCpu || 0;
        if (this.activeMetric === 'mem') return p.avgMem || 0;
        if (this.activeMetric === 'duration') return p.avgDuration || 0;
        if (this.activeMetric === 'io') return p.totalIo || 0;
        return 0;
      });
      
      const labels = this.sortedProcesses.map(p => p.process || 'Unknown');
      const colors = labels.map((_, i) => i % 2 === 0 ? '#0097a7' : '#4dd0e1');
      
      let unit = '';
      if (this.activeMetric === 'cpu') {
        unit = '%';
      } else if (this.activeMetric === 'mem') {
        unit = 'GB';
      } else if (this.activeMetric === 'duration') {
        unit = 'min';
      } else if (this.activeMetric === 'io') {
        unit = 'GB';
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
                  const process = this.sortedProcesses[context.dataIndex];
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
                text: this.metricLabel
              }
            }
          }
        }
      });
    },
    renderIOChart() {
      const ctx = this.$refs.ioChart;
      if (!ctx || !this.processData || this.processData.length === 0) return;
      
      if (this.ioChart) {
        this.ioChart.destroy();
      }
      
      // 只显示前10个I/O最多的进程
      const topProcesses = [...this.processData]
        .filter(p => p.readGb !== undefined && p.writeGb !== undefined)
        .sort((a, b) => ((b.readGb || 0) + (b.writeGb || 0)) - ((a.readGb || 0) + (a.writeGb || 0)))
        .slice(0, 10);
      
      if (topProcesses.length === 0) return;
      
      const labels = topProcesses.map(p => p.process || 'Unknown');
      const readData = topProcesses.map(p => p.readGb || 0);
      const writeData = topProcesses.map(p => p.writeGb || 0);
      
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