<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import { Delete, Edit, More, Plus } from '@element-plus/icons-vue';
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
import { useDictStore } from '#/store';

import SysRoleModal from './SysRoleModal.vue';

onMounted(() => {
  initDict();
});
const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const formInline = ref({
  roleName: '',
});
const dictStore = useDictStore();
function initDict() {
  dictStore.fetchDictionary('dataStatus');
}
function isAdminRole(data: any) {
  return data?.roleKey === 'super_admin';
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
          .post('/api/v1/sysRole/remove', { id: row.id })
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
</script>

<template>
  <div class="flex h-full flex-col gap-1.5 p-6">
    <SysRoleModal ref="saveDialog" @reload="reset" />
    <div class="flex items-center justify-between">
      <ElForm ref="formRef" :inline="true" :model="formInline">
        <ElFormItem :label="$t('sysRole.roleName')" prop="roleName">
          <ElInput
            v-model="formInline.roleName"
            :placeholder="$t('sysRole.roleName')"
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
          v-access:code="'/api/v1/sysRole/save'"
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
        page-url="/api/v1/sysRole/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <ElTable :data="pageList" border>
            <ElTableColumn prop="roleName" :label="$t('sysRole.roleName')">
              <template #default="{ row }">
                {{ row.roleName }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="roleKey" :label="$t('sysRole.roleKey')">
              <template #default="{ row }">
                {{ row.roleKey }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="status" :label="$t('sysRole.status')">
              <template #default="{ row }">
                {{ dictStore.getDictLabel('dataStatus', row.status) }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="created" :label="$t('sysRole.created')">
              <template #default="{ row }">
                {{ row.created }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="remark" :label="$t('sysRole.remark')">
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
                <ElDropdown v-if="!isAdminRole(row)">
                  <ElButton link>
                    <ElIcon>
                      <More />
                    </ElIcon>
                  </ElButton>

                  <template #dropdown>
                    <ElDropdownMenu>
                      <div v-access:code="'/api/v1/sysRole/save'">
                        <ElDropdownItem @click="showDialog(row)">
                          <ElButton :icon="Edit" link>
                            {{ $t('button.edit') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/sysRole/remove'">
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
