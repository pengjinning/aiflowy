<script setup lang="ts">
import { ref } from 'vue';

import { createIconifyIcon } from '@aiflowy/icons';
import { cn } from '@aiflowy/utils';

import { ElAside, ElButton, ElContainer, ElHeader, ElMain } from 'element-plus';

import {
  Card,
  CardAvatar,
  CardContent,
  CardDescription,
  CardTitle,
} from '#/components/card';

const sessionList = [
  { id: '0', title: '新对话' },
  { id: '1', title: '新对话' },
];
const currentSessionId = ref('0');
</script>

<template>
  <ElContainer class="border-border h-full rounded-lg border-2">
    <ElAside width="287px" class="border-border border-r p-6">
      <Card class="max-w-max p-0">
        <CardAvatar />
        <CardContent>
          <CardTitle>客服助手</CardTitle>
          <CardDescription>智能客服，回答用户问题</CardDescription>
        </CardContent>
      </Card>
      <ElButton
        class="mt-6 !h-10 w-full !text-sm"
        color="#0066FF"
        :icon="createIconifyIcon('mdi:chat-plus-outline')"
        plain
      >
        新建会话
      </ElButton>
      <div class="mt-8">
        <div
          v-for="session in sessionList"
          :key="session.id"
          :class="
            cn(
              'flex cursor-pointer items-center justify-between rounded-lg px-5 py-2.5 text-sm',
              currentSessionId === session.id
                ? 'bg-[hsl(var(--primary)/15%)] dark:bg-[hsl(var(--accent))]'
                : 'hover:bg-[hsl(var(--accent))]',
            )
          "
          @click="currentSessionId = session.id"
        >
          <span
            :class="
              cn(
                'text-foreground',
                currentSessionId === session.id && 'text-primary',
              )
            "
          >
            新对话
          </span>
          <span class="text-foreground">12:00</span>
        </div>
      </div>
    </ElAside>
    <ElContainer>
      <ElHeader class="border border-[#E6E9EE] bg-[#FAFAFA]" height="53px">
        <span class="text-base/[53px] font-medium text-[#323233]">新对话</span>
      </ElHeader>
      <ElMain>
        <slot></slot>
      </ElMain>
    </ElContainer>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-button :deep(.el-icon) {
  font-size: 20px;
}
</style>
