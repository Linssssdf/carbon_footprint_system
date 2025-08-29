<template>
  <div class="system-monitoring">
    <div class="header">
      <h1>Data Source Management</h1>
      <p class="subtitle">Connect and manage your data sources, supporting multiple data formats</p>
    </div>

    <div class="section">
      <div class="section-header">
        <h2>Connected Data Sources</h2>
        <button class="btn btn-primary" @click="openAddDataSource">
          <i class="fas fa-plus"></i> Add New Data Source
        </button>
      </div>
      <input 
        type="file" 
        ref="fileInput" 
        style="display: none" 
        @change="handleFileUpload"
        accept=".csv"
      >
      
      <!-- 如果没有文件，显示提示信息 -->
      <div v-if="dataSources.length === 0" class="empty-state">
        <i class="fas fa-cloud-upload-alt"></i>
        <h3>No data sources connected</h3>
        <p>Upload a CSV trace file to get started with carbon footprint analysis.</p>
      </div>
      
      <!-- 有文件时显示文件列表 -->
      <div v-else class="data-sources-grid">
        <div 
          v-for="source in dataSources" 
          :key="source.id" 
          class="source-card"
        >
          <div class="source-icon">
            <i class="fas fa-file-csv"></i>
          </div>
          <div class="source-info">
            <h3>{{ source.fileName }}</h3>
            <p>Uploaded at: {{ formatDate(source.uploadTime) }}</p>
            <div class="source-meta">
              <span>Status: <span :class="getStatusClass(source.status)">{{ source.status }}</span></span>
              <span v-if="source.lastExecutionTime">Last analyzed: {{ formatDate(source.lastExecutionTime) }}</span>
            </div>
            <div class="source-actions">
              <button class="btn btn-outline" @click="analyzeSource(source.id)">
                <i class="fas fa-chart-bar"></i> Analyze
              </button>
              <button class="btn btn-danger" @click="deleteSource(source.id)">
                <i class="fas fa-trash-alt"></i> Delete
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
      <p>Processing...</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      dataSources: [],
      loading: false
    };
  },
  created() {
    this.fetchDataSources();
  },
  methods: {
    async fetchDataSources() {
      try {
        const response = await axios.get('/api/data-sources');
        this.dataSources = response.data;
      } catch (error) {
        console.error("Failed to fetch data sources:", error);
        // 如果后端没有实现获取所有数据源的API，使用模拟数据
        this.dataSources = [];
      }
    },
    
    openAddDataSource() {
      this.$refs.fileInput.click();
    },
    
    async handleFileUpload(event) {
      const file = event.target.files[0];
      if (!file) return;
      
      // 验证文件类型
      if (file.type !== 'text/csv' && !file.name.toLowerCase().endsWith('.csv')) {
        alert('Please upload a CSV file');
        return;
      }
      
      this.loading = true;
      const formData = new FormData();
      formData.append('file', file);
      
      try {
        const response = await axios.post('/api/data-sources/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        
        const newSource = response.data;
        this.dataSources.push(newSource);
        alert('Data source added successfully!');
      } catch (error) {
        console.error("Upload failed:", error);
        let errorMsg = 'Upload failed';
        if (error.response?.data?.message) {
          errorMsg += ': ' + error.response.data.message;
        } else if (error.message) {
          errorMsg += ': ' + error.message;
        }
        alert(errorMsg);
      } finally {
        this.loading = false;
        event.target.value = '';
      }
    },
    
    async analyzeSource(sourceId) {
      this.loading = true;
      try {
        const response = await axios.post(`/api/analysis/${sourceId}/analyze`);
        const result = response.data;
        
        // 跳转到可视化页面，传递结果ID
        this.$router.push({ 
          path: '/visualization', 
          query: { resultId: result.id }
        });
      } catch (error) {
        console.error("Analysis failed:", error);
        let errorMsg = "Analysis failed";
        if (error.response?.data?.message) {
          errorMsg += ": " + error.response.data.message;
        } else if (error.message) {
          errorMsg += ": " + error.message;
        }
        alert(errorMsg);
      } finally {
        this.loading = false;
      }
    },
    
    async deleteSource(sourceId) {
      if (!confirm('Are you sure you want to delete this data source?')) {
        return;
      }
      
      try {
        await axios.delete(`/api/data-sources/${sourceId}`);
        this.dataSources = this.dataSources.filter(source => source.id !== sourceId);
        alert('Data source deleted successfully.');
      } catch (error) {
        console.error("Delete failed:", error);
        alert('Failed to delete data source.');
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const date = new Date(dateString);
      return date.toLocaleString();
    },
    
    getStatusClass(status) {
      switch (status) {
        case 'UPLOADED': return 'status-uploaded';
        case 'ANALYZED': return 'status-analyzed';
        case 'ANALYSIS_FAILED': return 'status-failed';
        default: return '';
      }
    }
  }
}
</script>

<style scoped>
/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 40px;
  color: #666;
  border: 2px dashed #ddd;
  border-radius: 8px;
  margin-top: 20px;
}

.empty-state i {
  font-size: 3rem;
  color: #ccc;
  margin-bottom: 15px;
}

.empty-state h3 {
  margin-bottom: 10px;
  color: #555;
}

.empty-state p {
  color: #777;
}

/* 状态标签样式 */
.status-uploaded {
  color: #ff9800;
  font-weight: 500;
}

.status-analyzed {
  color: #4caf50;
  font-weight: 500;
}

.status-failed {
  color: #f44336;
  font-weight: 500;
}

/* 加载覆盖层 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
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
</style>