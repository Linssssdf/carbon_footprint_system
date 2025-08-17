<template>
  <div class="workflow-page">
    <div class="section-header">
      <h2>Workflow Management</h2>
      <div class="workflow-filter">
        <button class="btn active">All Workflows</button>
        <button class="btn">Active</button>
        <button class="btn">Completed</button>
        <button class="btn">Failed</button>
      </div>
    </div>

    <div class="workflow-container">
      <div class="workflow-list">
        <div 
          class="workflow-card" 
          v-for="workflow in workflows" 
          :key="workflow.id"
          :class="{ 'selected': selectedWorkflow.id === workflow.id }"
          @click="selectWorkflow(workflow)"
        >
          <div class="workflow-info">
            <h3>{{ workflow.name }}</h3>
            <p>{{ workflow.description }}</p>
            <div class="workflow-meta">
              <span><i class="fas fa-cloud"></i> {{ workflow.co2e }} kgCO₂e</span>
              <span><i class="fas fa-bolt"></i> {{ workflow.energy }} kWh</span>
              <span><i class="fas fa-clock"></i> {{ formatRuntime(workflow.runtime) }}</span>
            </div>
          </div>
          <div class="workflow-status" :class="workflow.statusClass">
            {{ workflow.status }}
          </div>
        </div>
      </div>

      <div class="workflow-details">
        <div class="workflow-header">
          <h3>{{ selectedWorkflow.name }}</h3>
          <div class="workflow-actions">
            <button class="btn btn-run"><i class="fas fa-play"></i> Run Workflow</button>
            <button class="btn btn-edit"><i class="fas fa-cog"></i> Configure</button>
          </div>
        </div>

        <div class="workflow-stats">
          <div class="stat-card">
            <div class="stat-icon">
              <i class="fas fa-cloud"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ selectedWorkflow.co2e }} kgCO₂e</div>
              <div class="stat-label">Carbon Emissions</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">
              <i class="fas fa-bolt"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ selectedWorkflow.energy }} kWh</div>
              <div class="stat-label">Energy Consumption</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">
              <i class="fas fa-clock"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatRuntime(selectedWorkflow.runtime) }}</div>
              <div class="stat-label">Total Runtime</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon">
              <i class="fas fa-microchip"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ selectedWorkflow.cpuEfficiency }}%</div>
              <div class="stat-label">CPU Efficiency</div>
            </div>
          </div>
        </div>

        <div class="workflow-content">
          <div class="task-sequence">
            <h3>Task Sequence</h3>
            <div class="sequence-container">
              <div 
                class="task-node" 
                v-for="(task, index) in selectedWorkflow.tasks" 
                :key="task.id"
                :class="getTaskClass(task)"
              >
                <div class="task-header">
                  <div class="task-number">{{ index + 1 }}</div>
                  <div class="task-title">{{ task.name }}</div>
                  <div class="task-status" :class="task.statusClass">{{ task.status }}</div>
                </div>
                <div class="task-details">
                  <div class="task-meta">
                    <span><i class="fas fa-clock"></i> {{ task.runtime }} sec</span>
                    <span><i class="fas fa-microchip"></i> {{ task.cpu_usage }}%</span>
                    <span><i class="fas fa-memory"></i> {{ task.memory }} GB</span>
                    <span><i class="fas fa-cloud"></i> {{ task.co2e }} kg</span>
                  </div>
                </div>
                
                <!-- Task dependencies -->
                <div class="task-dependencies" v-if="task.dependencies && task.dependencies.length">
                  <div class="dependency" v-for="dep in task.dependencies" :key="dep">
                    <div class="dependency-line"></div>
                    <div class="dependency-label">Step {{ dep }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="workflow-charts">
            <div class="chart-card">
              <h4>Resource Utilization</h4>
              <div class="chart-container">
                <canvas ref="resourceChart"></canvas>
              </div>
            </div>
            
            <div class="chart-card">
              <h4>Task Durations</h4>
              <div class="chart-container">
                <canvas ref="durationChart"></canvas>
              </div>
            </div>
          </div>
        </div>

        <div class="optimization-section">
          <h3><i class="fas fa-lightbulb"></i> Optimization Suggestions</h3>
          <ul class="suggestion-list">
            <li v-for="(suggestion, index) in selectedWorkflow.optimizations" :key="index">
              <i class="fas fa-check-circle"></i> {{ suggestion }}
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Chart } from 'chart.js/auto';

export default {
  data() {
    return {
      selectedWorkflow: {},
      workflows: [
        {
          id: 1,
          name: 'ChIP-Seq Analysis',
          description: 'Chromatin Immunoprecipitation Sequencing workflow',
          co2e: '0.005347',
          energy: '5.213',
          runtime: 3600,
          cpuEfficiency: 66.0,
          status: 'Completed',
          statusClass: 'status-completed',
          optimizations: [
            'Parallelize BWA_MEM alignment stages to reduce runtime',
            'Optimize memory allocation for SAMTOOLS_SORT tasks',
            'Schedule compute-intensive tasks during low-carbon periods',
            'Use compressed intermediate formats to reduce I/O'
          ],
          tasks: [
            {
              id: 1,
              name: 'FASTQC',
              runtime: 120,
              cpu_usage: 86.4,
              memory: 1.6,
              co2e: '0.00001256',
              status: 'Completed',
              statusClass: 'completed',
              dependencies: []
            },
            {
              id: 2,
              name: 'TRIMGALORE',
              runtime: 540,
              cpu_usage: 276.5,
              memory: 2.9,
              co2e: '0.00001832',
              status: 'Completed',
              statusClass: 'completed',
              dependencies: [1]
            },
            {
              id: 3,
              name: 'BWA_MEM',
              runtime: 2064,
              cpu_usage: 990.7,
              memory: 32.0,
              co2e: '0.00003521',
              status: 'Completed',
              statusClass: 'completed',
              dependencies: [2]
            },
            {
              id: 4,
              name: 'SAMTOOLS_SORT',
              runtime: 582,
              cpu_usage: 412.8,
              memory: 5.9,
              co2e: '0.00005207',
              status: 'Completed',
              statusClass: 'completed',
              dependencies: [3]
            },
            {
              id: 5,
              name: 'MACS2_CALLPEAK',
              runtime: 294,
              cpu_usage: 97.3,
              memory: 1.8,
              co2e: '0.00002145',
              status: 'Completed',
              statusClass: 'completed',
              dependencies: [4]
            }
          ]
        },
        {
          id: 2,
          name: 'RNA-Seq Analysis',
          description: 'RNA Sequencing differential expression analysis',
          co2e: '0.004812',
          energy: '4.692',
          runtime: 2800,
          cpuEfficiency: 58.3,
          status: 'Running',
          statusClass: 'status-running',
          optimizations: [
            'Increase memory allocation for STAR alignment step',
            'Optimize disk I/O for large BAM files',
            'Combine featureCounts with alignment step'
          ],
          tasks: [
            {
              id: 1,
              name: 'FASTQC',
              runtime: 110,
              cpu_usage: 82.1,
              memory: 1.5,
              co2e: '0.00001142',
              status: 'Completed',
              statusClass: 'completed',
              dependencies: []
            },
            {
              id: 2,
              name: 'STAR Alignment',
              runtime: 1850,
              cpu_usage: 920.5,
              memory: 28.5,
              co2e: '0.00003214',
              status: 'Running',
              statusClass: 'running',
              dependencies: [1]
            }
          ]
        },
        {
          id: 3,
          name: 'WGS Variant Calling',
          description: 'Whole Genome Sequencing variant calling pipeline',
          co2e: '0.007891',
          energy: '7.692',
          runtime: 5200,
          cpuEfficiency: 72.4,
          status: 'Queued',
          statusClass: 'status-queued',
          optimizations: [
            'Use GPU acceleration for GATK steps',
            'Optimize storage for intermediate VCF files',
            'Parallelize across chromosomes'
          ],
          tasks: [
            {
              id: 1,
              name: 'BWA-MEM',
              runtime: 2200,
              cpu_usage: 980.2,
              memory: 30.2,
              co2e: '0.00003654',
              status: 'Queued',
              statusClass: 'queued',
              dependencies: []
            }
          ]
        }
      ]
    }
  },
  mounted() {
    // 设置默认选中的工作流
    this.selectedWorkflow = this.workflows[0];
    this.$nextTick(() => {
      this.renderResourceChart();
      this.renderDurationChart();
    });
  },
  methods: {
    selectWorkflow(workflow) {
      this.selectedWorkflow = workflow;
      this.$nextTick(() => {
        this.renderResourceChart();
        this.renderDurationChart();
      });
    },
    formatRuntime(seconds) {
      const hours = Math.floor(seconds / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      const secs = seconds % 60;
      
      if (hours > 0) {
        return `${hours}h ${minutes}m`;
      } else if (minutes > 0) {
        return `${minutes}m ${secs}s`;
      } else {
        return `${secs}s`;
      }
    },
    getTaskClass(task) {
      return {
        'task-high-cpu': task.cpu_usage > 500,
        'task-high-mem': task.memory > 10
      };
    },
    renderResourceChart() {
      const ctx = this.$refs.resourceChart;
      if (!ctx) return;
      
      const labels = this.selectedWorkflow.tasks.map(task => task.name);
      const cpuData = this.selectedWorkflow.tasks.map(task => task.cpu_usage);
      const memoryData = this.selectedWorkflow.tasks.map(task => task.memory);
      
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [
            {
              label: 'CPU Usage (%)',
              data: cpuData,
              backgroundColor: 'rgba(0, 151, 167, 0.6)',
              borderColor: 'rgba(0, 151, 167, 1)',
              borderWidth: 1
            },
            {
              label: 'Memory (GB)',
              data: memoryData,
              backgroundColor: 'rgba(77, 208, 225, 0.6)',
              borderColor: 'rgba(77, 208, 225, 1)',
              borderWidth: 1
            }
          ]
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true,
              title: {
                display: true,
                text: 'Resource Usage'
              }
            }
          }
        }
      });
    },
    renderDurationChart() {
      const ctx = this.$refs.durationChart;
      if (!ctx) return;
      
      const labels = this.selectedWorkflow.tasks.map(task => task.name);
      const durationData = this.selectedWorkflow.tasks.map(task => task.runtime);
      
      new Chart(ctx, {
        type: 'pie',
        data: {
          labels: labels,
          datasets: [{
            data: durationData,
            backgroundColor: [
              'rgba(0, 151, 167, 0.6)',
              'rgba(77, 208, 225, 0.6)',
              'rgba(178, 235, 242, 0.6)',
              'rgba(255, 112, 67, 0.6)',
              'rgba(108, 117, 125, 0.6)'
            ],
            borderColor: [
              'rgba(0, 151, 167, 1)',
              'rgba(77, 208, 225, 1)',
              'rgba(178, 235, 242, 1)',
              'rgba(255, 112, 67, 1)',
              'rgba(108, 117, 125, 1)'
            ],
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: 'right'
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  const label = context.label || '';
                  const value = context.raw || 0;
                  return `${label}: ${value} seconds`;
                }
              }
            }
          }
        }
      });
    }
  }
}
</script>

<style scoped>
.workflow-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  flex-wrap: wrap;
  gap: 15px;
}

.section-header h2 {
  font-size: 1.8rem;
  color: #38455b;
  margin: 0;
}

.workflow-filter {
  display: flex;
  gap: 10px;
}

.workflow-filter .btn {
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #4a5568;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.workflow-filter .btn:hover {
  background: #edf2f7;
}

.workflow-filter .btn.active {
  background: #2c7873;
  color: white;
  border-color: #2c7873;
}

.workflow-container {
  display: flex;
  gap: 25px;
}

.workflow-list {
  flex: 1;
  min-width: 300px;
  max-height: 80vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.workflow-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  border-left: 4px solid #2c7873;
  cursor: pointer;
  transition: all 0.3s ease;
}

.workflow-card.selected {
  border-left: 4px solid #0097a7;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-3px);
}

.workflow-card:hover:not(.selected) {
  box-shadow: 0 4px 10px rgba(0,0,0,0.08);
}

.workflow-info h3 {
  font-size: 1.2rem;
  margin: 0 0 10px 0;
  color: #38455b;
}

.workflow-info p {
  font-size: 0.9rem;
  color: #718096;
  margin: 0 0 15px 0;
  line-height: 1.5;
}

.workflow-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 0.85rem;
  color: #718096;
}

.workflow-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.workflow-status {
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 600;
  margin-top: 15px;
  display: inline-block;
}

.status-completed {
  background: rgba(40, 167, 69, 0.1);
  color: #28a745;
}

.status-running {
  background: rgba(255, 193, 7, 0.1);
  color: #d4a017;
}

.status-queued {
  background: rgba(108, 117, 125, 0.1);
  color: #6c757d;
}

.workflow-details {
  flex: 3;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  padding: 25px;
}

.workflow-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.workflow-header h3 {
  font-size: 1.5rem;
  color: #38455b;
  margin: 0;
}

.workflow-actions {
  display: flex;
  gap: 10px;
}

.btn-run {
  background: #28a745;
  color: white;
}

.btn-edit {
  background: #2c7873;
  color: white;
}

.workflow-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 25px;
}

.stat-card {
  display: flex;
  gap: 15px;
  align-items: center;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
}

.stat-icon {
  width: 45px;
  height: 45px;
  background: rgba(0, 151, 167, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon i {
  color: #0097a7;
  font-size: 1.3rem;
}

.stat-value {
  font-size: 1.3rem;
  font-weight: 700;
  color: #38455b;
}

.stat-label {
  font-size: 0.9rem;
  color: #718096;
}

.workflow-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 25px;
  margin-bottom: 25px;
}

.task-sequence {
  grid-column: span 1;
}

.workflow-charts {
  grid-column: span 1;
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.chart-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  height: 300px;
}

.chart-card h4 {
  margin: 0 0 15px 0;
  font-size: 1.1rem;
  color: #38455b;
}

.chart-container {
  height: 250px;
}

.sequence-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.task-node {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  overflow: hidden;
  border-left: 4px solid #2c7873;
  position: relative;
}

.task-high-cpu {
  border-left: 4px solid #ff7043;
}

.task-high-mem {
  border-left: 4px solid #4dd0e1;
}

.task-header {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.task-number {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #2c7873;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-weight: 600;
}

.task-title {
  flex: 1;
  font-weight: 600;
  color: #38455b;
}

.task-status {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.completed {
  background: rgba(40, 167, 69, 0.1);
  color: #28a745;
}

.running {
  background: rgba(255, 193, 7, 0.1);
  color: #d4a017;
}

.queued {
  background: rgba(108, 117, 125, 0.1);
  color: #6c757d;
}

.task-details {
  padding: 15px;
}

.task-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 0.9rem;
  color: #718096;
}

.task-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.task-dependencies {
  position: absolute;
  top: 0;
  left: -30px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 5px;
}

.dependency {
  position: relative;
  display: flex;
  align-items: center;
}

.dependency-line {
  width: 20px;
  height: 2px;
  background: #cbd5e0;
}

.dependency-label {
  font-size: 0.7rem;
  background: #e2e8f0;
  padding: 2px 5px;
  border-radius: 4px;
  margin-left: 5px;
}

.optimization-section {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.optimization-section h3 {
  font-size: 1.2rem;
  color: #38455b;
  margin: 0 0 15px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.optimization-section i {
  color: #ffc107;
}

.suggestion-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.suggestion-list li {
  padding: 10px 0;
  border-bottom: 1px dashed #e2e8f0;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.suggestion-list li:last-child {
  border-bottom: none;
}

.suggestion-list i {
  color: #28a745;
  font-size: 0.9rem;
  margin-top: 4px;
}

@media (max-width: 1200px) {
  .workflow-container {
    flex-direction: column;
  }
  
  .workflow-list {
    flex-direction: row;
    flex-wrap: wrap;
    max-height: none;
  }
  
  .workflow-card {
    min-width: 300px;
    flex: 1;
  }
}

@media (max-width: 992px) {
  .workflow-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .workflow-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .workflow-actions {
    width: 100%;
  }
  
  .workflow-actions .btn {
    flex: 1;
    text-align: center;
  }
  
  .task-dependencies {
    display: none;
  }
}
</style>