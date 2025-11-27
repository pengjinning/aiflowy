<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, reactive, ref } from 'vue';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';

const props = defineProps({
  pluginId: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['reload']);

const entity = reactive({
  pluginId: '',
  name: '',
  description: '',
});

const saveForm = ref<FormInstance>();
const dialogVisible = ref(false);
const isAdd = ref(true);
const btnLoading = ref(false);

const rules = ref({
  name: [{ required: true, message: $t('message.required'), trigger: 'blur' }],
});

// 安全地打开对话框
function openDialog(row: any) {
  try {
    // 重置表单状态
    saveForm.value?.resetFields();

    // 安全地处理传入的数据
    if (row && row.id) {
      isAdd.value = false;
      // 使用 Object.assign 避免直接 Proxy 赋值
      Object.assign(entity, {
        ...row,
        pluginId: props.pluginId,
      });
    } else {
      isAdd.value = true;
      // 重置 entity 数据
      Object.assign(entity, {
        pluginId: props.pluginId,
        name: '',
        description: '',
      });
    }

    dialogVisible.value = true;
  } catch (error) {
    console.error('打开对话框错误:', error);
  }
}

// 保存数据
function save() {
  if (!saveForm.value) return;

  saveForm.value.validate((valid) => {
    if (!valid) {
      return;
    }

    btnLoading.value = true;

    const apiUrl = isAdd.value
      ? 'api/v1/aiPluginTool/tool/save'
      : 'api/v1/aiPluginTool/tool/update';

    // 创建纯对象提交，避免 Proxy
    const submitData = { ...entity };

    api
      .post(apiUrl, submitData)
      .then((res) => {
        btnLoading.value = false;
        if (res.errorCode === 0) {
          ElMessage.success($t('message.saveOkMessage'));
          closeDialog();
          emit('reload');
        }
      })
      .catch((error) => {
        console.error('API请求错误:', error);
        btnLoading.value = false;
      });
  });
}

// 关闭对话框
function closeDialog() {
  try {
    if (saveForm.value) {
      saveForm.value.resetFields();
    }

    // 重置数据
    Object.assign(entity, {
      pluginId: props.pluginId,
      name: '',
      description: '',
    });

    isAdd.value = true;
    dialogVisible.value = false;
  } catch (error) {
    console.error('关闭对话框错误:', error);
    // 强制关闭
    dialogVisible.value = false;
  }
}

onMounted(() => {
  Object.assign(entity, {
    pluginId: props.pluginId,
    name: '',
    description: '',
  });
});

defineExpose({
  openDialog,
});
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="isAdd ? $t('button.add') : $t('button.edit')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    width="600px"
    @closed="closeDialog"
  >
    <ElForm
      ref="saveForm"
      :model="entity"
      :rules="rules"
      label-width="80px"
      status-icon
    >
      <ElFormItem :label="$t('aiPluginTool.name')" prop="name">
        <ElInput v-model.trim="entity.name" />
      </ElFormItem>
      <ElFormItem :label="$t('aiPluginTool.description')" prop="description">
        <ElInput v-model.trim="entity.description" type="textarea" :rows="4" />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElButton @click="closeDialog" :disabled="btnLoading">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton
        type="primary"
        :loading="btnLoading"
        :disabled="btnLoading"
        @click="save"
      >
        {{ $t('button.save') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<style scoped></style>
