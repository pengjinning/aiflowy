<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { ref } from 'vue';

import { Delete, Edit, More, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
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
  ElTag,
} from 'element-plus';

import { api } from '#/api/request';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import SysApiKeyResourcePermissionList from '#/views/config/apikey/SysApiKeyResourcePermissionList.vue';

import SysApiKeyModal from './SysApiKeyModal.vue';

const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const formInline = ref({
  apiKey: '',
  id: '',
});

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
function showAddPermissionDialog(_row: any) {
  dialogVisible.value = true;
}
const dialogVisible = ref(false);
function remove(row: any) {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .post('/api/v1/sysApiKey/remove', { id: row.id })
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
function addNewApiKey() {
  ElMessageBox.confirm(
    $t('sysApiKey.addApiKeyNotice'),
    $t('message.noticeTitle'),
    {
      confirmButtonText: $t('message.ok'),
      cancelButtonText: $t('message.cancel'),
      type: 'warning',
    },
  ).then(() => {
    api.post('/api/v1/sysApiKey/key/save', {}).then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.saveOkMessage'));
        pageDataRef.value.setQuery({});
      }
    });
  });
}
</script>

<template>
  <div class="flex h-full flex-col gap-1.5 p-6">
    <SysApiKeyModal ref="saveDialog" @reload="reset" />
    <div class="flex items-center justify-between">
      <ElForm ref="formRef" :inline="true" :model="formInline">
        <ElFormItem :label="$t('sysApiKey.apiKey')" prop="apiKey">
          <ElInput
            v-model="formInline.apiKey"
            :placeholder="$t('sysApiKey.apiKey')"
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
        <ElButton @click="addNewApiKey" type="primary">
          <ElIcon class="mr-1">
            <Plus />
          </ElIcon>
          {{ $t('sysApiKey.addApiKey') }}
        </ElButton>
        <ElButton @click="showAddPermissionDialog({})" type="primary">
          <ElIcon class="mr-1">
            <Plus />
          </ElIcon>
          {{ $t('sysApiKeyResourcePermission.addPermission') }}
        </ElButton>
      </div>
    </div>

    <div class="bg-background flex-1 rounded-lg p-5">
      <PageData
        ref="pageDataRef"
        page-url="/api/v1/sysApiKey/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <ElTable :data="pageList" border>
            <ElTableColumn
              prop="apiKey"
              :label="$t('sysApiKey.apiKey')"
              width="280"
            >
              <template #default="{ row }">
                {{ row.apiKey }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="created" :label="$t('sysApiKey.created')">
              <template #default="{ row }">
                {{ row.created }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="status" :label="$t('sysApiKey.status')">
              <template #default="{ row }">
                <ElTag type="primary" v-if="row.status === 1">
                  {{ $t('sysApiKey.actions.enable') }}
                </ElTag>
                <ElTag type="danger" v-else-if="row.status === 0">
                  {{ $t('sysApiKey.actions.disable') }}
                </ElTag>
                <ElTag type="warning" v-else>
                  {{ $t('sysApiKey.actions.failure') }}
                </ElTag>
              </template>
            </ElTableColumn>
            <ElTableColumn prop="expiredAt" :label="$t('sysApiKey.expiredAt')">
              <template #default="{ row }">
                {{ row.expiredAt }}
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
                      <ElDropdownItem @click="showDialog(row)">
                        <ElButton :icon="Edit" link>
                          {{ $t('button.edit') }}
                        </ElButton>
                      </ElDropdownItem>
                      <ElDropdownItem @click="remove(row)">
                        <ElButton type="danger" :icon="Delete" link>
                          {{ $t('button.delete') }}
                        </ElButton>
                      </ElDropdownItem>
                    </ElDropdownMenu>
                  </template>
                </ElDropdown>
              </template>
            </ElTableColumn>
          </ElTable>
        </template>
      </PageData>
    </div>
    <ElDialog v-model="dialogVisible" draggable :close-on-click-modal="false">
      <SysApiKeyResourcePermissionList />
    </ElDialog>
  </div>
</template>
