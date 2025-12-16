<script setup lang="ts">
import type { Props } from './types';

import { preferences } from '@aiflowy-core/preferences';
import {
  AIFlowyAvatar,
  Card,
  Tabs,
  TabsList,
  TabsTrigger,
} from '@aiflowy-core/shadcn-ui';

import { Page } from '../../components';

defineOptions({
  name: 'ProfileUI',
});

withDefaults(defineProps<Props>(), {
  title: '关于项目',
  tabs: () => [],
});

const tabsValue = defineModel<string>('modelValue');
</script>
<template>
  <Page auto-content-height>
    <div class="flex h-full w-full">
      <Card class="w-[200px]">
        <div class="mt-4 flex h-40 flex-col items-center justify-center gap-4">
          <AIFlowyAvatar
            :src="userInfo?.avatar ?? preferences.app.defaultAvatar"
            class="size-20"
          />
          <span class="text-lg font-semibold">
            {{ userInfo?.realName ?? '' }}
          </span>
          <span class="text-foreground/80 text-sm">
            {{ userInfo?.username ?? '' }}
          </span>
        </div>
        <!-- <Separator class="my-4" /> -->
        <Tabs v-model="tabsValue" orientation="vertical" class="mx-4">
          <TabsList class="bg-card grid w-full grid-cols-1 gap-2">
            <TabsTrigger
              v-for="tab in tabs"
              :key="tab.value"
              :value="tab.value"
              class="data-[state=active]:text-primary justify-start px-3 py-2.5 font-normal hover:bg-[hsl(var(--accent))] data-[state=active]:bg-[hsl(var(--primary)/15%)] data-[state=active]:shadow-none"
            >
              {{ tab.label }}
            </TabsTrigger>
          </TabsList>
        </Tabs>
      </Card>
      <Card class="ml-4 flex-1 p-8">
        <slot name="content"></slot>
      </Card>
    </div>
  </Page>
</template>
