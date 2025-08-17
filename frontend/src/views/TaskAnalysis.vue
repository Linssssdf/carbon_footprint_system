<template>
  <div class="system-monitoring">
    <div class="header">
      <h1>Task Analysis</h1>
    </div>

    <div class="section">
      <h2>数据表格分析</h2>
      
      <!-- 表1: 工作流元数据 -->
      <div class="table-section">
        <h3>表1: 评估工作流元数据</h3>
        <el-table 
          :data="table1Data" 
          border 
          class="data-table"
          v-loading="loadingTable1"
          element-loading-text="加载元数据..."
        >
          <el-table-column prop="category" label="类别" width="120" />
          <el-table-column prop="workflow" label="工作流" width="180" />
          <el-table-column prop="inputs" label="输入数据(GB)" align="right" />
          <el-table-column prop="generated" label="生成数据(GB)" align="right" />
          <el-table-column prop="factor" label="因子" align="right" />
          <el-table-column prop="abstractTasks" label="抽象任务" align="right" />
          <el-table-column prop="physicalTasks" label="物理任务" align="right" />
        </el-table>
      </div>

      <!-- 表2: 工作流执行结果 -->
      <div class="table-section">
        <h3>表2: 工作流执行结果 (Ceph & NFS, 8节点, 1Gbit网络)</h3>
        <el-table 
          :data="table2Data" 
          border 
          class="data-table"
          v-loading="loadingTable2"
          element-loading-text="加载执行结果..."
        >
          <el-table-column prop="workflow" label="工作流" width="150" fixed />
          <!-- Ceph列 -->
          <el-table-column label="Ceph" align="center">
            <el-table-column prop="ceph.orig" label="Orig(min)" align="right" />
            <el-table-column prop="ceph.cws" label="CWS(%)" align="right" />
            <el-table-column prop="ceph.wow" label="WOW(%)" align="right" />
            <el-table-column prop="ceph.cpuOrig" label="CPU Orig(h)" align="right" />
            <el-table-column prop="ceph.cpuCws" label="CPU CWS(%)" align="right" />
            <el-table-column prop="ceph.cpuWow" label="CPU WOW(%)" align="right" />
            <el-table-column prop="ceph.none" label="无COP(%)" align="right" />
            <el-table-column prop="ceph.used" label="COP使用(%)" align="right" />
          </el-table-column>
          <!-- NFS列 -->
          <el-table-column label="NFS" align="center">
            <el-table-column prop="nfs.orig" label="Orig(min)" align="right" />
            <el-table-column prop="nfs.cws" label="CWS(%)" align="right" />
            <el-table-column prop="nfs.wow" label="WOW(%)" align="right" />
            <el-table-column prop="nfs.cpuOrig" label="CPU Orig(h)" align="right" />
            <el-table-column prop="nfs.cpuCws" label="CPU CWS(%)" align="right" />
            <el-table-column prop="nfs.cpuWow" label="CPU WOW(%)" align="right" />
            <el-table-column prop="nfs.none" label="无COP(%)" align="right" />
            <el-table-column prop="nfs.used" label="COP使用(%)" align="right" />
          </el-table-column>
        </el-table>
      </div>

      <!-- 表3: 网络带宽影响 -->
      <div class="table-section">
        <h3>表3: 网络带宽对执行时间的影响(1Gbit→2Gbit)</h3>
        <el-table 
          :data="table3Data" 
          border 
          class="data-table"
          v-loading="loadingTable3"
          element-loading-text="加载网络影响数据..."
        >
          <el-table-column prop="workflow" label="工作流" width="150" />
          <el-table-column prop="ceph.orig" label="Ceph-Orig(%)" align="right" />
          <el-table-column prop="ceph.cws" label="Ceph-CWS(%)" align="right" />
          <el-table-column prop="ceph.wow" label="Ceph-WOW(%)" align="right" />
          <el-table-column prop="nfs.orig" label="NFS-Orig(%)" align="right" />
          <el-table-column prop="nfs.cws" label="NFS-CWS(%)" align="right" />
          <el-table-column prop="nfs.wow" label="NFS-WOW(%)" align="right" />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { ElTable, ElTableColumn } from 'element-plus'
export default {
  components: {
    ElTable,
    ElTableColumn
  },
  data() {
    return {
      loadingTable1: true,
      loadingTable2: true,
      loadingTable3: true,
      table1Data: [
        {
          category: "ChIP-Seq",
          workflow: "ChIP-Seq v1.0",
          inputs: 42.5,
          generated: 128.3,
          factor: 3.02,
          abstractTasks: 28,
          physicalTasks: 105
        },
        {
          category: "RNA-Seq",
          workflow: "RNA-Seq v2.1",
          inputs: 35.8,
          generated: 98.7,
          factor: 2.76,
          abstractTasks: 25,
          physicalTasks: 92
        },
        {
          category: "WGS",
          workflow: "WGS v3.2",
          inputs: 152.4,
          generated: 324.8,
          factor: 2.13,
          abstractTasks: 32,
          physicalTasks: 118
        },
        {
          category: "ATAC-Seq",
          workflow: "ATAC-Seq v1.5",
          inputs: 28.7,
          generated: 86.2,
          factor: 3.00,
          abstractTasks: 24,
          physicalTasks: 88
        }
      ],
      table2Data: [
        {
          workflow: "ChIP-Seq",
          ceph: {
            orig: 215,
            cws: 12.4,
            wow: 8.7,
            cpuOrig: 42.1,
            cpuCws: 15.3,
            cpuWow: 10.2,
            none: 18.6,
            used: 79.6
          },
          nfs: {
            orig: 243,
            cws: 14.2,
            wow: 9.8,
            cpuOrig: 48.3,
            cpuCws: 17.1,
            cpuWow: 11.5,
            none: 22.3,
            used: 72.4
          }
        },
        {
          workflow: "RNA-Seq",
          ceph: {
            orig: 187,
            cws: 10.8,
            wow: 7.2,
            cpuOrig: 36.5,
            cpuCws: 13.2,
            cpuWow: 8.9,
            none: 15.4,
            used: 82.1
          },
          nfs: {
            orig: 210,
            cws: 12.7,
            wow: 8.3,
            cpuOrig: 41.2,
            cpuCws: 15.8,
            cpuWow: 10.1,
            none: 19.8,
            used: 75.6
          }
        },
        {
          workflow: "WGS",
          ceph: {
            orig: 423,
            cws: 18.5,
            wow: 12.4,
            cpuOrig: 82.7,
            cpuCws: 22.3,
            cpuWow: 15.8,
            none: 24.1,
            used: 71.2
          },
          nfs: {
            orig: 486,
            cws: 21.7,
            wow: 14.6,
            cpuOrig: 95.1,
            cpuCws: 26.4,
            cpuWow: 18.3,
            none: 28.9,
            used: 65.3
          }
        },
        {
          workflow: "ATAC-Seq",
          ceph: {
            orig: 182,
            cws: 9.7,
            wow: 6.8,
            cpuOrig: 35.8,
            cpuCws: 12.1,
            cpuWow: 8.4,
            none: 14.2,
            used: 83.5
          },
          nfs: {
            orig: 205,
            cws: 11.9,
            wow: 8.1,
            cpuOrig: 40.3,
            cpuCws: 14.7,
            cpuWow: 9.8,
            none: 18.5,
            used: 76.9
          }
        }
      ],
      table3Data: [
        {
          workflow: "ChIP-Seq",
          ceph: { orig: -18.3, cws: -15.2, wow: -12.7 },
          nfs: { orig: -15.2, cws: -12.8, wow: -10.4 }
        },
        {
          workflow: "RNA-Seq",
          ceph: { orig: -16.5, cws: -14.1, wow: -11.3 },
          nfs: { orig: -14.0, cws: -11.9, wow: -9.6 }
        },
        {
          workflow: "WGS",
          ceph: { orig: -22.4, cws: -19.7, wow: -16.8 },
          nfs: { orig: -19.2, cws: -16.5, wow: -14.1 }
        },
        {
          workflow: "ATAC-Seq",
          ceph: { orig: -17.1, cws: -14.8, wow: -12.1 },
          nfs: { orig: -14.8, cws: -12.3, wow: -10.2 }
        }
      ]
    };
  },
  mounted() {
    // 模拟数据加载延迟
    setTimeout(() => {
      this.loadingTable1 = false;
    }, 800);
    setTimeout(() => {
      this.loadingTable2 = false;
    }, 1200);
    setTimeout(() => {
      this.loadingTable3 = false;
    }, 1600);
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

.table-section {
  margin-bottom: 30px;
  overflow-x: auto;
  position: relative;
}

.table-section h3 {
  font-size: 1.3rem;
  margin: 15px 0;
  color: #555;
  font-weight: 500;
}

.data-table {
  width: 100%;
  min-width: 1200px;
  margin-top: 10px;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  height: 8px;
  width: 8px;
}

::-webkit-scrollbar-thumb {
  background-color: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

@media (max-width: 768px) {
  .system-monitoring {
    padding: 20px;
  }
  
  .header h1 {
    font-size: 1.8rem;
  }
  
  .section h2 {
    font-size: 1.3rem;
  }
  
  .table-section h3 {
    font-size: 1.1rem;
  }
  
  .data-table {
    min-width: 900px;
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
  
  .data-table {
    min-width: 700px;
  }
}
</style>