<template>
  <div class="system-monitoring">
    <div class="header">
      <h1>Help Center</h1>
      <p class="subtitle">Get system usage help and answers to frequently asked questions</p>
    </div>

    <div class="section">
      <div class="section-header">
        <h2>Frequently Asked Questions</h2>
        <div class="search-container">
          <i class="fas fa-search"></i>
          <input type="text" placeholder="Search Help Content...">
        </div>
      </div>
      
      <div class="faq-list">
        <div 
          v-for="(faq, index) in faqs" 
          :key="faq.id" 
          class="faq-item"
          @click="toggleFAQ(index)"
        >
          <div class="faq-question">
            <span>{{ faq.question }}</span>
            <i :class="['fas', expandedIndex === index ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
          </div>
          <div v-if="expandedIndex === index" class="faq-answer">
            <p>{{ faq.answer }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      expandedIndex: null,
      faqs: [
        // 数据保持不变
      ]
    }
  },
  methods: {
    toggleFAQ(index) {
      this.expandedIndex = this.expandedIndex === index ? null : index;
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

.search-container {
  position: relative;
  width: 300px;
}

.search-container i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaa;
}

.search-container input {
  width: 100%;
  padding: 10px 15px 10px 40px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  transition: all 0.3s;
}

.search-container input:focus {
  border-color: #0097a7;
  outline: none;
  box-shadow: 0 0 0 2px rgba(0, 151, 167, 0.2);
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.faq-item {
  background: white;
  border-radius: 8px;
  border: 1px solid #eee;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.3s ease;
}

.faq-item:hover {
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.faq-question {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  cursor: pointer;
  font-weight: 500;
  font-size: 1.1rem;
  color: #333;
}

.faq-question i {
  color: #0097a7;
  transition: transform 0.3s ease;
}

.faq-answer {
  padding: 0 20px 20px 20px;
  border-top: 1px solid #eee;
  color: #555;
  line-height: 1.6;
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
  
  .search-container {
    width: 100%;
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
  
  .faq-question {
    padding: 15px;
    font-size: 1rem;
  }
}
</style>