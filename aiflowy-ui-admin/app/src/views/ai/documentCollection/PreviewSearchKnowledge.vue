<script setup lang="ts">
import { ref } from 'vue';

import { Document } from '@element-plus/icons-vue';
import { ElButton, ElIcon } from 'element-plus';
// 定义类型接口（与 React 版本一致）
interface PreviewItem {
  sorting: string;
  content: string;
  score: string;
}
const props = defineProps({
  hideScore: {
    type: Boolean,
    default: false,
  },
  data: {
    type: Array as () => PreviewItem[],
    default: () => [],
  },
  total: {
    type: Number,
    default: 0,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  confirmImport: {
    type: Boolean,
    default: false,
  },
  disabledConfirm: {
    type: Boolean,
    default: false,
  },
  onCancel: {
    type: Function,
    default: () => {},
  },
  onConfirm: {
    type: Function,
    default: () => {},
  },
  isSearching: {
    type: Boolean,
    default: false,
  },
});
const loadingStatus = ref(false);
defineExpose({
  loadingContent: (state: boolean) => {
    loadingStatus.value = state;
  },
});
</script>

<template>
  <div class="preview-container" v-loading="loadingStatus">
    <!-- 头部区域：标题 + 统计信息 -->
    <div class="preview-header">
      <h3>
        <ElIcon class="header-icon" size="20">
          <Document />
        </ElIcon>
        {{
          isSearching
            ? $t('documentCollection.searchResults')
            : $t('documentCollection.documentPreview')
        }}
      </h3>
      <span class="preview-stats" v-if="props.data.length > 0">
        {{ $t('documentCollection.total') }}
        {{ total > 0 ? total : data.length }}
        {{ $t('documentCollection.segments') }}
      </span>
    </div>

    <!-- 内容区域：列表预览 -->
    <div class="preview-content">
      <div class="preview-list">
        <div
          v-for="(item, index) in data"
          :key="index"
          class="el-list-item-container"
        >
          <div class="el-list-item">
            <div class="segment-badge">
              {{ item.sorting ?? index + 1 }}
            </div>
            <div class="el-list-item-meta">
              <div v-if="!hideScore">
                {{ $t('documentCollection.similarityScore') }}: {{ item.score }}
              </div>
              <div class="content-desc">{{ item.content }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 操作按钮区域（仅导入确认模式显示） -->
    <div class="preview-actions" v-if="confirmImport">
      <div class="action-buttons">
        <ElButton
          :style="{ minWidth: '100px', height: '36px' }"
          click="onCancel"
        >
          {{ $t('documentCollection.actions.confirmImport') }}
        </ElButton>
        <ElButton
          type="primary"
          :style="{ minWidth: '100px', height: '36px' }"
          :loading="disabledConfirm"
          click="onConfirm"
        >
          {{ $t('documentCollection.actions.cancelImport') }}
        </ElButton>
      </div>
    </div>
  </div>
</template>

<style scoped>
.preview-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-color: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgb(0 0 0 / 8%);

  .preview-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    border-bottom: 1px solid var(--el-border-color);

    h3 {
      display: flex;
      gap: 8px;
      align-items: center;
      margin: 0;
      font-size: 16px;
      font-weight: 500;
      color: var(--el-text-color-primary);

      .header-icon {
        color: var(--el-color-primary);
      }
    }

    .preview-stats {
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }

  .preview-content {
    padding: 20px;
    overflow-y: auto;

    .preview-list {
      .segment-badge {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 24px;
        height: 24px;
        font-size: 12px;
        font-weight: 500;
        color: var(--el-color-primary);
        background-color: var(--el-color-primary-light-9);
        border-radius: 50%;
      }

      .similarity-score {
        font-size: 14px;
        color: var(--el-color-primary);
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }

      .content-desc {
        width: 100%;
        padding: 12px;
        font-size: 14px;
        line-height: 1.6;
        white-space: pre-wrap;
        background-color: var(--el-bg-color);
        border-left: 3px solid #e2e8f0;
        border-radius: 6px;
        transition: all 0.2s;

        &:hover {
          border-color: #4361ee;
          box-shadow: 0 4px 12px rgb(67 97 238 / 8%);
          transform: translateY(-2px);
        }
      }

      .el-list-item {
        display: flex;
        gap: 12px;
        align-items: center;
        padding: 18px;
        border-radius: 8px;
      }

      .el-list-item-meta {
        display: flex;
        flex: 1;
        flex-direction: column;
        gap: 12px;
        align-items: flex-start;
      }
    }
  }

  .preview-actions {
    padding: 16px 20px;
    background-color: var(--el-bg-color-page);
    border-top: 1px solid var(--el-border-color);

    .action-buttons {
      display: flex;
      gap: 12px;
      justify-content: flex-end;
    }
  }
}

/* 适配 Element Plus 加载状态样式 */
.el-list--loading .el-list-loading {
  padding: 40px 0;
}

.el-list-item {
  width: 100%;
  margin-top: 12px;
  border: 1px solid var(--el-border-color-lighter);

  &:hover {
    border-color: var(--el-color-primary);
  }
}
</style>
