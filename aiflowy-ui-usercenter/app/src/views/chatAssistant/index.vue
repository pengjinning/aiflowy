<script setup lang="ts">
import { ElAvatar } from 'element-plus';

const recentUsedAssistant = [
  { id: 0, title: '客服助手', description: '智能客服，回答用户问题' },
  { id: 1, title: '知识助手', description: '基于知识库的问答助手dhish…' },
  {
    id: 2,
    title: '业务助手',
    description: '业务咨询和指导',
  },
];
</script>

<template>
  <div class="flex h-full w-full items-center bg-white">
    <div class="flex h-full flex-col gap-5 p-5 pr-6">
      <span>最近使用</span>
      <div class="flex h-full flex-col gap-5 overflow-auto">
        <div
          class="flex w-full max-w-60 items-start gap-2.5 pb-2.5 pl-2.5 pr-5 pt-3.5"
          v-for="assistant in recentUsedAssistant"
          :key="assistant.id"
        >
          <ElAvatar :size="36" class="shrink-0" />
          <div class="flex flex-col gap-1 overflow-hidden text-nowrap">
            <div
              class="overflow-hidden text-ellipsis text-base font-semibold text-[#323233]"
            >
              {{ assistant.title }}
            </div>
            <span class="overflow-hidden text-ellipsis text-xs text-[#ABB0B9]">
              {{ assistant.description }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
