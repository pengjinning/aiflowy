<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import {
  CaretRight,
  CircleCloseFilled,
  Delete,
  Edit,
  More,
  Plus,
  Tickets,
} from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import { router } from '#/router';
import { useDictStore } from '#/store';

import SysJobModal from './SysJobModal.vue';

onMounted(() => {
  initDict();
});
const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const formInline = ref({
  jobName: '',
});
const dictStore = useDictStore();
function initDict() {
  dictStore.fetchDictionary('jobType');
  dictStore.fetchDictionary('jobStatus');
  dictStore.fetchDictionary('yesOrNo');
  dictStore.fetchDictionary('misfirePolicy');
}
function search(formEl: FormInstance | undefined) {
  formEl?.validate((valid) => {
    if (valid) {
      pageDataRef.value.setQuery(formInline.value);
    }
  });
}
function reset(formEl: FormInstance | undefined) {
  formEl?.resetFields();
  pageDataRef.value.setQuery({});
}
function showDialog(row: any) {
  saveDialog.value.openDialog({ ...row });
}
function remove(row: any) {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .post('/api/v1/sysJob/remove', { id: row.id })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              reset(formRef.value);
              done();
            }
          })
          .catch(() => {
            instance.confirmButtonLoading = false;
          });
      } else {
        done();
      }
    },
  }).catch(() => {});
}
function start(row: any) {
  ElMessageBox.confirm($t('message.startAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api.get(`/api/v1/sysJob/start?id=${row.id}`).then((res) => {
          instance.confirmButtonLoading = false;
          if (res.errorCode === 0) {
            ElMessage.success(res.message);
            reset(formRef.value);
            done();
          }
        });
      } else {
        done();
      }
    },
  });
}
function stop(row: any) {
  ElMessageBox.confirm($t('message.stopAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api.get(`/api/v1/sysJob/stop?id=${row.id}`).then((res) => {
          instance.confirmButtonLoading = false;
          if (res.errorCode === 0) {
            ElMessage.success(res.message);
            reset(formRef.value);
            done();
          }
        });
      } else {
        done();
      }
    },
  });
}
function toLogPage(row: any) {
  router.push({
    name: 'SysJobLog',
    query: {
      jobId: row.id,
    },
  });
}
</script>

<template>
  <div class="flex h-full flex-col gap-1.5 p-6">
    <SysJobModal ref="saveDialog" @reload="reset" />
    <div class="flex items-center justify-between">
      <ElForm ref="formRef" :inline="true" :model="formInline">
        <ElFormItem :label="$t('sysJob.jobName')" prop="jobName">
          <ElInput
            v-model="formInline.jobName"
            :placeholder="$t('sysJob.jobName')"
          />
        </ElFormItem>
        <ElFormItem>
          <ElButton @click="search(formRef)" type="primary">
            {{ $t('button.query') }}
          </ElButton>
          <ElButton @click="reset(formRef)">
            {{ $t('button.reset') }}
          </ElButton>
        </ElFormItem>
      </ElForm>
      <div class="handle-div">
        <ElButton
          v-access:code="'/api/v1/sysJob/save'"
          @click="showDialog({})"
          type="primary"
        >
          <ElIcon class="mr-1">
            <Plus />
          </ElIcon>
          {{ $t('button.add') }}
        </ElButton>
      </div>
    </div>

    <div class="bg-background flex-1 rounded-lg p-5">
      <PageData
        ref="pageDataRef"
        page-url="/api/v1/sysJob/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <ElTable :data="pageList" border>
            <ElTableColumn prop="jobName" :label="$t('sysJob.jobName')">
              <template #default="{ row }">
                {{ row.jobName }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="jobType" :label="$t('sysJob.jobType')">
              <template #default="{ row }">
                {{ dictStore.getDictLabel('jobType', row.jobType) }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="cronExpression"
              :label="$t('sysJob.cronExpression')"
            >
              <template #default="{ row }">
                {{ row.cronExpression }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="allowConcurrent"
              :label="$t('sysJob.allowConcurrent')"
            >
              <template #default="{ row }">
                {{ dictStore.getDictLabel('yesOrNo', row.allowConcurrent) }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="misfirePolicy"
              :label="$t('sysJob.misfirePolicy')"
            >
              <template #default="{ row }">
                {{ dictStore.getDictLabel('misfirePolicy', row.misfirePolicy) }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="status" :label="$t('sysJob.status')">
              <template #default="{ row }">
                {{ dictStore.getDictLabel('jobStatus', row.status) }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="created" :label="$t('sysJob.created')">
              <template #default="{ row }">
                {{ row.created }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="remark" :label="$t('sysJob.remark')">
              <template #default="{ row }">
                {{ row.remark }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              :label="$t('common.handle')"
              width="80"
              align="center"
            >
              <template #default="{ row }">
                <ElDropdown>
                  <ElButton link>
                    <ElIcon>
                      <More />
                    </ElIcon>
                  </ElButton>

                  <template #dropdown>
                    <ElDropdownMenu>
                      <div v-access:code="'/api/v1/sysJob/save'">
                        <ElDropdownItem @click="start(row)">
                          <ElButton :icon="CaretRight" link>
                            {{ $t('button.start') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div
                        v-if="row.status === 1"
                        v-access:code="'/api/v1/sysJob/save'"
                      >
                        <ElDropdownItem @click="stop(row)">
                          <ElButton :icon="CircleCloseFilled" link>
                            {{ $t('button.stop') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/sysJob/query'">
                        <ElDropdownItem @click="toLogPage(row)">
                          <ElButton :icon="Tickets" link>
                            {{ $t('button.log') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/sysJob/save'">
                        <ElDropdownItem @click="showDialog(row)">
                          <ElButton :icon="Edit" link>
                            {{ $t('button.edit') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/sysJob/remove'">
                        <ElDropdownItem @click="remove(row)">
                          <ElButton type="danger" :icon="Delete" link>
                            {{ $t('button.delete') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                    </ElDropdownMenu>
                  </template>
                </ElDropdown>
              </template>
            </ElTableColumn>
          </ElTable>
        </template>
      </PageData>
    </div>
  </div>
</template>

<style scoped></style>
