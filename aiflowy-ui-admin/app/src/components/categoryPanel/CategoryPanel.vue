<template>
  <div
    class="category-panel"
    :style="{ width: panelWidth + 'px' }"
  >
    <!-- 右上角收缩/展开按钮 -->
    <div class="toggle-panel-btn" @click="togglePanel">
      <ElIcon>
        <ArrowLeft v-if="!isCollapsed" />
        <ArrowRight v-else />
      </ElIcon>
    </div>

    <!-- 分类列表容器 -->
    <div
      class="category-list"
      :class="{ 'collapsed': isCollapsed }"
    >
      <!-- 遍历一级分类数据 -->
      <div
        v-for="(category, index) in categories"
        :key="index"
        class="category-item"
      >
        <div
          class="category-item-content"
          @click="handleCategoryClick(category)"
        >
          <!-- 图标（如果有） -->
          <ElIcon v-if="category[iconKey]" class="category-icon">
            <component :is="category[iconKey]" />
          </ElIcon>

          <!-- 分类名称（收缩状态且有图标时隐藏文字） -->
          <span
            class="category-name"
            :class="{ 'hidden': isCollapsed && category[iconKey] }"
          >
            {{ category[titleKey] }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, computed } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { ElIcon } from 'element-plus'

// 定义组件属性
const props = defineProps({
  // 分类数据，格式示例：[{ name: '分类1', icon: SomeIcon }, { name: '分类2' }]
  categories: {
    type: Array,
    default: () => [],
    required: true
  },
  titleKey: {
    type: String,
    default: 'name'
  },
  iconKey: {
    type: String,
    default: 'icon'
  },
  // 自定义展开状态宽度（默认300px）
  expandWidth: {
    type: Number,
    default: 120
  },
  // 自定义收缩状态宽度（默认48px）
  collapseWidth: {
    type: Number,
    default: 48
  }
})

// 定义事件
const emit = defineEmits([
  'click',       // 分类项点击事件
  'panel-toggle' // 面板收缩状态改变事件
])

// 面板收缩状态
const isCollapsed = ref(false)

// 检查是否有分类包含图标
const hasIcons = computed(() => {
  return props.categories.some(item => item[props.iconKey])
})

// 动态计算面板宽度
const panelWidth = computed(() => {
  if (isCollapsed.value) {
    // 收缩状态：有图标用自定义收缩宽度，无图标保持最小适配宽度
    return hasIcons.value ? props.collapseWidth : 120
  } else {
    // 展开状态：使用自定义展开宽度
    return props.expandWidth
  }
})

// 切换面板收缩状态
const togglePanel = () => {
  isCollapsed.value = !isCollapsed.value
  emit('panel-toggle', {
    collapsed: isCollapsed.value,
    currentWidth: panelWidth.value
  })
}

// 处理分类项点击
const handleCategoryClick = (category) => {
  emit('click', category)
}
</script>

<style scoped>
.category-panel {
  position: relative; /* 相对定位，用于按钮绝对定位 */
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
  height: 100%;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1); /* 平滑宽度过渡 */
  box-sizing: border-box;
}

/* 右上角收缩/展开按钮 */
.toggle-panel-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 10; /* 确保按钮在最上层 */
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: var(--el-color-white);
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.2s;
  /* 按钮不随面板收缩移动 */
  transform: translateX(0);
}

.toggle-panel-btn:hover {
  background-color: #f3f4f6;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.toggle-panel-btn .el-icon {
  width: 16px;
  height: 16px;
  color: #666;
}

/* 分类列表容器 */
.category-list {
  overflow: hidden;
  padding-top: 48px; /* 给右上角按钮留出空间 */
  height: 100%;
  box-sizing: border-box;
}

.category-item {
  border-bottom: 1px solid #e5e7eb;
}

.category-item:last-child {
  border-bottom: none;
}

.category-item-content {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  transition: background-color 0.2s;
  gap: 12px;
}

.category-item-content:hover {
  background-color: #f9fafb;
}

.category-icon {
  width: 18px;
  height: 18px;
  color: var(--el-text-color-primary);
}

.category-name {
  transition: opacity 0.2s, transform 0.2s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: var(--el-text-color-primary);
}

/* 收缩状态样式 */
.hidden {
  display: none;
}

.collapsed .category-item-content {
  justify-content: center;
  padding: 12px 0;
}

/* 收缩状态下文字强制隐藏（避免无图标时文字溢出） */
.collapsed .category-name {
  display: none;
}
</style>
