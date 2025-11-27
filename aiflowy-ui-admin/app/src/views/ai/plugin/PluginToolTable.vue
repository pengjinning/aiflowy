<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { Delete, Edit } from '@element-plus/icons-vue';
import {
  ElButton,
  ElIcon,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import PageData from '#/components/page/PageData.vue';
import AiPluginToolModal from '#/views/ai/plugin/AiPluginToolModal.vue';

const props = defineProps({
  pluginId: {
    required: true,
    type: String,
  },
});
const router = useRouter();
defineExpose({
  openPluginToolModal() {
    aiPluginToolRef.value.openDialog();
  },
  reload: () => {
    pageDataRef.value.setQuery({ pluginId: props.pluginId });
  },
  handleSearch: (params: string) => {
    pageDataRef.value.setQuery({
      pluginId: props.pluginId,
      isQueryOr: true,
      name: params,
    });
  },
});
const pageDataRef = ref();
const handleEdit = (row: any) => {
  router.push({
    path: '/ai/plugin/tool/edit',
    query: {
      id: row.id,
      pageKey: '/ai/plugin',
    },
  });
};

const handleDelete = (row: any) => {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('button.confirm'),
    cancelButtonText: $t('button.cancel'),
    type: 'warning',
  }).then(() => {
    api.post('/api/v1/aiPluginTool/remove', { id: row.id }).then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.deleteOkMessage'));
        pageDataRef.value.setQuery({ pluginId: props.pluginId });
      }
    });
  });
};
const aiPluginToolRef = ref();
const pluginToolReload = () => {
  pageDataRef.value.setQuery({ pluginId: props.pluginId });
};
</script>

<template>
  <PageData
    page-url="/api/v1/aiPluginTool/page"
    ref="pageDataRef"
    :page-size="10"
    :extra-query-params="{ pluginId: props.pluginId }"
  >
    <template #default="{ pageList }">
      <ElTable :data="pageList" style="width: 100%" size="large">
        <ElTableColumn
          prop="name"
          :label="$t('aiPluginTool.name')"
          width="180"
        />
        <ElTableColumn
          prop="description"
          :label="$t('aiPluginTool.description')"
        />
        <ElTableColumn
          prop="created"
          :label="$t('aiPluginTool.created')"
          width="200"
        />
        <ElTableColumn fixed="right" :label="$t('common.handle')" width="200">
          <template #default="scope">
            <ElButton link type="primary" @click="handleEdit(scope.row)">
              <ElIcon class="mr-1">
                <Edit />
              </ElIcon>
              {{ $t('button.edit') }}
            </ElButton>
            <ElButton link type="danger" @click="handleDelete(scope.row)">
              <ElIcon class="mr-1">
                <Delete />
              </ElIcon>
              {{ $t('button.delete') }}
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </template>
  </PageData>
  <AiPluginToolModal
    ref="aiPluginToolRef"
    :plugin-id="pluginId"
    @reload="pluginToolReload"
  />
</template>

<style scoped>
.time-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
</style>
