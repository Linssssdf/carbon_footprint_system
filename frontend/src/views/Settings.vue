<template>
  <div class="settings system-monitoring">
    <div class="header">
      <h1>System Settings</h1>
    </div>

    <div class="section">
      <div class="setting-category">
        <h2>General Settings</h2>
        <div class="setting-item">
          <label>Theme</label>
          <select v-model="theme" class="form-control">
            <option value="light">Light</option>
            <option value="dark">Dark</option>
          </select>
        </div>
        <div class="setting-item">
          <label>Language</label>
          <select v-model="language" class="form-control">
            <option value="en">English</option>
            <option value="zh">中文</option>
          </select>
        </div>
      </div>

      <div class="setting-category">
        <h2>Notification Settings</h2>
        <div class="setting-item">
          <div class="checkbox-container">
            <input type="checkbox" v-model="notificationsEnabled" id="notifications">
            <label for="notifications">Enable Notifications</label>
          </div>
        </div>
        <div class="setting-item">
          <label>Notification Frequency</label>
          <select v-model="notificationFrequency" class="form-control">
            <option value="realtime">Real-time</option>
            <option value="hourly">Hourly</option>
            <option value="daily">Daily</option>
          </select>
        </div>
      </div>

      <div class="setting-category">
        <h2>Advanced Settings</h2>
        <div class="setting-item">
          <div class="checkbox-container">
            <input type="checkbox" v-model="debugMode" id="debugMode">
            <label for="debugMode">Debug Mode</label>
          </div>
        </div>
        <div class="setting-item">
          <label>Log Level</label>
          <select v-model="logLevel" class="form-control">
            <option value="info">Info</option>
            <option value="warning">Warning</option>
            <option value="error">Error</option>
          </select>
        </div>
      </div>
    </div>

    <div class="action-buttons">
      <button class="btn btn-primary" @click="saveSettings">
        <i class="fas fa-save"></i> Save Changes
      </button>
      <button class="btn btn-outline" @click="resetSettings">
        <i class="fas fa-undo"></i> Reset to Default
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      theme: 'light',
      language: 'en',
      notificationsEnabled: true,
      notificationFrequency: 'realtime',
      debugMode: false,
      logLevel: 'info'
    }
  },
  methods: {
    saveSettings() {
      // Implement save functionality
      console.log("Settings saved");
    },
    resetSettings() {
      // Implement reset functionality
      console.log("Settings reset");
    }
  }
}
</script>

<style scoped>
.settings.system-monitoring {
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
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.setting-category {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
}

.setting-category h2 {
  font-size: 1.5rem;
  margin-bottom: 20px;
  color: #555;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
  font-weight: 500;
}

.setting-item {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.setting-item label {
  display: block;
  font-weight: 500;
  color: #555;
  font-size: 1rem;
}

.form-control {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fff;
  font-size: 1rem;
  color: #333;
  transition: all 0.3s;
  box-sizing: border-box;
}

.form-control:focus {
  border-color: #0097a7;
  outline: none;
  box-shadow: 0 0 0 2px rgba(0, 151, 167, 0.2);
}

.checkbox-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.checkbox-container input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #0097a7;
}

.checkbox-container label {
  font-weight: normal;
  margin-bottom: 0;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 10px;
  justify-content: flex-end;
}

.btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 1rem;
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

@media (max-width: 768px) {
  .settings.system-monitoring {
    padding: 20px;
  }
  
  .header h1 {
    font-size: 1.8rem;
  }
  
  .setting-category h2 {
    font-size: 1.3rem;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 10px;
  }
  
  .btn {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .settings.system-monitoring {
    padding: 15px;
  }
  
  .header h1 {
    font-size: 1.5rem;
  }
  
  .section {
    padding: 15px;
  }
  
  .setting-category {
    padding: 15px;
  }
}
</style>