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
    text: $t('aiPluginTool.createPluginTool'),
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
  <div class="plugin-tools-container">
    <div class="plugin-tools-header">
      <HeaderSearch
        :buttons="headerButtons"
        search-placeholder="搜索用户"
        @search="handleSearch"
        @button-click="handleButtonClick"
      />
    </div>
    <div class="plugin-tools-content">
      <PluginToolTable :plugin-id="pluginId" ref="pluginToolRef" />
    </div>
  </div>
</template>

<style scoped>
.plugin-tools-container {
  width: 100%;
  display: flex;
  height: 100%;
}
.plugin-tools-container {
  padding: 20px;
  width: 100%;
  display: flex;
  flex-direction: column;
}
.plugin-tools-header {
  width: 100%;
  margin: 0 auto;
}
.plugin-tools-content {
  padding-top: 20px;
  width: 100%;
  margin: 0 auto;
  flex: 1;
}
</style>
