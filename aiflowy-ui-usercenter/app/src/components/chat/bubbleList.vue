<script setup lang="ts">
import { useUserStore } from '@aiflowy/stores';

import { ElAvatar } from 'element-plus';

import defaultAssistantAvatar from '#/assets/defaultAssistantAvatar.svg';
import defaultUserAvatar from '#/assets/defaultUserAvatar.png';

// type listType = BubbleListItemProps & {
//   key: number;
//   role: 'assistant' | 'user';
// };
// const messageList: BubbleListProps<listType>['list'] = [
//   {
//     key: 0,
//     role: 'user',
//     placement: 'end',
//     content: '哈哈哈，让我试试',
//     typing: true,
//   },
//   {
//     key: 1,
//     role: 'assistant',
//     placement: 'start',
//     content: '💖 感谢使用 Element Plus X ! 你的支持，是我们开源的最强动力 ~',
//     typing: true,
//   },
//   {
//     key: 2,
//     role: 'user',
//     placement: 'end',
//     content: '哈哈哈，让我试试',
//     typing: true,
//   },
//   {
//     key: 3,
//     role: 'assistant',
//     placement: 'start',
//     content: '💖 感谢使用 Element Plus X ! 你的支持，是我们开源的最强动力 ~',
//     loading: true,
//   },
// ];
interface Props {
  bot: any;
  messages: any[];
}
const props = defineProps<Props>();
const store = useUserStore();

function getAssistantAvatar() {
  return props.bot.icon || defaultAssistantAvatar;
}
function getUserAvatar() {
  return store.userInfo?.avatar || defaultUserAvatar;
}
</script>

<template>
  <el-bubble-list :list="messages" max-height="calc(100vh - 345px)">
    <!-- 自定义头像 -->
    <template #avatar="{ item }">
      <ElAvatar
        :src="
          item.role === 'assistant' ? getAssistantAvatar() : getUserAvatar()
        "
        :size="40"
      />
    </template>

    <!-- 自定义头部 -->
    <template #header="{ item }">
      <span class="text-foreground/50 text-xs">
        {{ item.created }}
      </span>
    </template>

    <!-- 自定义气泡内容 -->
    <template #content="{ item }">
      <el-x-markdown :markdown="item.content" />
    </template>

    <!-- 自定义底部 -->
    <!--<template #footer="{ item }">
      <div class="flex items-center">
        <template v-if="item.role === 'assistant'">
          <ElButton :icon="RefreshRight" link />
          <ElButton :icon="CopyDocument" link />
        </template>
        <template v-else>
          <ElButton :icon="CopyDocument" link />
          <ElButton :icon="EditPen" link />
        </template>
      </div>
    </template>-->
  </el-bubble-list>
</template>
