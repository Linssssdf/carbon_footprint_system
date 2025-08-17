<template>
  <div class="system-monitoring">
    <div class="header">
      <h1>Data Source Management</h1>
      <p class="subtitle">Connect and manage your data sources, supporting multiple data formats</p>
    </div>

    <div class="section">
      <div class="section-header">
        <h2>Connected Data Sources</h2>
        <!-- 添加点击事件处理 -->
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
      
      <div class="data-sources-grid">
        <div 
          v-for="source in dataSources" 
          :key="source.id" 
          class="source-card"
        >
          <div class="source-icon">
            <i :class="source.icon"></i>
          </div>
          <div class="source-info">
            <h3>{{ source.name }}</h3>
            <p>{{ source.description }}</p>
            <div class="source-meta">
              <span>Last sync: {{ source.lastSync }}</span>
              <span>State: <span :class="source.statusClass">{{ source.status }}</span></span>
            </div>
            <div class="source-actions">
              <button class="btn btn-outline">
                <i class="fas fa-sync"></i> Sync
              </button>
              <button class="btn btn-outline">
                <i class="fas fa-edit"></i> Edit
              </button>
              <button class="btn btn-danger">
                <i class="fas fa-trash-alt"></i> Delete
              </button>
              <button class="btn btn-outline" @click="analyzeSource(source.id)">
                <i class="fas fa-chart-bar"></i> Analyze
              </button>
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
  // 添加 data() 方法来声明和初始化响应式数据
  data() {
    return {
      dataSources: [], // 初始化为一个空数组
    };
  },
  methods: {
    // 添加新方法处理按钮点击
    openAddDataSource() {
      // 触发隐藏的文件输入
      this.$refs.fileInput.click();
    },
    
    handleFileUpload(event) {
      const file = event.target.files[0];
      if (!file) return;
      
      // 验证文件类型
      if (file.type !== 'text/csv' && !file.name.toLowerCase().endsWith('.csv')) {
        alert('请上传CSV格式的文件');
        return;
      }
      
      // 创建FormData对象
      const formData = new FormData();
      formData.append('file', file);
      
      // 发送上传请求
      axios.post('/api/data-sources/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      .then(response => {
        const newSource = response.data;
        // 确保 dataSources 是一个数组，现在它已经被正确初始化了
        this.dataSources.push(newSource);
        alert('数据源添加成功！');
      })
      .catch(error => {
        console.error("详细错误:", error);
        if (error.response) {
          console.error("响应数据:", error.response.data);
        }
        alert('上传失败: ' + (error.response?.data?.message || error.message));
      })
      .finally(() => {
        // 重置文件输入
        event.target.value = '';
      });
    },
    
    analyzeSource(sourceId) {
      axios.post(`/api/analysis/${sourceId}/analyze`)
        .then(response => {
          const resultId = response.data.id;
          this.$router.push({ 
            path: '/visualization2', 
            query: { resultId: resultId }
          });
        })
        .catch(error => {
          console.error("Analysis failed:", error);
          alert("Analysis failed. See console for details.");
        });
    }
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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 1.5rem;
  color: #555;
  font-weight: 500;
}

.data-sources-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

.source-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.source-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.source-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(0, 151, 167, 0.1);
  color: #0097a7;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  flex-shrink: 0;
  font-size: 1.5rem;
}

.source-info {
  flex: 1;
}

.source-info h3 {
  font-size: 1.3rem;
  margin-bottom: 8px;
  color: #333;
}

.source-info p {
  color: #666;
  line-height: 1.5;
  margin-bottom: 15px;
}

.source-meta {
  display: flex;
  gap: 20px;
  font-size: 0.95rem;
  color: #666;
  margin-bottom: 15px;
}

.text-success {
  color: #28a745;
}

.text-danger {
  color: #dc3545;
}

.source-actions {
  display: flex;
  gap: 12px;
}

.btn {
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: #0097a7;
  color: white;
  border: 1px solid #0097a7;
}

.btn-primary:hover {
  background: #008394;
  border-color: #007784;
}

.btn-outline {
  background: transparent;
  color: #0097a7;
  border: 1px solid #0097a7;
}

.btn-outline:hover {
  background: #f0f8ff;
}

.btn-danger {
  background: #dc3545;
  color: white;
  border: 1px solid #dc3545;
}

.btn-danger:hover {
  background: #c82333;
  border-color: #bd2130;
}

@media (max-width: 768px) {
  .system-monitoring {
    padding: 20px;
  }
  
  .header h1 {
    font-size: 1.8rem;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .source-card {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .source-icon {
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .source-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .source-actions {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .system-monitoring {
    padding: 15px;
  }
  
  .header h1 {
    font-size: 1.5rem;
  }
  
  .section {
    padding: 15px;
  }
  
  .source-actions {
    flex-wrap: wrap;
  }
  
  .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>