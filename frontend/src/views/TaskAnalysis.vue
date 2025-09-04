<template>
  <div class="system-monitoring">
    <div class="header">
      <h1>Task Analysis</h1>
      <p class="subtitle">Detailed analysis of workflow tasks and performance metrics</p>
    </div>

    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
      <p>Loading task analysis data...</p>
    </div>

    <div v-else-if="error" class="error-message">
      <i class="fas fa-exclamation-triangle"></i>
      <p>{{ error }}</p>
      <button @click="fetchTaskAnalysis" class="retry-btn">Retry</button>
    </div>

    <div v-else class="section">
      <div class="analysis-controls">
        <div class="control-group">
          <label>Select Analysis:</label>
          <select v-model="selectedAnalysisId" @change="loadAnalysisData" class="form-control">
            <option value="">-- Select Analysis --</option>
            <option v-for="analysis in availableAnalyses" :key="analysis.id" :value="analysis.id">
              {{ analysis.fileName }} ({{ formatDate(analysis.analyzedAt) }})
            </option>
          </select>
        </div>
      </div>

      <div v-if="selectedAnalysis && selectedAnalysis.tasks" class="analysis-results">
        <h2>Task Performance Metrics</h2>
        
        <div class="table-container">
          <table class="metrics-table">
            <thead>
              <tr>
                <th>Process Name</th>
                <th>Tasks Count</th>
                <th>Energy Consumption (kWh)</th>
                <th>Carbon Footprint (kgCO₂)</th>
                <th>Runtime (min)</th>
                <th>CPU Usage (%)</th>
                <th>Memory Allocated (GB)</th>
                <th>I/O Volume (GB)</th>
                <th>Hardware</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="task in selectedAnalysis.tasks" :key="task.id">
                <td>{{ task.process }}</td>
                <td>{{ task.tasks || 'N/A' }}</td>
                <td>{{ task.energy_consumption ? (task.energy_consumption / 1000).toFixed(3) : 'N/A' }}</td>
                <td>{{ task.carbon_footprint ? (task.carbon_footprint / 1000).toFixed(3) : 'N/A' }}</td>
                <td>{{ task.runtime ? task.runtime.toFixed(3) : 'N/A' }}</td>
                <td>{{ task.cpu_usage ? task.cpu_usage.toFixed(2) : 'N/A' }}</td>
                <td>{{ task.memory_allocated ? task.memory_allocated.toFixed(2) : 'N/A' }}</td>
                <td>{{ task.io_volume ? task.io_volume.toFixed(2) : 'N/A' }}</td>
                <td>{{ task.hardware || 'N/A' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div v-else>
        <p>No analysis selected or no task data available. Please select an analysis from the dropdown.</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'TaskAnalysis',
  data() {
    return {
      availableAnalyses: [],
      selectedAnalysisId: null,
      selectedAnalysis: null,
      loading: true,
      error: null
    };
  },
  mounted() {
    this.fetchAvailableAnalyses();
  },
  methods: {
    async fetchAvailableAnalyses() {
      this.loading = true;
      try {
        const response = await axios.get('/api/analyses');
        this.availableAnalyses = response.data;
      } catch (error) {
        console.error("Failed to fetch available analyses:", error);
        this.error = "Failed to load available analyses.";
      } finally {
        this.loading = false;
      }
    },
    
    async loadAnalysisData() {
      if (!this.selectedAnalysisId) {
        this.selectedAnalysis = null;
        return;
      }
      
      this.loading = true;
      try {
        const response = await axios.get(`/api/analyses/${this.selectedAnalysisId}`);
        this.selectedAnalysis = response.data;
        
        console.log("Analysis data loaded:", this.selectedAnalysis);
        if (this.selectedAnalysis.tasks) {
          console.log("Tasks data:", this.selectedAnalysis.tasks);
          console.log("First task:", this.selectedAnalysis.tasks[0]);
        }
      } catch (error) {
        console.error("Failed to fetch analysis data:", error);
        this.error = "Failed to load analysis details.";
        this.selectedAnalysis = null;
      } finally {
        this.loading = false;
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const date = new Date(dateString);
      return date.toLocaleDateString();
    }
  }
}
</script>

<style scoped>
/* 保留原有样式不变 */
.system-monitoring {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  color: #2c3e50;
  padding: 20px;
  background: #f4f7f6;
  min-height: 100vh;
}

.header {
  margin-bottom: 20px;
}

.header h1 {
  font-size: 2.5rem;
  color: #38455b;
  margin-bottom: 5px;
}

.subtitle {
  color: #6b7280;
  font-size: 1rem;
}

.section {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.analysis-controls {
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.05);
}

.control-group {
  display: flex;
  align-items: center;
  gap: 15px;
}

.control-group label {
  font-weight: 500;
  min-width: 120px;
}

.form-control {
  flex-grow: 1;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  background-color: #f9fafb;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
}

.form-control:focus {
  outline: none;
  border-color: #2c7873;
  box-shadow: 0 0 0 3px rgba(44, 120, 115, 0.2);
}

.table-container {
  overflow-x: auto;
}

.metrics-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.metrics-table th, .metrics-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.metrics-table th {
  background-color: #eef1f4;
  font-weight: 600;
  color: #4a5568;
  text-transform: uppercase;
  font-size: 0.85rem;
}

.metrics-table tbody tr:hover {
  background-color: #f7fafc;
}

.metrics-table tbody tr:last-child td {
  border-bottom: none;
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
  border-radius: 5px;
  background-color: #d32f2f;
  color: white;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}

.retry-btn:hover {
  background-color: #b71c1c;
}
</style>