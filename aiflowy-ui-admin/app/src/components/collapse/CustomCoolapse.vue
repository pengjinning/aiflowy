<script setup lang="ts">
import { ref } from 'vue';

interface AccordionItem {
  id: number;
  title: string;
  content: string;
  isOpen: boolean;
}

const accordionData = ref<AccordionItem[]>([
  {
    id: 1,
    title: 'Vue.js 简介',
    content:
      'Vue.js 是一套用于构建用户界面的渐进式框架。与其它大型框架不同的是，Vue 被设计为可以自底向上逐层应用。',
    isOpen: true,
  },
  {
    id: 2,
    title: 'Composition API',
    content:
      'Composition API 是 Vue 3 中引入的一组 API，允许您使用函数而不是通过选项来组织组件的逻辑。',
    isOpen: false,
  },
  {
    id: 3,
    title: '响应式原理',
    content:
      'Vue 3 使用 Proxy 对象来实现响应式，相比 Vue 2 的 Object.defineProperty，Proxy 可以监听动态添加的属性。',
    isOpen: false,
  },
]);

const allowMultiple = ref(false);

const togglePanel = (index: number) => {
  const item = accordionData.value[index];

  if (!item) return;

  if (!allowMultiple.value) {
    accordionData.value.forEach((otherItem, i) => {
      if (i !== index && otherItem) {
        otherItem.isOpen = false;
      }
    });
  }

  item.isOpen = !item.isOpen;
};

const expandAll = () => {
  accordionData.value.forEach((item) => {
    if (item) {
      item.isOpen = true;
    }
  });
};

const collapseAll = () => {
  accordionData.value.forEach((item) => {
    if (item) {
      item.isOpen = false;
    }
  });
};
</script>

<template>
  <div class="accordion-container">
    <h1 class="title">Vue3 折叠面板</h1>
    <p class="subtitle">使用 Vue3 Composition API 实现</p>

    <!-- 控制面板 -->
    <div class="controls">
      <div class="control-group">
        <label class="checkbox-label">
          <input type="checkbox" v-model="allowMultiple" class="checkbox" />
          允许多个同时展开
        </label>
      </div>
      <div class="control-group">
        <button @click="expandAll" class="control-btn">展开全部</button>
        <button @click="collapseAll" class="control-btn">收起全部</button>
      </div>
    </div>

    <!-- 折叠面板列表 -->
    <div class="accordion-list">
      <div
        v-for="(item, index) in accordionData"
        :key="item.id"
        class="accordion-item"
        :class="{ 'accordion-item--active': item.isOpen }"
      >
        <!-- 面板头部 -->
        <div class="accordion-header" @click="togglePanel(index)">
          <div class="column-header-container">
            <div
              class="accordion-icon"
              :class="{ 'accordion-icon--rotated': item.isOpen }"
            >
              ▼
            </div>
            <h3 class="accordion-title">{{ item.title }}</h3>
          </div>
        </div>

        <!-- 面板内容 -->
        <div
          class="accordion-content"
          :class="{ 'accordion-content--open': item.isOpen }"
        >
          <div class="accordion-content-inner">
            <p>{{ item.content }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.accordion-container {
  max-width: 100%;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.title {
  text-align: center;
  color: var(--el-text-color-secondary);
  margin-bottom: 8px;
  font-size: 2rem;
  font-weight: 600;
}

.subtitle {
  text-align: center;
  color: var(--el-text-color-secondary);
  margin-bottom: 30px;
  font-size: 1.1rem;
}

/* 控制面板样式 */
.controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: var(--el-bg-color);
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.control-group {
  display: flex;
  align-items: center;
  gap: 15px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  cursor: pointer;
}

.checkbox {
  width: 16px;
  height: 16px;
}

.control-btn {
  padding: 8px 16px;
  border: 1px solid #3498db;
  background: var(--el-bg-color);
  color: var(--el-text-color-secondary);
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.control-btn:hover {
  background: #3498db;
  background: var(--el-color-primary-light-9);
}

/* 折叠面板列表 */
.accordion-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.accordion-item {
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  overflow: hidden;
  background: var(--el-bg-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.accordion-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.accordion-item--active {
  border-color: #3498db;
}

/* 面板头部 */
.accordion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: var(--el-bg-color);
  cursor: pointer;
  transition: background-color 0.3s ease;
  user-select: none;
}

.accordion-header:hover {
  background: var(--el-color-primary-light-9);
}

.accordion-title {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--el-text-color-secondary);
  padding-left: 12px;
}

.accordion-icon {
  transition: transform 0.3s ease;
  color: #7f8c8d;
  font-size: 12px;
}

.accordion-icon--rotated {
  transform: rotate(180deg);
  color: #3498db;
}

.accordion-content {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.4s ease;
  background: var(--el-bg-color);
}

.accordion-content--open {
  max-height: 200px;
}

.accordion-content-inner {
  padding: 20px;
  border-top: 1px solid #e1e5e9;
}

.accordion-content-inner p {
  margin: 0;
  line-height: 1.6;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}
.column-header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
/* 响应式设计 */
@media (max-width: 768px) {
  .accordion-container {
    padding: 15px;
  }

  .controls {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .control-group {
    justify-content: center;
  }

  .title {
    font-size: 1.5rem;
  }

  .accordion-header {
    padding: 14px 16px;
  }

  .accordion-title {
    font-size: 1rem;
  }
}
</style>
