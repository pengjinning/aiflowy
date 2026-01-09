<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { markRaw, ref } from 'vue';

import { Delete, MoreFilled, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElMessage,
  ElMessageBox,
  ElSwitch,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';

import McpModal from './McpModal.vue';

const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
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
          .post('/api/v1/mcp/remove', { id: row.id })
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
const handleUpdate = (row: any) => {
  api.post('/api/v1/mcp/update', { ...row }).then((res) => {
    if (res.errorCode === 0) {
      ElMessage.success($t('message.updateOkMessage'));
    }
  });
};
const headerButtons = [
  {
    key: 'create',
    type: 'primary',
    text: $t('button.add'),
    icon: markRaw(Plus),
    data: { action: 'create' },
  },
];
const handleSearch = (params: string) => {
  pageDataRef.value.setQuery({ packageName: params, isQueryOr: true });
};
const handleHeaderButtonClick = (button: any) => {
  if (button.key === 'create') {
    showDialog({});
  }
};
</script>

<template>
  <div class="flex h-full flex-col gap-6 p-6">
    <McpModal ref="saveDialog" @reload="reset" />
    <HeaderSearch
      :buttons="headerButtons"
      @search="handleSearch"
      @button-click="handleHeaderButtonClick"
    />
    <div class="bg-background border-border flex-1 rounded-lg border p-5">
      <PageData ref="pageDataRef" page-url="/api/v1/mcp/page" :page-size="10">
        <template #default="{ pageList }">
          <ElTable :data="pageList" border>
            <ElTableColumn prop="title" :label="$t('mcp.title')">
              <template #default="{ row }">
                {{ row.title }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="description" :label="$t('mcp.description')">
              <template #default="{ row }">
                {{ row.description }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="created" :label="$t('mcp.created')">
              <template #default="{ row }">
                {{ row.created }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="status" :label="$t('mcp.status')">
              <template #default="{ row }">
                <ElSwitch v-model="row.status" @change="handleUpdate(row)" />
              </template>
            </ElTableColumn>
            <ElTableColumn
              :label="$t('common.handle')"
              width="120"
              align="right"
            >
              <template #default="{ row }">
                <div class="flex items-center gap-3">
                  <div v-access:code="'/api/v1/mcp/save'">
                    <ElButton type="primary" link @click="showDialog(row)">
                      {{ $t('button.edit') }}
                    </ElButton>
                  </div>
                  <ElDropdown>
                    <ElButton link :icon="MoreFilled" />
                    <template #dropdown>
                      <ElDropdownMenu>
                        <div v-access:code="'/api/v1/mcp/remove'">
                          <ElDropdownItem @click="remove(row)">
                            <ElButton type="danger" :icon="Delete" link>
                              {{ $t('button.delete') }}
                            </ElButton>
                          </ElDropdownItem>
                        </div>
                      </ElDropdownMenu>
                    </template>
                  </ElDropdown>
                </div>
              </template>
            </ElTableColumn>
          </ElTable>
        </template>
      </PageData>
    </div>
  </div>
</template>

<style scoped></style>
