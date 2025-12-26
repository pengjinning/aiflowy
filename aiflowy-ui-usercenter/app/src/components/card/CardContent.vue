<script setup lang="ts">
import { cn } from '@aiflowy/utils';

interface CardContentProps {
  class?: string;
}

defineOptions({ name: 'CardContent' });
const props = defineProps<CardContentProps>();
</script>

<template>
  <div
    :class="
      cn('flex w-full flex-col gap-1 overflow-hidden text-nowrap', props.class)
    "
  >
    <slot></slot>
  </div>
</template>
