<script setup lang="ts">
import { ref, watch } from 'vue';

import { Delete, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElInput,
  ElMessage,
  ElOption,
  ElSelect,
  ElSwitch,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { $t } from '#/locales';

export interface TreeTableNode {
  key: string;
  name: string;
  description: string;
  method?: 'Body' | 'Header' | 'Path' | 'Query';
  required?: boolean;
  defaultValue?: string;
  enabled?: boolean;
  type?: string;
  children?: TreeTableNode[];
}

interface Props {
  modelValue?: TreeTableNode[];
  editable?: boolean;
  isEditOutput?: boolean;
}

interface Emits {
  (e: 'update:modelValue', value: TreeTableNode[]): void;
  (e: 'submit', value: TreeTableNode[]): void;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  editable: false,
  isEditOutput: false,
});

const emit = defineEmits<Emits>();

const data = ref<TreeTableNode[]>([]);
const expandedKeys = ref<string[]>(['1']);
const errors = ref<
  Record<string, Partial<Record<keyof TreeTableNode, string>>>
>({});

watch(
  () => props.modelValue,
  (newVal) => {
    if (newVal) {
      data.value = newVal;
    }
  },
  { immediate: true, deep: true },
);

// 计算缩进宽度
const getIndentWidth = (record: TreeTableNode): number => {
  const level = String(record.key).split('-').length - 1;
  const indentSize = 20;
  return level > 0 ? level * indentSize : 0;
};

// 获取类型选项
const getTypeOptions = (record: TreeTableNode) => {
  if (record.name === 'arrayItem') {
    return [
      { label: 'Array[String]', value: 'Array[String]' },
      { label: 'Array[Number]', value: 'Array[Number]' },
      { label: 'Array[Boolean]', value: 'Array[Boolean]' },
      { label: 'Array[Object]', value: 'Array[Object]' },
    ];
  }
  return [
    { label: 'String', value: 'String' },
    { label: 'Boolean', value: 'Boolean' },
    { label: 'Number', value: 'Number' },
    { label: 'Object', value: 'Object' },
    { label: 'Array', value: 'Array' },
    { label: 'File', value: 'File' },
  ];
};

// 数据变化处理
const handleDataChange = () => {
  emit('update:modelValue', data.value);
};

// 类型变化处理
const handleTypeChange = (record: TreeTableNode, newType: string) => {
  const updateNode = (nodes: TreeTableNode[]): TreeTableNode[] => {
    return nodes.map((node) => {
      if (node.key === record.key) {
        // 如果是简单类型，移除 children
        if (
          [
            'Array[Boolean]',
            'Array[Integer]',
            'Array[Number]',
            'Array[Object]',
            'Array[String]',
            'Boolean',
            'Number',
            'String',
          ].includes(newType)
        ) {
          return {
            ...node,
            type: newType,
            children: undefined,
          };
        }
        // 如果是 Object 或 Array，保留或初始化 children
        return {
          ...node,
          type: newType,
          children: node.children || [],
        };
      }
      if (node.children) {
        return {
          ...node,
          children: updateNode(node.children),
        };
      }
      return node;
    });
  };

  data.value = updateNode(data.value);
  handleDataChange();

  // 如果是 Object 或 Array，添加默认子节点并展开
  if (
    newType === 'Object' ||
    newType === 'Array' ||
    newType === 'Array[Object]'
  ) {
    const newChild: TreeTableNode = {
      key: `${record.key}-${Date.now()}`,
      name: newType === 'Array' ? 'arrayItem' : '',
      description: '',
      enabled: true,
      ...(props.isEditOutput
        ? {}
        : { method: 'Query', defaultValue: '', required: false }),
      type: newType === 'Array' ? 'Array[String]' : 'String',
    };

    const addChildToNode = (nodes: TreeTableNode[]): TreeTableNode[] => {
      return nodes.map((node) => {
        if (node.key === record.key) {
          return {
            ...node,
            children: [newChild],
          };
        }
        if (node.children) {
          return {
            ...node,
            children: addChildToNode(node.children),
          };
        }
        return node;
      });
    };

    data.value = addChildToNode(data.value);
    handleDataChange();

    // 自动展开父节点
    if (!expandedKeys.value.includes(record.key)) {
      expandedKeys.value = [...expandedKeys.value, record.key];
    }
  }
};

// 展开/折叠处理
const onExpand = (_row: TreeTableNode, expandedRows: TreeTableNode[]) => {
  expandedKeys.value = expandedRows.map((item) => item.key);
};

// 添加根节点
const addNewRootNode = () => {
  if (!props.editable) return;

  const newKey = `${Date.now()}`;
  const newNode: TreeTableNode = {
    key: newKey,
    name: '',
    description: '',
    enabled: true,
    type: 'String',
    ...(props.isEditOutput
      ? {}
      : { method: 'Query', defaultValue: '', required: false }),
  };

  data.value = [...data.value, newNode];
  handleDataChange();
};

// 添加子节点
const handleAddChild = (parentKey: string) => {
  if (!props.editable || !parentKey) return;

  const newChild: TreeTableNode = {
    key: `${parentKey}-${Date.now()}`,
    name: '',
    description: '',
    required: false,
    enabled: true,
    type: 'String',
    ...(props.isEditOutput ? {} : { method: 'Query', defaultValue: '' }),
  };

  const addChildToNode = (nodes: TreeTableNode[]): TreeTableNode[] => {
    return nodes.map((node) => {
      if (node.key === parentKey) {
        return {
          ...node,
          children: [...(node.children || []), newChild],
        };
      }
      if (node.children) {
        return {
          ...node,
          children: addChildToNode(node.children),
        };
      }
      return node;
    });
  };

  data.value = addChildToNode(data.value);
  handleDataChange();

  if (!expandedKeys.value.includes(parentKey)) {
    expandedKeys.value = [...expandedKeys.value, parentKey];
  }
};

// 删除节点
const deleteNode = (key: string) => {
  if (!props.editable) return;

  const removeNodeRecursively = (nodes: TreeTableNode[]): TreeTableNode[] => {
    return nodes.filter((node) => {
      if (node.key === key) return false;
      if (node.children) {
        node.children = removeNodeRecursively(node.children);
      }
      return true;
    });
  };

  data.value = removeNodeRecursively(data.value);
  handleDataChange();
};

// 验证字段
// 验证字段
const validateFields = (): boolean => {
  const newErrors: Record<
    string,
    Partial<Record<keyof TreeTableNode, string>>
  > = {};
  let isValid = true;

  // 递归校验节点（包括子节点）
  const checkNode = (node: TreeTableNode): boolean => {
    const { name, description, method, type } = node;
    const nodeErrors: Partial<Record<keyof TreeTableNode, string>> = {};
    let nodeIsValid = true;

    // 校验参数名称
    if (!name?.trim()) {
      nodeErrors.name = $t('message.cannotBeEmpty.name');
      nodeIsValid = false;
      isValid = false;
    }

    // 校验参数描述
    if (!description?.trim()) {
      nodeErrors.description = $t('message.cannotBeEmpty.description');
      nodeIsValid = false;
      isValid = false;
    }

    // 校验传入方法（仅根节点+输入参数）
    if (isRootNode(node) && !method && !props.isEditOutput) {
      nodeErrors.method = $t('message.cannotBeEmpty.method');
      nodeIsValid = false;
      isValid = false;
    }

    // 校验参数类型
    if (!type) {
      nodeErrors.type = $t('message.cannotBeEmpty.type');
      nodeIsValid = false;
      isValid = false;
    }

    // 记录当前节点的错误
    if (Object.keys(nodeErrors).length > 0) {
      newErrors[node.key] = nodeErrors;
    }

    // 递归校验子节点
    if (node.children) {
      node.children.forEach((child) => {
        if (!checkNode(child)) {
          nodeIsValid = false;
          isValid = false;
        }
      });
    }

    return nodeIsValid;
  };

  // 校验所有根节点
  data.value.forEach((node) => {
    checkNode(node);
  });

  // 更新错误信息
  errors.value = newErrors;
  return isValid;
};

// 判断是否为根节点
const isRootNode = (record: TreeTableNode): boolean => {
  return !record.key.includes('-');
};

const handleSubmitParams = () => {
  // 全量校验所有字段
  const isFormValid = validateFields();

  if (!isFormValid) {
    ElMessage.error($t('message.cannotBeEmpty.all'));

    // 找到第一个错误的输入框/选择器
    const firstErrorInput = document.querySelector('.error-border');
    if (firstErrorInput) {
      // 滚动到错误元素位置
      firstErrorInput.scrollIntoView({ behavior: 'smooth', block: 'center' });
      // 给输入框添加焦点
      if ((firstErrorInput as HTMLInputElement).focus) {
        (firstErrorInput as HTMLInputElement).focus();
      } else {
        // 处理选择器的焦点
        const selectInput = firstErrorInput.querySelector('.el-input__inner');
        if (selectInput) (selectInput as HTMLInputElement).focus();
      }
    }
    throw new Error($t('message.cannotBeEmpty.error'));
  }

  // 校验通过，提交数据
  emit('submit', data.value);
};

// 暴露方法给父组件
defineExpose({
  handleSubmitParams,
});
// 输入框失焦时清除对应字段的错误提示
const handleInputBlur = (row: TreeTableNode, field: keyof TreeTableNode) => {
  if (
    errors.value &&
    row &&
    field &&
    (errors.value[row.key] as Record<string, unknown>)
  ) {
    delete (errors.value[row.key] as Record<string, unknown>)[field];
  }
};
</script>

<template>
  <div class="tree-table-container">
    <ElTable
      :data="data"
      row-key="key"
      :border="true"
      size="default"
      :expand-row-keys="expandedKeys"
      @expand-change="onExpand"
      style="width: 100%; overflow-x: auto"
    >
      <!-- 参数名称列 -->
      <ElTableColumn prop="name" class-name="first-column">
        <template #header>
          <div class="header-with-asterisk">
            {{ $t('pluginItem.parameterName') }}
            <span class="required-asterisk">*</span>
          </div>
        </template>
        <template #default="{ row }">
          <div class="name-cell">
            <div
              v-if="!props.editable"
              :style="{ paddingLeft: `${getIndentWidth(row)}px` }"
            >
              {{ row.name || '' }}
            </div>
            <div v-else>
              <div class="name-input-wrapper">
                <div :style="{ width: `${getIndentWidth(row)}px` }"></div>
                <ElInput
                  v-model="row.name"
                  :disabled="row.name === 'arrayItem'"
                  @input="handleDataChange"
                  @blur="handleInputBlur(row, 'name')"
                  :class="{ 'error-border': errors[row.key]?.name }"
                />
                <div v-if="errors[row.key]?.name" class="error-message">
                  {{ errors[row.key]?.name }}
                </div>
              </div>
            </div>
          </div>
        </template>
      </ElTableColumn>

      <!-- 参数描述列 -->
      <ElTableColumn prop="description">
        <template #header>
          <div class="header-with-asterisk">
            {{ $t('pluginItem.parameterDescription') }}
            <span class="required-asterisk">*</span>
          </div>
        </template>
        <template #default="{ row }">
          <div class="description-cell">
            <span v-if="!props.editable">{{ row.description || '' }}</span>
            <div v-else>
              <ElInput
                v-model="row.description"
                @input="handleDataChange"
                @blur="handleInputBlur(row, 'description')"
                :class="{ 'error-border': errors[row.key]?.description }"
              />
              <div v-if="errors[row.key]?.description" class="error-message">
                {{ errors[row.key]?.description }}
              </div>
            </div>
          </div>
        </template>
      </ElTableColumn>

      <!-- 参数类型列 -->
      <ElTableColumn
        prop="type"
        :label="$t('pluginItem.parameterType')"
        width="150px"
      >
        <template #default="{ row }">
          <span v-if="!props.editable">{{ row.type || '' }}</span>
          <div v-else>
            <ElSelect
              v-model="row.type"
              @change="handleTypeChange(row, $event)"
            >
              <ElOption
                v-for="option in getTypeOptions(row)"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </ElSelect>
            <div v-if="errors[row.key]?.type" class="error-message">
              {{ errors[row.key]?.type }}
            </div>
          </div>
        </template>
      </ElTableColumn>

      <!-- 传入方法列 (仅输入参数显示) -->
      <ElTableColumn
        v-if="!props.isEditOutput"
        prop="method"
        :label="$t('pluginItem.inputMethod')"
        width="120px"
      >
        <template #default="{ row }">
          <span v-if="row.name === 'arrayItem'"></span>
          <span v-else-if="!props.editable">{{ row.method || '' }}</span>
          <div v-else>
            <ElSelect v-model="row.method" @change="handleDataChange">
              <ElOption label="Query" value="Query" />
              <ElOption label="Body" value="Body" />
              <ElOption label="Path" value="Path" />
              <ElOption label="Header" value="Header" />
            </ElSelect>
            <div v-if="errors[row.key]?.method" class="error-message">
              {{ errors[row.key]?.method }}
            </div>
          </div>
        </template>
      </ElTableColumn>

      <!-- 是否必填列 (仅输入参数显示) -->
      <ElTableColumn
        v-if="!props.isEditOutput"
        prop="required"
        :label="$t('pluginItem.required')"
        width="120px"
      >
        <template #default="{ row }">
          <ElSwitch
            v-model="row.required"
            @change="handleDataChange"
            :disabled="!props.editable"
          />
        </template>
      </ElTableColumn>

      <!-- 默认值列 (仅输入参数显示) -->
      <ElTableColumn
        v-if="!props.isEditOutput"
        prop="defaultValue"
        :label="$t('pluginItem.defaultValue')"
        width="150px"
      >
        <template #default="{ row }">
          <span v-if="row.type === 'Object'"></span>
          <span v-else-if="!props.editable">{{ row.defaultValue || '' }}</span>
          <ElInput
            v-else
            v-model="row.defaultValue"
            @input="handleDataChange"
            :disabled="!props.editable"
          />
        </template>
      </ElTableColumn>

      <!-- 启用状态列 -->
      <ElTableColumn
        prop="enabled"
        :label="$t('pluginItem.enabledStatus')"
        width="120px"
      >
        <template #default="{ row }">
          <ElSwitch
            v-model="row.enabled"
            @change="handleDataChange"
            :disabled="!props.editable"
          />
        </template>
      </ElTableColumn>

      <!-- 操作列 (仅可编辑时显示) -->
      <ElTableColumn
        v-if="props.editable"
        :label="$t('common.handle')"
        width="130px"
      >
        <template #default="{ row }">
          <div class="action-buttons">
            <ElButton
              v-if="row.type === 'Object' || row.type === 'Array[Object]'"
              type="primary"
              link
              :icon="Plus"
              @click="handleAddChild(row.key)"
              :title="$t('pluginItem.addChildNode')"
            />
            <ElButton
              type="danger"
              link
              :icon="Delete"
              @click="deleteNode(row.key)"
            >
              {{ $t('button.delete') }}
            </ElButton>
          </div>
        </template>
      </ElTableColumn>
    </ElTable>

    <!-- 新增参数按钮 -->
    <div v-if="props.editable" class="add-button-container">
      <ElButton type="default" @click="addNewRootNode" :icon="Plus">
        {{ $t('pluginItem.addParameter') }}
      </ElButton>
    </div>
  </div>
</template>

<style scoped>
.tree-table-container {
  width: 100%;
  overflow-x: auto;
}

.name-cell {
  position: relative;
  min-width: 100%;
}

.editable-name {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.name-input-wrapper {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  width: 100%;
}

.error-message {
  margin-top: 2px;
  font-size: 12px;
  line-height: 1.2;
  color: #ff4d4f;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: center;
}

.action-buttons .el-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  padding: 0;
}

.add-button-container {
  margin-top: 16px;
  text-align: left;
}

.description-cell {
  position: relative;
  width: 100%;
}

:deep(.el-table td.el-table__cell.first-column > div) {
  display: flex;
  gap: 2px;
  align-items: center;
}

.el-table__header-wrapper,
.el-table__body-wrapper {
  min-width: 100%;
}

.header-with-asterisk {
  position: relative;
  display: inline-flex;
  align-items: center;
}

.required-asterisk {
  position: absolute;
  right: -8px;
  font-size: 12px;
  font-weight: bold;
  line-height: 1;
  color: #ff4d4f;
}

/* 输入框/选择器错误样式 */
:deep(.el-input__inner.error-border),
:deep(.el-select .el-input__inner.error-border) {
  border-color: #ff4d4f !important;
  box-shadow: 0 0 0 2px rgb(255 77 79 / 20%) !important;
}

/* 下拉选择器的触发框错误样式 */
:deep(.el-select__wrapper.error-border) {
  border-color: #ff4d4f !important;
  box-shadow: 0 0 0 2px rgb(255 77 79 / 20%) !important;
}

.name-input-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
}
</style>
