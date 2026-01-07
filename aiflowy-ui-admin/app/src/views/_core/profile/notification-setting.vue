<script setup lang="ts">
import { computed } from 'vue';

import { ProfileNotificationSetting } from '@aiflowy/common-ui';

import { $t } from '#/locales';

const formSchema = computed(() => {
  return [
    {
      value: true,
      fieldName: 'accountPassword',
      label: $t('page.auth.accountPassword'),
      description: $t('page.description.accountPassword'),
    },
    {
      value: true,
      fieldName: 'systemMessage',
      label: $t('page.auth.systemMessage'),
      description: $t('page.description.systemMessage'),
    },
    {
      value: true,
      fieldName: 'todoTask',
      label: $t('page.auth.todoTasks'),
      description: $t('page.description.todoTasks'),
    },
  ];
});
</script>
<template>
  <ProfileNotificationSetting :form-schema="formSchema" />
</template>
