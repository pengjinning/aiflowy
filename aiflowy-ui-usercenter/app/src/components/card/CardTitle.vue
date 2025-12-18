<script setup lang="ts">
import { cn } from '@aiflowy/utils';

interface CardTitleProps {
  class?: string;
}

defineOptions({ name: 'CardTitle' });
const props = defineProps<CardTitleProps>();
</script>

<template>
  <span
    :class="
      cn('overflow-hidden text-ellipsis text-base font-semibold', props.class)
    "
  >
    <slot></slot>
  </span>
</template>
