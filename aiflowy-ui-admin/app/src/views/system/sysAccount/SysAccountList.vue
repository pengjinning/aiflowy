<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import { Delete, Edit, More, Plus } from '@element-plus/icons-vue';
import {
  ElAvatar,
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
import defaultAvatar from '#/assets/defaultUserAvatar.png';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import { useDictStore } from '#/store';

import SysAccountModal from './SysAccountModal.vue';

onMounted(() => {
  initDict();
});
const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const formInline = ref({
  loginName: '',
});
const dictStore = useDictStore();
function initDict() {
  dictStore.fetchDictionary('dataStatus');
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
          .post('/api/v1/sysAccount/remove', { id: row.id })
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
function isAdmin(data: any) {
  return data?.accountType === 1 || data?.accountType === 99;
}
</script>

<template>
  <div class="flex h-full flex-col gap-1.5 p-6">
    <SysAccountModal ref="saveDialog" @reload="reset" />
    <div class="flex items-center justify-between">
      <ElForm ref="formRef" :inline="true" :model="formInline">
        <ElFormItem :label="$t('sysAccount.loginName')" prop="loginName">
          <ElInput
            v-model="formInline.loginName"
            :placeholder="`${$t('sysAccount.loginName')}`"
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
          v-access:code="'/api/v1/sysAccount/save'"
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
        page-url="/api/v1/sysAccount/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <ElTable :data="pageList" border>
            <ElTableColumn
              prop="avatar"
              align="center"
              :label="$t('sysAccount.avatar')"
            >
              <template #default="{ row }">
                <ElAvatar :src="row.avatar || defaultAvatar" />
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="loginName"
              align="center"
              :label="$t('sysAccount.loginName')"
            >
              <template #default="{ row }">
                {{ row.loginName }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="nickname"
              align="center"
              :label="$t('sysAccount.nickname')"
            >
              <template #default="{ row }">
                {{ row.nickname }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="mobile"
              align="center"
              width="120"
              :label="$t('sysAccount.mobile')"
            >
              <template #default="{ row }">
                {{ row.mobile }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="email"
              align="center"
              width="200"
              :label="$t('sysAccount.email')"
            >
              <template #default="{ row }">
                {{ row.email }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="status"
              align="center"
              :label="$t('sysAccount.status')"
            >
              <template #default="{ row }">
                {{ dictStore.getDictLabel('dataStatus', row.status) }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="created"
              width="160"
              :label="$t('sysAccount.created')"
            >
              <template #default="{ row }">
                {{ row.created }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="remark" :label="$t('sysAccount.remark')">
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
                <ElDropdown v-if="!isAdmin(row)">
                  <ElButton link>
                    <ElIcon>
                      <More />
                    </ElIcon>
                  </ElButton>

                  <template #dropdown>
                    <ElDropdownMenu>
                      <div v-access:code="'/api/v1/sysAccount/save'">
                        <ElDropdownItem @click="showDialog(row)">
                          <ElButton :icon="Edit" link>
                            {{ $t('button.edit') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/sysAccount/remove'">
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
