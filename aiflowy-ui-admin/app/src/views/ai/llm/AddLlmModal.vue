<script setup lang="ts">
import { reactive, ref, watch } from 'vue';

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

type BooleanField =
  | 'supportEmbedding'
  | 'supportFree'
  | 'supportReasoning'
  | 'supportRerank'
  | 'supportTool';

interface ModelAbilityItem {
  activeType: 'danger' | 'info' | 'primary' | 'success' | 'warning';
  defaultType: 'info';
  field: BooleanField;
  label: string;
  selected: boolean;
  value: string;
}

const props = defineProps({
  providerId: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['reload']);
const selectedProviderId = ref<string>(props.providerId ?? '');

// 监听 providerId 的变化
watch(
  () => props.providerId,
  (newVal) => {
    if (newVal) {
      selectedProviderId.value = newVal;
    }
  },
  { immediate: true },
);

const formDataRef = ref();
defineExpose({
  openAddDialog(modelType: string) {
    isAdd.value = true;
    if (formDataRef.value) {
      formDataRef.value.resetFields();
    }
    Object.assign(formData, {
      id: '',
      modelType,
      title: '',
      llmModel: '',
      groupName: '',
      provider: '',
      endPoint: '',
      providerId: '',
      supportReasoning: false,
      supportEmbedding: false,
      supportRerank: false,
      supportTool: false,
      supportFree: false,
      options: {
        llmEndpoint: '',
        chatPath: '',
        embedPath: '',
        rerankPath: '',
      },
    });
    syncTagSelectedStatus();
    dialogVisible.value = true;
  },
  openEditDialog(item: any) {
    dialogVisible.value = true;
    isAdd.value = false;

    Object.assign(formData, {
      id: item.id,
      modelType: item.modelType || '',
      title: item.title || '',
      llmModel: item.llmModel || '',
      groupName: item.groupName || '',
      provider: item.provider || '',
      endPoint: item.endPoint || '',
      supportReasoning: item.supportReasoning || false,
      supportEmbedding: item.supportEmbedding || false,
      supportRerank: item.supportRerank || false,
      supportTool: item.supportTool || false,
      supportFree: item.supportFree || false,
      options: {
        llmEndpoint: item.options?.llmEndpoint || '',
        chatPath: item.options?.chatPath || '',
        embedPath: item.options?.embedPath || '',
        rerankPath: item.options?.rerankPath || '',
      },
    });

    syncTagSelectedStatus();
  },
});
const isAdd = ref(true);
const dialogVisible = ref(false);

const formData = reactive({
  modelType: '',
  title: '',
  llmModel: '',
  groupName: '',
  providerId: '',
  provider: '',
  endpoint: '',
  supportReasoning: false,
  supportEmbedding: false,
  supportRerank: false,
  supportTool: false,
  supportFree: false,
  options: {
    llmEndpoint: '',
    chatPath: '',
    embedPath: '',
    rerankPath: '',
  },
});

const modelAbility = ref<ModelAbilityItem[]>([
  {
    label: $t('llm.modelAbility.reasoning'),
    value: 'reasoning',
    defaultType: 'info',
    activeType: 'success',
    selected: false,
    field: 'supportReasoning', // 关联 supportReasoning
  },
  {
    label: $t('llm.modelAbility.tool'),
    value: 'tool',
    defaultType: 'info',
    activeType: 'primary',
    selected: false,
    field: 'supportTool', // 关联 supportTool
  },
  {
    label: $t('llm.modelAbility.embedding'),
    value: 'embedding',
    defaultType: 'info',
    activeType: 'warning',
    selected: false,
    field: 'supportEmbedding', // 关联 supportEmbedding
  },
  {
    label: $t('llm.modelAbility.rerank'),
    value: 'rerank',
    defaultType: 'info',
    activeType: 'danger',
    selected: false,
    field: 'supportRerank', // 关联 supportRerank
  },
  {
    label: $t('llm.modelAbility.free'),
    value: 'free',
    defaultType: 'info',
    activeType: 'success',
    selected: false,
    field: 'supportFree', // 关联 supportFree
  },
]);

const syncTagSelectedStatus = () => {
  modelAbility.value.forEach((tag) => {
    tag.selected = formData[tag.field];
  });
};

const closeDialog = () => {
  dialogVisible.value = false;
};

const rules = {
  title: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
  llmModel: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
  groupName: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
  modelType: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
  provider: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
};

const btnLoading = ref(false);
const save = async () => {
  btnLoading.value = true;
  try {
    await formDataRef.value.validate();
    const submitData = { ...formData };
    if (isAdd.value) {
      submitData.providerId = selectedProviderId.value;
      const res = await api.post('/api/v1/model/save', submitData);
      if (res.errorCode === 0) {
        ElMessage.success(res.message);
        emit('reload');
        closeDialog();
      }
    } else {
      const res = await api.post('/api/v1/model/update', submitData);
      if (res.errorCode === 0) {
        ElMessage.success(res.message);
        emit('reload');
        closeDialog();
      }
    }
  } catch {
    ElMessage.error($t('message.operationFailed'));
  } finally {
    btnLoading.value = false;
  }
};
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="isAdd ? $t('button.add') : $t('button.edit')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    align-center
    width="482"
  >
    <ElForm
      label-width="100px"
      ref="formDataRef"
      :model="formData"
      status-icon
      :rules="rules"
    >
      <ElFormItem prop="title" :label="$t('llm.title')">
        <ElInput v-model.trim="formData.title" />
      </ElFormItem>
      <ElFormItem prop="llmModel" :label="$t('llm.llmModel')">
        <ElInput v-model.trim="formData.llmModel" />
      </ElFormItem>
      <ElFormItem prop="endpoint" :label="$t('llmProvider.endpoint')">
        <ElInput v-model.trim="formData.endpoint" />
      </ElFormItem>
      <ElFormItem prop="chatPath" :label="$t('llmProvider.chatPath')">
        <ElInput v-model.trim="formData.options.chatPath" />
      </ElFormItem>
      <ElFormItem prop="embedPath" :label="$t('llmProvider.embedPath')">
        <ElInput v-model.trim="formData.options.embedPath" />
      </ElFormItem>
      <ElFormItem prop="groupName" :label="$t('llm.groupName')">
        <ElInput v-model.trim="formData.groupName" />
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

<style scoped>
.model-ability {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  gap: 8px;
}
.model-ability-tag {
  cursor: pointer;
}
.tag-selected {
  font-weight: bold;
}
</style>
