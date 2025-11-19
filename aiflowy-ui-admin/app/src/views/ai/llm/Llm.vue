<script setup>
import { markRaw, onMounted, ref } from 'vue';

import { Delete, Edit, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
  ElIcon,
  ElImage,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { getLlmBrandList } from '#/api/ai/llm.js';
import CategoryPanel from '#/components/categoryPanel/CategoryPanel.vue';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';
import Upload from '#/components/upload/Upload.vue';
import UploadAvatar from '#/components/upload/UploadAvatar.vue';

const brandListData = ref([]);
const dialogTitle = ref('新增');
const LlmAddOrUpdateDialog = ref(false);
onMounted(() => {
  getLlmBrandList().then((res) => {
    brandListData.value = res.data;
  });
});

const handleCategoryClick = (category) => {};
const uploadSuccess = (res) => {
  console.log('上传成功');
  console.log(res);
};
// 按钮配置
const headerButtons = ref([
  {
    key: 'add',
    text: '新增大模型',
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'addLlm' },
  },
  {
    key: 'edit',
    text: '一键添加',
    type: 'primary',
    icon: markRaw(Plus),
    data: { action: 'oneClickAdd' },
  },
]);

const addLlm = () => {
  LlmAddOrUpdateDialog.value = true;
};

const oneClickAdd = () => {};
// 处理搜索事件
const handleSearch = (searchValue) => {
  // 执行搜索逻辑
};
// 处理按钮点击事件
const handleButtonClick = (event) => {
  // 根据按钮 key 执行不同操作
  switch (event.key) {
    case 'add': {
      addLlm();
      break;
    }
    case 'edit': {
      oneClickAdd();
      break;
    }
  }
};

const handleClose = () => {};
</script>

<template>
  <div class="llm-container">
    <Upload @success="uploadSuccess" :multiple="false" />
    <div class="llm-header">
      <HeaderSearch
        :buttons="headerButtons"
        search-placeholder="搜索用户"
        @search="handleSearch"
        @button-click="handleButtonClick"
      />
    </div>

    <div class="llm-content">
      <div>
        <CategoryPanel
          :categories="brandListData"
          title-key="title"
          :use-img-for-svg="true"
          :expand-width="150"
          @click="handleCategoryClick"
        />
      </div>
      <div class="llm-table">
        <PageData
          page-url="/api/v1/aiLlm/page"
          :page-size="10"
          :init-query-params="{ status: 1 }"
        >
          <template #default="{ pageList }">
            <ElTable :data="pageList" style="width: 100%">
              <ElTableColumn prop="icon" label="Icon" width="80">
                <template #default="scope">
                  <ElImage
                    v-if="scope.row.icon"
                    :src="scope.row.icon"
                    style="width: 30px; height: 30px"
                  />
                </template>
              </ElTableColumn>
              <ElTableColumn prop="id" label="id" width="180" />
              <ElTableColumn prop="title" label="名称" width="180" />
              <ElTableColumn
                prop="description"
                label="描述"
                width="300"
                show-overflow-tooltip
              />
              <ElTableColumn fixed="right" label="操作" min-width="120">
                <template #default>
                  <ElButton link type="primary">
                    <ElIcon class="mr-1">
                      <Edit />
                    </ElIcon>
                    编辑
                  </ElButton>
                  <ElButton link type="primary">
                    <ElIcon class="mr-1">
                      <Delete />
                    </ElIcon>
                    删除
                  </ElButton>
                </template>
              </ElTableColumn>
            </ElTable>
          </template>
        </PageData>
      </div>
    </div>

    <!--    新增大模型对话框-->
    <ElDialog
      v-model="LlmAddOrUpdateDialog"
      :title="dialogTitle"
      width="500"
      :before-close="handleClose"
    >
      <div class="llm-dialog-container">
        <UploadAvatar :allowed-image-types="['image/jpeg']" />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <ElButton @click="LlmAddOrUpdateDialog = false">Cancel</ElButton>
          <ElButton type="primary" @click="LlmAddOrUpdateDialog = false">
            Confirm
          </ElButton>
        </div>
      </template>
    </ElDialog>
  </div>
</template>

<style scoped>
.llm-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.llm-header {
  margin-bottom: 20px;
}

.llm-content {
  display: flex;
}

.llm-table {
  width: 100%;
}

.llm-dialog-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
