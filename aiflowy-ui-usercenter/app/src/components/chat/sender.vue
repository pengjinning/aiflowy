<script setup lang="ts">
import { ref } from 'vue';
import { Sender } from 'vue-element-plus-x';

import { Promotion } from '@element-plus/icons-vue';
import { ElButton, ElIcon } from 'element-plus';

const senderValue = ref('');
</script>

<template>
  <Sender
    v-model="senderValue"
    variant="updown"
    :auto-size="{ minRows: 2, maxRows: 5 }"
    clearable
    allow-speech
    placeholder="发送消息"
  >
    <!-- 自定义 prefix 前缀 -->
    <!-- <template #prefix>
    </template> -->

    <template #action-list>
      <div style="display: flex; align-items: center; gap: 8px">
        <ElButton round color="#626aef">
          <ElIcon><Promotion /></ElIcon>
        </ElButton>
      </div>
    </template>
  </Sender>
</template>
