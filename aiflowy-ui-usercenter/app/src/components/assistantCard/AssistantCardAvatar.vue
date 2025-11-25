<script setup lang="ts">
import { ElAvatar } from 'element-plus';

const props = defineProps({
  class: {
    type: String,
  },
});
</script>

<template>
  <ElAvatar :size="36" class="shrink-0" />
</template>
