<script lang="ts" setup>
import type { UploadProps, UploadUserFile } from 'element-plus';

import { defineEmits, ref } from 'vue';

import { useAppConfig } from '@aiflowy/hooks';
import { useAccessStore } from '@aiflowy/stores';

import { ElButton, ElMessage, ElMessageBox, ElUpload } from 'element-plus';

const props = defineProps({
  action: {
    type: String,
    default: '/api/v1/commons/upload',
  },
  tips: {
    type: String,
    default: '',
  },
  limit: {
    type: Number,
    default: 3,
  },
  multiple: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits([
  'success', // 文件上传成功
  'handleDelete',
  'handlePreview',
]);

const { apiURL } = useAppConfig(import.meta.env, import.meta.env.PROD);

const accessStore = useAccessStore();
const headers = ref({
  'aiflowy-token': accessStore.accessToken,
});
const fileList = ref<UploadUserFile[]>([]);

const handleRemove: UploadProps['onRemove'] = (file, uploadFiles) => {
  emit('handleDelete', file, uploadFiles);
};

const handlePreview: UploadProps['onPreview'] = (uploadFile) => {
  emit('handleDelete', uploadFile);
};

const handleExceed: UploadProps['onExceed'] = (files, uploadFiles) => {
  ElMessage.warning(
    `The limit is 3, you selected ${files.length} files this time, add up to ${
      files.length + uploadFiles.length
    } totally`,
  );
};

const handleSuccess: UploadProps['onSuccess'] = (response) => {
  emit('success', response);
};

const beforeRemove: UploadProps['beforeRemove'] = (uploadFile) => {
  return ElMessageBox.confirm(`确定删除 ${uploadFile.name} ?`).then(
    () => true,
    () => false,
  );
};
</script>

<template>
  <ElUpload
    v-model:file-list="fileList"
    class="upload-demo"
    :headers="headers"
    :action="`${apiURL}${props.action}`"
    :multiple="props.multiple"
    :on-preview="handlePreview"
    :on-remove="handleRemove"
    :before-remove="beforeRemove"
    :limit="props.limit"
    :on-exceed="handleExceed"
    :on-success="handleSuccess"
  >
    <ElButton type="primary">Click to upload</ElButton>
    <template #tip>
      <div class="el-upload__tip">
        {{ tips }}
      </div>
    </template>
  </ElUpload>
</template>
