<template>
  <div class="header">
    <div class="header-search">
      <i class="fas fa-search"></i>
      <input type="text" placeholder="Search for a project, task, or workflow...">
    </div>
    
    <div class="user-controls">
      <div class="notification-icon">
        <i class="fas fa-bell"></i>
        <div class="notification-count">3</div>
      </div>
      
      <div class="user-profile">
        <div class="user-avatar">Student</div>
        <div class="user-info">
          <div class="user-name">runze</div>
          <div class="user-role">Data Analyst</div>
        </div>
        <i class="fas fa-chevron-down" style="margin-left: 8px;"></i>
      </div>
    </div>
  </div>
</template>

<script>
export default {}
</script>

<style scoped>
.header {
  height: var(--header-height);
  background: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  z-index: 90;
}

.header-search {
  position: relative;
  width: 300px;
}

.header-search input {
  width: 100%;
  padding: 10px 15px 10px 40px;
  border: 1px solid #e1e5eb;
  border-radius: 4px;
  font-size: 0.95rem;
  transition: var(--transition);
}

.header-search input:focus {
  border-color: var(--secondary);
  outline: none;
  box-shadow: 0 0 0 3px rgba(111, 185, 143, 0.2);
}

.header-search i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--gray);
}

.user-controls {
  display: flex;
  align-items: center;
}

.notification-icon {
  position: relative;
  margin-right: 25px;
  cursor: pointer;
  font-size: 1.2rem;
  color: var(--gray);
}

.notification-count {
  position: absolute;
  top: -8px;
  right: -8px;
  background: var(--accent);
  color: white;
  font-size: 0.7rem;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-profile {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--secondary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  margin-right: 12px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 600;
  font-size: 0.95rem;
}

.user-role {
  font-size: 0.8rem;
  color: var(--gray);
}
</style>