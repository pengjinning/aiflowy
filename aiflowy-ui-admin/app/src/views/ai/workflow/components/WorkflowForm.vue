<script setup lang="ts">
import { computed, ref } from 'vue';
import {Position} from '@element-plus/icons-vue'
import type {FormInstance} from 'element-plus'
import {
  ElAlert,
  ElButton,
  ElCheckboxGroup,
  ElForm,
  ElFormItem,
  ElInput,
  ElRadioGroup,
  ElSelect,
} from 'element-plus';

import { $t } from '#/locales';

export type WorkflowFormProps = {
  onExecute?: (values: any) => void;
  submitLoading: boolean;
  workflowParams: any;
};
const props = withDefaults(defineProps<WorkflowFormProps>(), {
  onExecute: () => {
    console.warn('no execute method');
  },
});
const runForm = ref<FormInstance>();
const runParams = ref<any>({});
const parameters = computed(() => {
  return props.workflowParams.parameters;
});
function getContentType(item: any) {
  return item.contentType || 'text';
}
function isResource(contentType: any) {
  return ['audio', 'file', 'image', 'video'].includes(contentType);
}
function getCheckboxOptions(item: any) {
  if (item.enums) {
    return (
      item.enums?.map((option: any) => ({
        label: option,
        value: option,
      })) || []
    );
  }
  return [];
}
function submit() {
  runForm.value?.validate((valid) => {
    if (valid) {
      props.onExecute(runParams.value);
    }
  });
}
</script>

<template>
  <ElForm label-position="top" ref="runForm" :model="runParams">
    <ElFormItem
      v-for="(item, idx) in parameters"
      :prop="item.name"
      :key="idx"
      :label="item.formLabel || item.name"
      :rules="
        item.required
          ? [{ required: true, message: $t('message.required') }]
          : []
      "
    >
      <template v-if="getContentType(item) === 'text'">
        <ElInput
          v-if="item.formType === 'input' || !item.formType"
          v-model="runParams[item.name]"
          :placeholder="item.formPlaceholder"
        />
        <ElSelect
          v-if="item.formType === 'select'"
          v-model="runParams[item.name]"
          :placeholder="item.formPlaceholder"
          :options="getCheckboxOptions(item)"
          clearable
        />
        <ElInput
          v-if="item.formType === 'textarea'"
          v-model="runParams[item.name]"
          :placeholder="item.formPlaceholder"
          :rows="3"
          type="textarea"
        />
        <ElRadioGroup
          v-if="item.formType === 'radio'"
          v-model="runParams[item.name]"
          :options="getCheckboxOptions(item)"
        />
        <ElCheckboxGroup
          v-if="item.formType === 'checkbox'"
          v-model="runParams[item.name]"
          :options="getCheckboxOptions(item)"
        />
      </template>
      <template v-if="getContentType(item) === 'other'">
        <ElInput
          v-model="runParams[item.name]"
          :placeholder="item.formPlaceholder"
        />
      </template>
      <template v-if="isResource(getContentType(item))">
        选择资源组件
      </template>
      <ElAlert v-if="item.formDescription" type="info" style="margin-top: 5px;">
        {{ item.formDescription }}
      </ElAlert>
    </ElFormItem>
    <ElFormItem>
      <ElButton
        type="primary"
        @click="submit"
        :loading="props.submitLoading"
        :icon="Position"
      >
        {{ $t('button.run') }}
      </ElButton>
    </ElFormItem>
  </ElForm>
</template>

<style scoped></style>
