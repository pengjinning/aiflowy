<script setup lang="ts">
import { markRaw, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { Back, Plus } from '@element-plus/icons-vue';

import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PluginToolTable from '#/views/ai/plugin/PluginToolTable.vue';

const route = useRoute();
const router = useRouter();

const pluginId = ref<string>((route.query.id as string) || '');
const headerButtons = [
  {
    key: 'back',
    text: $t('button.back'),
    icon: markRaw(Back),
    data: { action: 'back' },
  },
  {
    key: 'createTool',
    text: $t('pluginItem.createPluginTool'),
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'createTool' },
  },
];
const handleSearch = (params: any) => {
  pluginToolRef.value.handleSearch(params);
};
const handleButtonClick = (event: any) => {
  // 根据按钮 key 执行不同操作
  switch (event.key) {
    case 'back': {
      router.push({ path: '/ai/plugin' });
      break;
    }
    case 'createTool': {
      pluginToolRef.value.openPluginToolModal({});
      break;
    }
  }
};
const pluginToolRef = ref();
</script>

<template>
  <div class="flex h-full flex-col gap-6 p-6">
    <HeaderSearch
      :buttons="headerButtons"
      @search="handleSearch"
      @button-click="handleButtonClick"
    />

    <div class="bg-background flex-1 rounded-lg p-5">
      <PluginToolTable :plugin-id="pluginId" ref="pluginToolRef" />
    </div>
  </div>
</template>
