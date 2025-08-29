<template>
  <div class="content">
    <h2 style="margin-bottom: 20px;">Carbon Footprint Analysis Dashboard</h2>
    
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Loading analysis data...</p>
    </div>
    
    <div v-else-if="error" class="error-state">
      <i class="fas fa-exclamation-triangle"></i>
      <p>{{ error }}</p>
      <button @click="fetchDashboardData" class="retry-btn">Retry</button>
    </div>
    
    <div v-else class="dashboard-grid">
      <div class="dashboard-card">
        <div class="card-header">
          <h3>Analysis Summary</h3>
        </div>
        <div class="card-content">
          <div class="summary-stats">
            <div class="stat-item">
              <i class="fas fa-bolt"></i>
              <div>
                <div class="stat-value">{{ summary.totalEnergy ? summary.totalEnergy.toFixed(2) : 0 }} kWh</div>
                <div class="stat-label">Total Energy</div>
              </div>
            </div>
            <div class="stat-item">
              <i class="fas fa-cloud"></i>
              <div>
                <div class="stat-value">{{ summary.totalCarbonFootprint ? summary.totalCarbonFootprint.toFixed(2) : 0 }} kgCO₂</div>
                <div class="stat-label">Carbon Emissions</div>
              </div>
            </div>
            <div class="stat-item">
              <i class="fas fa-clock"></i>
              <div>
                <div class="stat-value">{{ summary.totalRuntime ? summary.totalRuntime.toFixed(1) : 0 }} min</div>
                <div class="stat-label">Total Runtime</div>
              </div>
            </div>
            <div class="stat-item">
              <i class="fas fa-microchip"></i>
              <div>
                <div class="stat-value">{{ summary.avgCpuUtilization ? summary.avgCpuUtilization.toFixed(1) : 0 }}%</div>
                <div class="stat-label">Avg CPU Usage</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="dashboard-card">
        <div class="card-header">
          <h3>Recent Analyses</h3>
        </div>
        <div class="card-content">
          <div v-if="recentAnalyses.length === 0" class="empty-state">
            <i class="fas fa-chart-bar"></i>
            <p>No analyses yet</p>
            <router-link to="/data-sources" class="btn btn-primary">
              Analyze Your First File
            </router-link>
          </div>
          <div v-else class="recent-list">
            <div v-for="analysis in recentAnalyses" :key="analysis.id" class="recent-item">
              <div class="recent-info">
                <h4>{{ analysis.fileName }}</h4>
                <p>Analyzed: {{ formatDate(analysis.analyzedAt) }}</p>
              </div>
              <div class="recent-actions">
                <router-link 
                  :to="'/visualization?resultId=' + analysis.resultId" 
                  class="btn btn-sm btn-outline"
                >
                  View Details
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="dashboard-card">
        <div class="card-header">
          <h3>Top Energy Consumers</h3>
        </div>
        <div class="card-content">
          <div v-if="topConsumers.length === 0" class="empty-state">
            <i class="fas fa-bolt"></i>
            <p>No consumption data</p>
          </div>
          <div v-else class="consumers-list">
            <div v-for="(consumer, index) in topConsumers" :key="index" class="consumer-item">
              <div class="consumer-rank">{{ index + 1 }}</div>
              <div class="consumer-info">
                <div class="consumer-name">{{ consumer.name }}</div>
                <div class="consumer-energy">{{ consumer.energy.toFixed(4) }} kWh</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      loading: true,
      error: null,
      summary: {},
      recentAnalyses: [],
      topConsumers: []
    };
  },
  mounted() {
    this.fetchDashboardData();
  },
  methods: {
    async fetchDashboardData() {
      this.loading = true;
      this.error = null;
      
      try {
        // Fetch dashboard summary
        const summaryResponse = await axios.get('/api/dashboard/summary');
        this.summary = summaryResponse.data;
        
        // Fetch recent analyses
        const analysesResponse = await axios.get('/api/dashboard/recent-analyses');
        this.recentAnalyses = analysesResponse.data;
        
        // Fetch top energy consumers
        const consumersResponse = await axios.get('/api/dashboard/top-consumers');
        this.topConsumers = consumersResponse.data;
        
      } catch (error) {
        console.error("Failed to fetch dashboard data:", error);
        this.error = "Failed to load dashboard data. Please try again.";
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
};
</script>

<style scoped>
.summary-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item i {
  font-size: 1.5rem;
  color: #2c7873;
}

.stat-value {
  font-size: 1.3rem;
  font-weight: bold;
  color: #38455b;
}

.stat-label {
  font-size: 0.9rem;
  color: #6b7280;
}

.recent-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.recent-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.recent-info h4 {
  margin: 0 0 5px 0;
  font-size: 1rem;
}

.recent-info p {
  margin: 0;
  font-size: 0.9rem;
  color: #6b7280;
}

.consumers-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.consumer-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px;
}

.consumer-rank {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #2c7873;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.consumer-name {
  flex: 1;
  font-weight: 500;
}

.consumer-energy {
  color: #2c7873;
  font-weight: 600;
}

.loading-state, .error-state {
  text-align: center;
  padding: 40px;
}

.empty-state {
  text-align: center;
  padding: 30px;
  color: #6b7280;
}

.empty-state i {
  font-size: 2rem;
  margin-bottom: 15px;
  color: #d1d5db;
}
</style>