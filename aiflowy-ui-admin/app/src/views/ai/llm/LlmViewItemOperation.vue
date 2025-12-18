<script setup lang="ts">
import type { PropType } from 'vue';

import type { llmType } from '#/api';

import { Minus, Plus, Setting } from '@element-plus/icons-vue';
import { ElIcon, ElImage } from 'element-plus';

import { getIconByValue } from '#/views/ai/llm/defaultIcon';

defineProps({
  llmList: {
    type: Array as PropType<llmType[]>,
    default: () => [],
  },
  icon: {
    type: String,
    default: '',
  },
  needHiddenSettingIcon: {
    type: Boolean,
    default: false,
  },
  isManagement: {
    type: Boolean,
    default: false,
  },
});
const emit = defineEmits(['deleteLlm', 'editLlm', 'addLlm']);
const handleDeleteLlm = (id: string) => {
  emit('deleteLlm', id);
};
const handleAddLlm = (id: string) => {
  emit('addLlm', id);
};
const handleEditLlm = (id: string) => {
  emit('editLlm', id);
};
</script>

<template>
  <div v-for="llm in llmList" :key="llm.id" class="container">
    <div class="llm-item">
      <div class="start">
        <ElImage
          v-if="llm.aiLlmProvider.icon"
          :src="llm.aiLlmProvider.icon"
          style="width: 21px; height: 21px"
        />

        <div
          v-else
          v-html="getIconByValue(llm.aiLlmProvider.provider)"
          :style="{
            width: '21px',
            height: '21px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            overflow: 'hidden',
          }"
          class="svg-container"
        ></div>

        <div>{{ llm?.aiLlmProvider?.providerName }}/{{ llm.title }}</div>
      </div>
      <div class="end">
        <ElIcon
          v-if="!needHiddenSettingIcon"
          size="16"
          @click="handleEditLlm(llm.id)"
          style="cursor: pointer"
        >
          <Setting />
        </ElIcon>
        <template v-if="!isManagement">
          <ElIcon
            size="16"
            @click="handleDeleteLlm(llm.id)"
            style="cursor: pointer"
          >
            <Minus />
          </ElIcon>
        </template>

        <template v-if="isManagement">
          <ElIcon
            v-if="llm.added"
            size="16"
            @click="handleDeleteLlm(llm.id)"
            style="cursor: pointer"
          >
            <Minus />
          </ElIcon>
          <ElIcon
            v-else
            size="16"
            @click="handleAddLlm(llm.id)"
            style="cursor: pointer"
          >
            <Plus />
          </ElIcon>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.llm-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 40px;
}
.container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-left: 18px;
  padding-right: 18px;
}
.start {
  display: flex;
  align-items: center;
  gap: 12px;
}
.end {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
