<template>
  <div class="context-tracking">
    <div class="section-header">
      <div class="section-title">Hardware Energy Tracking</div>
      <div class="carbon-summary">
        <div class="carbon-item">
          <i class="fas fa-bolt"></i>
          <span>Total Energy: <strong>{{ totalEnergy.toFixed(2) }} kWh</strong></span>
        </div>
        <div class="carbon-item">
          <i class="fas fa-cloud"></i>
          <span>Total CO₂e: <strong>{{ totalCO2.toFixed(2) }} g</strong></span>
        </div>
      </div>
    </div>
    <div class="section-content">
      <div class="table-filter">
        <button class="btn btn-sm btn-primary" @click="showAddHardware = true">
          <i class="fas fa-plus"></i> Add New Hardware
        </button>
        <select v-model="sortBy" class="select" style="margin-left: 10px;">
          <option value="type">Sort by Type</option>
          <option value="energy">Sort by Energy</option>
          <option value="co2">Sort by CO₂e</option>
        </select>
      </div>

      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>Hardware Type</th>
              <th>Configuration</th>
              <th>Energy (kWh)</th>
              <th>CO₂e (g)</th>
              <th>Efficiency</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="hardware in sortedHardwares" :key="hardware.id">
              <td>{{ hardware.type }}</td>
              <td>{{ hardware.name }}</td>
              <td>{{ hardware.energy }}</td>
              <td>{{ hardware.co2 }}</td>
              <td :class="getEfficiencyClass(hardware.efficiency)">
                <div class="efficiency-indicator">
                  <div class="efficiency-bar" :style="{ width: getEfficiencyWidth(hardware.efficiency) }"></div>
                  <span>{{ hardware.efficiency }}</span>
                </div>
              </td>
              <td>
                <button class="btn btn-sm btn-outline" @click="editHardware(hardware)">
                  <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-danger" @click="deleteHardware(hardware)">
                  <i class="fas fa-trash-alt"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      sortBy: 'type',
      showAddHardware: false,
      hardwares: [
        {
          id: 'hw-1',
          type: 'CPU',
          name: 'Intel Xeon Platinum 8380',
          energy: '0.65',
          co2: '10.2',
          efficiency: 'high'
        },
        {
          id: 'hw-2',
          type: 'GPU',
          name: 'NVIDIA A100 80GB',
          energy: '0.42',
          co2: '8.5',
          efficiency: 'high'
        },
        {
          id: 'hw-3',
          type: 'FPGA',
          name: 'Xilinx Alveo U280',
          energy: '0.35',
          co2: '6.8',
          efficiency: 'high'
        },
        {
          id: 'hw-4',
          type: 'Storage',
          name: 'Samsung PM1733 SSD',
          energy: '0.18',
          co2: '3.2',
          efficiency: 'medium'
        },
        {
          id: 'hw-5',
          type: 'Memory',
          name: 'Samsung DDR4 3200MHz',
          energy: '0.12',
          co2: '2.1',
          efficiency: 'medium'
        }
      ]
    }
  },
  computed: {
    sortedHardwares() {
      const sorted = [...this.hardwares]
      if (this.sortBy === 'type') {
        sorted.sort((a, b) => a.type.localeCompare(b.type))
      } else if (this.sortBy === 'energy') {
        sorted.sort((a, b) => a.energy - b.energy)
      } else if (this.sortBy === 'co2') {
        sorted.sort((a, b) => a.co2 - b.co2)
      }
      return sorted
    },
    totalEnergy() {
      return this.hardwares.reduce((sum, hw) => sum + parseFloat(hw.energy), 0)
    },
    totalCO2() {
      return this.hardwares.reduce((sum, hw) => sum + parseFloat(hw.co2), 0)
    }
  },
  methods: {
    getEfficiencyClass(efficiency) {
      return {
        'high-efficiency': efficiency === 'high',
        'medium-efficiency': efficiency === 'medium',
        'low-efficiency': efficiency === 'low'
      }
    },
    getEfficiencyWidth(efficiency) {
      const map = {
        'high': '90%',
        'medium': '60%',
        'low': '30%'
      }
      return map[efficiency] || '30%'
    },
    editHardware(hardware) {
      // Implement edit functionality
    },
    deleteHardware(hardware) {
      this.hardwares = this.hardwares.filter(hw => hw.id !== hardware.id)
    }
  }
}
</script>

<style scoped>
.context-tracking {
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.section-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.section-title {
  font-size: 1.5rem;
  font-weight: bold;
}

.carbon-summary {
  display: flex;
  gap: 20px;
  background: #f8f9fa;
  padding: 10px 15px;
  border-radius: 8px;
}

.carbon-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.95rem;
}

.carbon-item i {
  color: #2c7873;
  font-size: 1.2rem;
}

.table-filter {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #eaeff5;
}

.data-table thead {
  background: rgba(44, 120, 115, 0.1);
  color: #1a1a1a;
}

.high-efficiency {
  color: #28a745;
}

.medium-efficiency {
  color: #ffc107;
}

.low-efficiency {
  color: #dc3545;
}

.efficiency-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
}

.efficiency-bar {
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.efficiency-bar::after {
  content: '';
  display: block;
  height: 100%;
  background: currentColor;
}

.btn-outline {
  border: 1px solid #cbd5e0;
  color: #4b5563;
}

.btn-danger {
  background-color: #dc2626;
  border-color: #dc2626;
  color: white;
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .carbon-summary {
    width: 100%;
    justify-content: space-around;
  }
}
</style>