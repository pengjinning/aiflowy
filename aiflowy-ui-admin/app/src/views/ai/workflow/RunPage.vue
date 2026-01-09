<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { sortNodes } from '@aiflowy/utils';

import { ArrowLeft } from '@element-plus/icons-vue';
import { ElAvatar, ElButton, ElCard, ElCol, ElRow } from 'element-plus';

import { api } from '#/api/request';
import workflowIcon from '#/assets/ai/workflow/workflowIcon.png';
import { $t } from '#/locales';
import { router } from '#/router';
import ExecResult from '#/views/ai/workflow/components/ExecResult.vue';
import WorkflowForm from '#/views/ai/workflow/components/WorkflowForm.vue';
import WorkflowSteps from '#/views/ai/workflow/components/WorkflowSteps.vue';

onMounted(async () => {
  pageLoading.value = true;
  await Promise.all([getWorkflowInfo(workflowId.value), getRunningParams()]);
  pageLoading.value = false;
});
const pageLoading = ref(false);
const route = useRoute();
const workflowId = ref(route.query.id);
const workflowInfo = ref<any>({});
const runParams = ref<any>(null);
const initState = ref(false);
const tinyFlowData = ref<any>(null);
const workflowForm = ref();
async function getWorkflowInfo(workflowId: any) {
  api.get(`/api/v1/workflow/detail?id=${workflowId}`).then((res) => {
    workflowInfo.value = res.data;
    tinyFlowData.value = workflowInfo.value.content
      ? JSON.parse(workflowInfo.value.content)
      : {};
  });
}
async function getRunningParams() {
  api
    .get(`/api/v1/workflow/getRunningParameters?id=${workflowId.value}`)
    .then((res) => {
      runParams.value = res.data;
    });
}
function onSubmit() {
  initState.value = !initState.value;
}
function resumeChain(data: any) {
  workflowForm.value?.resume(data);
}
const chainInfo = ref<any>(null);
function onAsyncExecute(info: any) {
  chainInfo.value = info;
}
</script>

<template>
  <div
    v-loading="pageLoading"
    class="bg-background-deep flex h-full max-h-[calc(100vh-90px)] w-full flex-col gap-6 overflow-hidden p-6"
  >
    <div>
      <ElButton :icon="ArrowLeft" @click="router.back()">
        {{ $t('button.back') }}
      </ElButton>
    </div>
    <div
      class="flex h-[150px] shrink-0 items-center gap-6 rounded-lg border border-[var(--el-border-color)] bg-[var(--el-bg-color)] pl-11"
    >
      <ElAvatar
        class="shrink-0"
        :src="workflowInfo.icon ?? workflowIcon"
        :size="72"
      />
      <div class="flex flex-col gap-5">
        <span class="text-2xl font-medium">{{ workflowInfo.title }}</span>
        <span class="text-base text-[#75808d]">{{
          workflowInfo.description
        }}</span>
      </div>
    </div>
    <ElRow class="h-full overflow-hidden" :gutter="10">
      <ElCol :span="10" class="h-full overflow-hidden">
        <div class="grid h-full grid-rows-2 gap-2.5">
          <ElCard shadow="never" style="height: 100%; overflow: auto">
            <div class="mb-2.5 font-semibold">
              {{ $t('aiWorkflow.params') }}：
            </div>
            <WorkflowForm
              v-if="runParams && tinyFlowData"
              ref="workflowForm"
              :workflow-id="workflowId"
              :workflow-params="runParams"
              :on-submit="onSubmit"
              :on-async-execute="onAsyncExecute"
              :tiny-flow-data="tinyFlowData"
            />
          </ElCard>
          <ElCard shadow="never" style="height: 100%; overflow: auto">
            <div class="mb-2.5 font-semibold">
              {{ $t('aiWorkflow.steps') }}：
            </div>
            <WorkflowSteps
              v-if="tinyFlowData"
              :workflow-id="workflowId"
              :node-json="sortNodes(tinyFlowData)"
              :init-signal="initState"
              :polling-data="chainInfo"
              @resume="resumeChain"
            />
          </ElCard>
        </div>
      </ElCol>
      <ElCol :span="14">
        <ElCard shadow="never" style="height: 100%; overflow: auto">
          <div class="mb-2.5 mt-2.5 font-semibold">
            {{ $t('aiWorkflow.result') }}：
          </div>
          <ExecResult
            v-if="tinyFlowData"
            :workflow-id="workflowId"
            :node-json="sortNodes(tinyFlowData)"
            :init-signal="initState"
            :polling-data="chainInfo"
          />
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>
