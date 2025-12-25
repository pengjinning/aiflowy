<template>
  <div class="mb-10 sm:mx-auto sm:w-full sm:max-w-md">
    <h2
      class="text-foreground mb-3.5 text-2xl font-medium leading-9 tracking-tight lg:text-3xl"
    >
      <slot></slot>
    </h2>

    <p class="text-muted-foreground text-sm lg:text-base">
      <slot name="desc"></slot>
    </p>
  </div>
</template>
