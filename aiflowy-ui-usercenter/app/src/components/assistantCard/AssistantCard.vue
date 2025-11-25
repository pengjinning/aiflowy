<template>
  <div
    class="flex w-full max-w-60 items-start gap-2.5 pb-2.5 pl-2.5 pr-5 pt-3.5"
  >
    <slot name="avatar"><ElAvatar :size="36" class="shrink-0" /></slot>
    <div class="flex flex-col gap-1 overflow-hidden text-nowrap">
      <span
        class="overflow-hidden text-ellipsis text-base font-semibold text-[#323233]"
      >
        {{ assistant.title }}
      </span>
      <span class="overflow-hidden text-ellipsis text-xs text-[#ABB0B9]">
        {{ assistant.description }}
      </span>
    </div>
  </div>
</template>
