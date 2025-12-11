<script setup lang="ts">
import type { BubbleListProps } from 'vue-element-plus-x/types/BubbleList';
import type { TypewriterInstance } from 'vue-element-plus-x/types/Typewriter';

import type { BotInfo, ChatMessage } from '@aiflowy/types';

import { onMounted, ref, watchEffect } from 'vue';
import { BubbleList, Sender } from 'vue-element-plus-x';
import { useRoute, useRouter } from 'vue-router';

import { useUserStore } from '@aiflowy/stores';
import { cn, tryit, uuid } from '@aiflowy/utils';

import {
  DocumentCopy,
  Microphone,
  Paperclip,
  Promotion,
  Refresh,
} from '@element-plus/icons-vue';
import { ElAvatar, ElButton, ElIcon, ElSpace } from 'element-plus';

import { getMessageList } from '#/api';
import { api, sse } from '#/api/request';
import MarkdownRenderer from '#/components/chat/MarkdownRenderer.vue';

import BotAvatar from '../botAvatar/botAvatar.vue';
// import RecordingIcon from '../icons/RecordingIcon.vue';
import SendingIcon from '../icons/SendingIcon.vue';

interface queryMessageType {
  botId: string;
  isExternalMsg: number;
  sessionId?: string;
}
const props = defineProps<{
  bot?: BotInfo;
  // 是否是外部消息 1 外部消息 0 内部消息
  isExternalMsg: number;
  sessionId?: string;
}>();
const route = useRoute();
const botId = ref<string>((route.params.id as string) || '');

const { postSse } = sse();
const router = useRouter();
const userStore = useUserStore();
const bubbleItems = ref<BubbleListProps<ChatMessage>['list']>([]);
const senderRef = ref<InstanceType<typeof Sender>>();
const senderValue = ref('');
const sending = ref(false);
const sessionId = ref(
  props.sessionId && props.sessionId.length > 0 ? props.sessionId : uuid(),
);
const historyMessage = ref<ChatMessage[]>([]);
const getMessagesHistory = () => {
  const tempParams: queryMessageType = {
    botId: botId.value,
    isExternalMsg: props.isExternalMsg,
  };

  // 如果是外部地址获取记录
  if (props.isExternalMsg === 1) {
    tempParams.sessionId = props.sessionId;
  }
  api
    .get('/api/v1/aiBotMessage/list', {
      params: {
        ...tempParams,
      },
    })
    .then((res) => {
      bubbleItems.value = res.data.map((item: ChatMessage) => {
        return item.role === 'assistant'
          ? {
              ...item,
              placement: 'start',
              isAssistant: true,
            }
          : {
              ...item,
              placement: 'end',
              isAssistant: false,
            };
      });
    });
};
onMounted(() => {
  getMessagesHistory();
});
watchEffect(async () => {
  if (props.bot && props.sessionId) {
    const [, res] = await tryit(
      getMessageList({
        sessionId: props.sessionId,
        botId: props.bot.id,
        tempUserId: uuid() + props.bot.id,
        isExternalMsg: 1,
      }),
    );

    if (res?.errorCode === 0) {
      bubbleItems.value = res.data.map((item) => ({
        ...item,
        content:
          item.role === 'assistant'
            ? item.content.replace(/^Final Answer:\s*/i, '')
            : item.content,
        placement: item.role === 'assistant' ? 'start' : 'end',
      }));
    }
  } else {
    bubbleItems.value = [];
  }
});

const handleSubmit = async () => {
  sending.value = true;
  const data = {
    botId: botId.value,
    // fileList: [],
    // isExternalMsg: 1,
    prompt: senderValue.value,
    // tempUserId: uuid() + props.bot?.id,
    sessionId: sessionId.value,
  };

  const mockMessages = generateMockMessages();
  bubbleItems.value.push(...mockMessages);
  senderRef.value?.clear();

  postSse('/api/v1/aiBot/chat', data, {
    onMessage(message) {
      const content = message.data!.replace(/^Final Answer:\s*/i, '');
      const lastBubbleItem = bubbleItems.value[bubbleItems.value.length - 1];

      if (lastBubbleItem) {
        if (content === lastBubbleItem.content) {
          sending.value = false;
        } else {
          bubbleItems.value[bubbleItems.value.length - 1] = {
            ...lastBubbleItem,
            content: lastBubbleItem.content + content,
            loading: false,
            typing: true,
          };
        }
      }
    },
  });
};
const handleComplete = (_: TypewriterInstance, index: number) => {
  if (
    index === bubbleItems.value.length - 1 &&
    props.sessionId &&
    props.sessionId.length <= 0 &&
    sending.value === false
  ) {
    setTimeout(() => {
      router.replace({ params: { sessionId: sessionId.value } });
    }, 100);
  }
};

const generateMockMessages = () => {
  const userMessage: ChatMessage = {
    role: 'user',
    id: Date.now().toString(),
    fileList: [],
    content: senderValue.value,
    created: Date.now(),
    updateAt: Date.now(),
    placement: 'end',
  };

  const assistantMessage: ChatMessage = {
    role: 'assistant',
    id: Date.now().toString(),
    content: '',
    loading: true,
    created: Date.now(),
    updateAt: Date.now(),
    placement: 'start',
  };

  return [userMessage, assistantMessage];
};
</script>

<template>
  <div class="mx-auto h-full max-w-[780px]">
    <div
      :class="
        cn(
          'flex h-full w-full flex-col gap-3',
          !sessionId && 'items-center justify-center gap-8',
        )
      "
    >
      <!-- 对话列表 -->
      <div
        v-if="sessionId || bubbleItems.length > 0"
        class="w-full flex-1 overflow-hidden"
      >
        <BubbleList
          class="!h-full"
          :list="bubbleItems"
          max-height="none"
          @complete="handleComplete"
        >
          <!-- 自定义头像 -->
          <template #avatar="{ item }">
            <BotAvatar
              v-if="item.role === 'assistant'"
              :src="bot?.icon"
              :size="40"
            />
            <ElAvatar v-else :src="userStore.userInfo?.avatar" :size="40" />
          </template>

          <!-- 自定义头部 -->
          <template #header="{ item }">
            <div class="text-sm text-[#979797]">
              {{
                item.role === 'assistant'
                  ? bot?.title
                  : userStore.userInfo?.nickname
              }}
            </div>
          </template>
          <!-- 核心：使用 MarkdownRenderer 渲染助手消息 -->
          <template #content="{ item }">
            <!-- 助手消息：渲染 Markdown -->
            <MarkdownRenderer :content="item.content" />
          </template>
          <!-- 自定义底部 -->
          <template #footer="{ item }">
            <ElSpace :size="10">
              <ElSpace>
                <ElButton
                  v-if="item.role === 'assistant'"
                  type="info"
                  :icon="Refresh"
                  size="small"
                  circle
                />
                <ElButton
                  color="#626aef"
                  :icon="DocumentCopy"
                  size="small"
                  circle
                />
              </ElSpace>
              <div class="text-xs">
                {{ new Date(item.created).toLocaleString() }}
              </div>
            </ElSpace>
          </template>
        </BubbleList>
      </div>

      <!-- 新对话显示bot信息 -->
      <div v-else class="flex flex-col items-center gap-3.5">
        <BotAvatar :src="bot?.icon" :size="88" />
        <h1 class="text-base font-medium text-black/85">
          {{ bot?.title }}
        </h1>
        <span class="text-sm text-[#757575]">{{ bot?.description }}</span>
      </div>

      <!-- Sender -->
      <Sender
        ref="senderRef"
        class="w-full"
        v-model="senderValue"
        variant="updown"
        :auto-size="{ minRows: 3, maxRows: 6 }"
        allow-speech
        @submit="handleSubmit"
      >
        <!-- 自定义头部内容 -->
        <!-- <template #header></template> -->
        <template #action-list>
          <ElSpace>
            <ElButton circle>
              <ElIcon><Paperclip /></ElIcon>
            </ElButton>
            <ElButton circle>
              <ElIcon><Microphone /></ElIcon>
              <!-- <ElIcon color="#0066FF"><RecordingIcon /></ElIcon> -->
            </ElButton>
            <ElButton v-if="sending" circle>
              <ElIcon size="30" color="#409eff"><SendingIcon /></ElIcon>
            </ElButton>
            <ElButton v-else circle color="#0066FF" @click="handleSubmit">
              <ElIcon><Promotion /></ElIcon>
            </ElButton>
          </ElSpace>
        </template>
      </Sender>
    </div>
  </div>
</template>
