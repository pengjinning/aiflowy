<script setup lang="ts">
import type { Recordable } from '@aiflowy/types';

import type { AIFlowyFormSchema } from '@aiflowy-core/form-ui';

import { computed, reactive } from 'vue';

import { useAIFlowyForm } from '@aiflowy-core/form-ui';
import { AIFlowyButton } from '@aiflowy-core/shadcn-ui';

interface Props {
  formSchema?: AIFlowyFormSchema[];
  buttonText?: string;
  buttonLoading?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  formSchema: () => [],
  buttonText: '更新密码',
  buttonLoading: false,
});

const emit = defineEmits<{
  submit: [Recordable<any>];
}>();

const [Form, formApi] = useAIFlowyForm(
  reactive({
    commonConfig: {
      // 所有表单项
      componentProps: {
        class: 'w-full',
      },
    },
    layout: 'vertical',
    schema: computed(() => props.formSchema),
    showDefaultActions: false,
  }),
);

async function handleSubmit() {
  const { valid } = await formApi.validate();
  const values = await formApi.getValues();
  if (valid) {
    emit('submit', values);
  }
}

defineExpose({
  getFormApi: () => formApi,
});
</script>
<template>
  <div class="mx-auto max-w-[360px] pt-12">
    <Form />
    <AIFlowyButton
      :loading="buttonLoading"
      type="submit"
      class="mx-auto mt-4 block h-8 w-[106px] p-0"
      @click="handleSubmit"
    >
      {{ buttonText }}
    </AIFlowyButton>
  </div>
</template>
