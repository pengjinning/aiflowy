<script setup lang="ts">
import { cn } from '@aiflowy/utils';

interface CardDescriptionProps {
  class?: string;
}

defineOptions({ name: 'CardDescription' });
const props = defineProps<CardDescriptionProps>();
</script>

<template>
  <span
    :class="
      cn(
        'text-foreground/50 overflow-hidden text-ellipsis text-xs',
        props.class,
      )
    "
  >
    <slot></slot>
  </span>
</template>
