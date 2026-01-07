<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { ArrowDown, Back, VideoPlay } from '@element-plus/icons-vue';
import {
  ElButton,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessage,
  ElOption,
  ElSelect,
} from 'element-plus';

import { api } from '#/api/request';
import PluginInputAndOutParams from '#/views/ai/plugin/PluginInputAndOutParams.vue';
import PluginRunTestModal from '#/views/ai/plugin/PluginRunTestModal.vue';

const route = useRoute();
const router = useRouter();

const toolId = ref<string>((route.query.id as string) || '');

onMounted(() => {
  if (!toolId.value) {
    return;
  }
  getPluginToolInfo();
});
const pluginToolInfo = ref<any>({
  name: '',
  englishName: '',
  description: '',
  basePath: '',
  requestMethod: '',
});
const pluginInfo = ref<any>({});
const pluginInputData = ref<any[]>([]);
const pluginOutputData = ref<any[]>([]);

function getPluginToolInfo() {
  api
    .post('/api/v1/pluginItem/tool/search', {
      aiPluginToolId: toolId.value,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        pluginToolInfo.value = res.data.data;
        pluginInfo.value = res.data.aiPlugin;
        pluginInputData.value = JSON.parse(res.data.data.inputData || '[]');
        pluginOutputData.value = JSON.parse(res.data.data.outputData || '[]');
      }
    });
}
const pluginInputParamsEditable = ref(false);
const pluginOutputParamsEditable = ref(false);

const pluginBasicCollapse = ref({
  title: $t('pluginItem.pluginToolEdit.basicInfo'),
  isOpen: true,
  isEdit: false,
});
const pluginBasicCollapseInputParams = ref({
  title: $t('pluginItem.pluginToolEdit.configureInputParameters'),
  isOpen: false,
  isEdit: false,
});
const pluginBasicCollapseOutputParams = ref({
  title: $t('pluginItem.pluginToolEdit.configureOutputParameters'),
  isOpen: false,
  isEdit: false,
});
const pluginInputParamsRef = ref();
const pluginOutputParamsRef = ref();
const handleClickHeader = (index: number) => {
  switch (index) {
    case 1: {
      pluginBasicCollapse.value.isOpen = !pluginBasicCollapse.value.isOpen;
      break;
    }
    case 2: {
      pluginBasicCollapseInputParams.value.isOpen =
        !pluginBasicCollapseInputParams.value.isOpen;

      break;
    }
    case 3: {
      pluginBasicCollapseOutputParams.value.isOpen =
        !pluginBasicCollapseOutputParams.value.isOpen;

      break;
    }
    // No default
  }
};

const back = () => {
  router.back();
};
const rules = reactive({
  name: [{ required: true, message: $t('message.required'), trigger: 'blur' }],
  requestMethod: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
  basePath: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  englishName: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  description: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
      whiteSpace: true,
    },
  ],
});
const saveForm = ref();
const updatePluginTool = (index: number) => {
  if (index === 1) {
    if (!saveForm.value) return;
    saveForm.value.validate((valid: boolean) => {
      if (valid) {
        updatePluginToolInfo(index);
      }
    });
  } else {
    updatePluginToolInfo(index);
  }
};
const updatePluginToolInfo = (index: number) => {
  api
    .post('/api/v1/pluginItem/tool/update', {
      id: toolId.value,
      name: pluginToolInfo.value.name,
      englishName: pluginToolInfo.value.englishName,
      description: pluginToolInfo.value.description,
      basePath: pluginToolInfo.value.basePath,
      requestMethod: pluginToolInfo.value.requestMethod,
      inputData: JSON.stringify(pluginInputData.value),
      outputData: JSON.stringify(pluginOutputData.value),
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.updateOkMessage'));
        switch (index) {
          case 1: {
            pluginBasicCollapse.value.isEdit = false;

            break;
          }
          case 2: {
            pluginBasicCollapseInputParams.value.isEdit = false;

            break;
          }
          case 3: {
            pluginBasicCollapseOutputParams.value.isEdit = false;

            break;
          }
          // No default
        }
      }
    });
};
const handleEdit = (index: number) => {
  switch (index) {
    case 1: {
      pluginBasicCollapse.value.isEdit = true;
      break;
    }
    case 2: {
      pluginBasicCollapseInputParams.value.isEdit = true;
      pluginBasicCollapseInputParams.value.isOpen = true;
      pluginInputParamsEditable.value = true;
      break;
    }
    case 3: {
      pluginBasicCollapseOutputParams.value.isEdit = true;
      pluginBasicCollapseOutputParams.value.isOpen = true;
      pluginOutputParamsEditable.value = true;
      break;
    }
    // No default
  }
};
const handleSave = (index: number) => {
  if (index === 2) {
    try {
      // 调用校验方法，若抛异常则进入 catch
      pluginInputParamsRef.value.handleSubmitParams();
    } catch (error) {
      console.error('校验失败:', error);
      return;
    }
  }
  if (index === 3) {
    try {
      pluginOutputParamsRef.value.handleSubmitParams();
    } catch (error) {
      console.error('校验失败:', error);
      return;
    }
  }
  pluginInputParamsEditable.value = false;
  updatePluginTool(index);
};
const handleCancel = (index: number) => {
  getPluginToolInfo();
  switch (index) {
    case 1: {
      pluginBasicCollapse.value.isEdit = false;

      break;
    }
    case 2: {
      pluginBasicCollapseInputParams.value.isEdit = false;
      pluginInputParamsEditable.value = false;
      break;
    }
    case 3: {
      pluginBasicCollapseOutputParams.value.isEdit = false;
      pluginOutputParamsEditable.value = false;
      break;
    }
    // No default
  }
};
const requestMethodOptions = [
  {
    label: 'POST',
    value: 'POST',
  },
  {
    label: 'GET',
    value: 'GET',
  },
  {
    label: 'PUT',
    value: 'PUT',
  },
  {
    label: 'DELETE',
    value: 'DELETE',
  },
  {
    label: 'PATCH',
    value: 'PATCH',
  },
];
const runTestRef = ref();
const handleOpenRunModal = () => {
  runTestRef.value.openDialog();
};
</script>

<template>
  <div class="accordion-container">
    <div class="controls-header">
      <ElButton @click="back" :icon="Back">
        {{ $t('button.back') }}
      </ElButton>
      <ElButton type="primary" :icon="VideoPlay" @click="handleOpenRunModal">
        {{ $t('pluginItem.pluginToolEdit.trialRun') }}
      </ElButton>
    </div>
    <!-- 折叠面板列表 -->
    <div class="accordion-list">
      <!--      基本信息-->
      <div
        class="accordion-item"
        :class="{ 'accordion-item--active': pluginBasicCollapse.isOpen }"
      >
        <!-- 面板头部 -->
        <div class="accordion-header" @click="handleClickHeader(1)">
          <div class="column-header-container">
            <div
              class="accordion-icon"
              :class="{ 'accordion-icon--rotated': pluginBasicCollapse.isOpen }"
            >
              <ElIcon size="16">
                <ArrowDown />
              </ElIcon>
            </div>
            <h3 class="accordion-title">{{ pluginBasicCollapse.title }}</h3>
          </div>
          <div>
            <ElButton
              @click.stop="handleEdit(1)"
              type="primary"
              v-if="!pluginBasicCollapse.isEdit"
            >
              {{ $t('button.edit') }}
            </ElButton>
            <ElButton
              @click.stop="handleCancel(1)"
              v-if="pluginBasicCollapse.isEdit"
            >
              {{ $t('button.cancel') }}
            </ElButton>
            <ElButton
              @click.stop="handleSave(1)"
              type="primary"
              v-if="pluginBasicCollapse.isEdit"
            >
              {{ $t('button.save') }}
            </ElButton>
          </div>
        </div>

        <!-- 面板内容 -->
        <div
          class="accordion-content"
          :class="{ 'accordion-content--open': pluginBasicCollapse.isOpen }"
        >
          <div class="accordion-content-inner">
            <!--编辑基本信息-->
            <div v-show="pluginBasicCollapse.isEdit">
              <div class="plugin-tool-info-edit-container">
                <ElForm
                  ref="saveForm"
                  :model="pluginToolInfo"
                  label-width="80px"
                  status-icon
                  :rules="rules"
                >
                  <ElFormItem :label="$t('pluginItem.name')" prop="name">
                    <ElInput v-model.trim="pluginToolInfo.name" />
                  </ElFormItem>
                  <ElFormItem
                    :label="$t('pluginItem.englishName')"
                    prop="englishName"
                  >
                    <ElInput v-model.trim="pluginToolInfo.englishName" />
                  </ElFormItem>
                  <ElFormItem
                    :label="$t('pluginItem.pluginToolEdit.toolPath')"
                    prop="basePath"
                  >
                    <ElInput v-model.trim="pluginToolInfo.basePath">
                      <template #prepend>{{ pluginInfo.baseUrl }}</template>
                    </ElInput>
                  </ElFormItem>
                  <ElFormItem
                    :label="$t('pluginItem.description')"
                    prop="description"
                  >
                    <ElInput
                      v-model.trim="pluginToolInfo.description"
                      type="textarea"
                      :rows="4"
                    />
                  </ElFormItem>
                  <ElFormItem
                    :label="$t('pluginItem.pluginToolEdit.requestMethod')"
                    prop="requestMethod"
                  >
                    <ElSelect
                      v-model="pluginToolInfo.requestMethod"
                      :placeholder="$t('ui.placeholder.select')"
                    >
                      <ElOption
                        v-for="option in requestMethodOptions"
                        :key="option.value"
                        :label="option.label"
                        :value="option.value"
                      />
                    </ElSelect>
                  </ElFormItem>
                </ElForm>
              </div>
            </div>
            <!--显示基本信息-->
            <div
              v-show="!pluginBasicCollapse.isEdit"
              class="plugin-tool-info-view-container"
            >
              <div class="plugin-tool-view-item">
                <div class="view-item-title">{{ $t('pluginItem.name') }}:</div>
                <div>{{ pluginToolInfo.name }}</div>
              </div>
              <div class="plugin-tool-view-item">
                <div class="view-item-title">
                  {{ $t('pluginItem.englishName') }}:
                </div>
                <div>{{ pluginToolInfo.englishName }}</div>
              </div>
              <div class="plugin-tool-view-item">
                <div class="view-item-title">
                  {{ $t('pluginItem.description') }}:
                </div>
                <div>{{ pluginToolInfo.description }}</div>
              </div>
              <div class="plugin-tool-view-item">
                <div class="view-item-title">
                  {{ $t('pluginItem.pluginToolEdit.toolPath') }}:
                </div>
                <div>{{ pluginInfo.baseUrl }}{{ pluginToolInfo.basePath }}</div>
              </div>
              <div class="plugin-tool-view-item">
                <div class="view-item-title">
                  {{ $t('pluginItem.pluginToolEdit.requestMethod') }}:
                </div>
                <div>
                  {{ pluginToolInfo.requestMethod }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!--      输入参数-->
      <div
        class="accordion-item"
        :class="{
          'accordion-item--active': pluginBasicCollapseInputParams.isOpen,
        }"
      >
        <!-- 面板头部 -->
        <div class="accordion-header" @click="handleClickHeader(2)">
          <div class="column-header-container">
            <div
              class="accordion-icon"
              :class="{
                'accordion-icon--rotated':
                  pluginBasicCollapseInputParams.isOpen,
              }"
            >
              <ElIcon size="16">
                <ArrowDown />
              </ElIcon>
            </div>
            <h3 class="accordion-title">
              {{ pluginBasicCollapseInputParams.title }}
            </h3>
          </div>
          <div>
            <ElButton
              @click.stop="handleEdit(2)"
              type="primary"
              v-if="!pluginBasicCollapseInputParams.isEdit"
            >
              {{ $t('button.edit') }}
            </ElButton>
            <ElButton
              @click.stop="handleCancel(2)"
              v-if="pluginBasicCollapseInputParams.isEdit"
            >
              {{ $t('button.cancel') }}
            </ElButton>
            <ElButton
              @click.stop="handleSave(2)"
              type="primary"
              v-if="pluginBasicCollapseInputParams.isEdit"
            >
              {{ $t('button.save') }}
            </ElButton>
          </div>
        </div>

        <!--输入参数-->
        <div
          class="accordion-content"
          :class="{
            'accordion-content--open': pluginBasicCollapseInputParams.isOpen,
          }"
        >
          <div class="accordion-content-inner">
            <PluginInputAndOutParams
              ref="pluginInputParamsRef"
              v-model="pluginInputData"
              :editable="pluginInputParamsEditable"
              :is-edit-output="false"
            />
          </div>
        </div>
      </div>
      <!--      输出参数-->
      <div
        class="accordion-item"
        :class="{
          'accordion-item--active': pluginBasicCollapseOutputParams.isOpen,
        }"
      >
        <!-- 面板头部 -->
        <div class="accordion-header" @click="handleClickHeader(3)">
          <div class="column-header-container">
            <div
              class="accordion-icon"
              :class="{
                'accordion-icon--rotated':
                  pluginBasicCollapseOutputParams.isOpen,
              }"
            >
              <ElIcon size="16">
                <ArrowDown />
              </ElIcon>
            </div>
            <h3 class="accordion-title">
              {{ pluginBasicCollapseOutputParams.title }}
            </h3>
          </div>
          <div>
            <ElButton
              @click.stop="handleEdit(3)"
              type="primary"
              v-if="!pluginBasicCollapseOutputParams.isEdit"
            >
              {{ $t('button.edit') }}
            </ElButton>
            <ElButton
              @click.stop="handleCancel(3)"
              v-if="pluginBasicCollapseOutputParams.isEdit"
            >
              {{ $t('button.cancel') }}
            </ElButton>
            <ElButton
              @click.stop="handleSave(3)"
              type="primary"
              v-if="pluginBasicCollapseOutputParams.isEdit"
            >
              {{ $t('button.save') }}
            </ElButton>
          </div>
        </div>

        <!--输出参数-->
        <div
          class="accordion-content"
          :class="{
            'accordion-content--open': pluginBasicCollapseOutputParams.isOpen,
          }"
        >
          <div class="accordion-content-inner">
            <PluginInputAndOutParams
              v-model="pluginOutputData"
              ref="pluginOutputParamsRef"
              :editable="pluginOutputParamsEditable"
              :is-edit-output="true"
            />
          </div>
        </div>
      </div>
    </div>
    <!--    试运行模态框-->
    <PluginRunTestModal ref="runTestRef" :plugin-tool-id="toolId" />
  </div>
</template>

<style scoped>
/* 响应式设计 */
@media (max-width: 768px) {
  .accordion-container {
    padding: 15px;
  }

  .controls {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .control-group {
    justify-content: center;
  }

  .title {
    font-size: 1.5rem;
  }

  .accordion-header {
    padding: 14px 16px;
  }

  .accordion-title {
    font-size: 1rem;
  }
}

.accordion-container {
  max-width: 100%;
  padding: 20px;
  margin: 0 auto;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.controls-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title {
  margin-bottom: 8px;
  font-size: 2rem;
  font-weight: 600;
  color: var(--el-text-color-secondary);
  text-align: center;
}

.subtitle {
  margin-bottom: 30px;
  font-size: 1.1rem;
  color: var(--el-text-color-secondary);
  text-align: center;
}

/* 控制面板样式 */
.controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  margin-bottom: 30px;
  background: var(--el-bg-color);
  border: 1px solid #e9ecef;
  border-radius: 8px;
}

.control-group {
  display: flex;
  gap: 15px;
  align-items: center;
}

.checkbox-label {
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  cursor: pointer;
}

.checkbox {
  width: 16px;
  height: 16px;
}

.control-btn {
  padding: 8px 16px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  cursor: pointer;
  background: var(--el-bg-color);
  border-radius: 4px;
  transition: all 0.3s ease;
}

.control-btn:hover {
  background: #3498db;
  background: var(--el-color-primary-light-9);
}

/* 折叠面板列表 */
.accordion-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 20px;
}

.accordion-item {
  overflow: hidden;
  background: hsl(var(--background));
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
  transition: all 0.3s ease;
}

.accordion-item:hover {
  box-shadow: 0 4px 12px rgb(0 0 0 / 10%);
}

.accordion-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  cursor: pointer;
  user-select: none;
  background: hsl(var(--background));
  transition: background-color 0.3s ease;
}

.accordion-title {
  padding-left: 12px;
  margin: 0;
  font-size: 1.1rem;
  font-weight: 500;
}

.accordion-icon {
  font-size: 12px;
  color: #7f8c8d;
  transition: transform 0.3s ease;
}

.accordion-icon--rotated {
  transform: rotate(180deg);
}

.accordion-content {
  max-height: 0;
  overflow: hidden;
  background: hsl(var(--background));
  transition: max-height 0.4s ease;
}

.accordion-content--open {
  max-height: 2000px;
}

.accordion-content-inner {
  padding: 20px;
  border-top: 1px solid hsl(var(--border));
}

.accordion-content-inner p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: var(--el-text-color-secondary);
}

.column-header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.plugin-tool-info-view-container {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.plugin-tool-view-item {
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 14px;
}

.view-item-title {
  width: 70px;

  /* text-align: right; */

  /* margin-right: 12px; */
}
</style>
