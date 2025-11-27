<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElMessage,
  ElOption,
  ElSelect,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';

const emit = defineEmits(['reload']);
const categoryList = ref<any[]>([]);
const getPluginCategoryList = async () => {
  return api.get('/api/v1/aiPluginCategories/list').then((res) => {
    if (res.errorCode === 0) {
      categoryList.value = res.data;
    }
  });
};
onMounted(() => {
  getPluginCategoryList();
});

defineExpose({
  openDialog,
});
const saveForm = ref<FormInstance>();
const dialogVisible = ref(false);
const isAdd = ref(true);
const entity = ref<any>({
  id: '',
  categoryIds: [],
});
const btnLoading = ref(false);
function getPluginCategories(id: string) {
  return api
    .get(`/api/v1/aiPluginCategoryRelation/getPluginCategories?pluginId=${id}`)
    .then((res) => {
      if (res.errorCode === 0) {
        entity.value.categoryIds = res.data;
      }
    });
}
function openDialog(row: any) {
  if (row.id) {
    isAdd.value = false;
  }
  getPluginCategories(row.id).then(() => {
    entity.value.categoryIds = row.categoryIds.map((item: any) => item.id);
  });
  entity.value = row;
  dialogVisible.value = true;
}
function save() {
  saveForm.value?.validate((valid) => {
    if (valid) {
      const tempParams = {
        pluginId: entity.value.id,
        categoryIds: entity.value.categoryIds,
      };
      api
        .post('/api/v1/aiPluginCategoryRelation/updateRelation', tempParams)
        .then((res) => {
          if (res.errorCode === 0) {
            ElMessage.success($t('message.updateOkMessage'));
            closeDialog();
            emit('reload');
          }
        });
    }
  });
}
function closeDialog() {
  saveForm.value?.resetFields();
  isAdd.value = true;
  entity.value = {};
  dialogVisible.value = false;
}
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    width="500px"
    :title="$t('plugin.placeholder.categorize')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    align-center
  >
    <ElForm ref="saveForm" :model="entity" status-icon>
      <ElFormItem prop="authType" :label="$t('plugin.category')">
        <ElSelect
          v-model="entity.categoryIds"
          multiple
          collapse-tags
          collapse-tags-tooltip
          :max-collapse-tags="3"
        >
          <ElOption
            v-for="item in categoryList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </ElSelect>
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="closeDialog">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton
        type="primary"
        @click="save"
        :loading="btnLoading"
        :disabled="btnLoading"
      >
        {{ $t('button.save') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<style scoped></style>
