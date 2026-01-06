<script setup lang="ts">
import type { CronItemState } from './CronTabPane.vue';

import { computed, reactive, ref } from 'vue';

import {
  ElButton,
  ElCard,
  ElDivider,
  ElInput,
  ElTable,
  ElTableColumn,
  ElTabPane,
  ElTabs,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';

import CrontabPane from './CronTabPane.vue';

const emit = defineEmits(['useCron']);
const activeTab = ref('second');

// 默认状态工厂函数
const defaultState = (min: number, _: number): CronItemState => ({
  type: 'every',
  rangeStart: min,
  rangeEnd: min + 1,
  loopStart: min,
  loopStep: 1,
  specificList: [],
});

const state = reactive({
  second: defaultState(0, 59),
  minute: defaultState(0, 59),
  hour: defaultState(0, 23),
  day: { ...defaultState(1, 31), type: 'every' } as CronItemState,
  month: defaultState(1, 12),
  week: { ...defaultState(1, 7), type: 'none' } as CronItemState, // 默认为?
});

const weekAlias: Record<number, string> = {
  1: $t('common.Sun'),
  2: $t('common.Mon'),
  3: $t('common.Tue'),
  4: $t('common.Wed'),
  5: $t('common.Thu'),
  6: $t('common.Fri'),
  7: $t('common.Sat'),
};

// 核心：格式化单个字段
const formatItem = (item: CronItemState): string => {
  switch (item.type) {
    case 'every': {
      return '*';
    }
    case 'loop': {
      return `${item.loopStart}/${item.loopStep}`;
    }
    case 'none': {
      return '?';
    }
    case 'range': {
      return `${item.rangeStart}-${item.rangeEnd}`;
    }
    case 'specific': {
      if (item.specificList.length === 0) return '*';
      return item.specificList.sort((a, b) => a - b).join(',');
    }
    default: {
      return '*';
    }
  }
};

// 互斥逻辑
const handleDayChange = () => {
  if (state.day.type !== 'none') {
    state.week.type = 'none';
  }
};

const handleWeekChange = () => {
  if (state.week.type !== 'none') {
    state.day.type = 'none';
  }
};

// 计算最终 Cron 字符串
const cronResult = computed(() => {
  const s = formatItem(state.second);
  const m = formatItem(state.minute);
  const h = formatItem(state.hour);
  const d = formatItem(state.day);
  const M = formatItem(state.month);
  const w = formatItem(state.week);
  return `${s} ${m} ${h} ${d} ${M} ${w}`;
});

// 表格展示数据
const resultTableData = computed(() => [
  {
    second: formatItem(state.second),
    minute: formatItem(state.minute),
    hour: formatItem(state.hour),
    day: formatItem(state.day),
    month: formatItem(state.month),
    week: formatItem(state.week),
  },
]);

const copyCron = () => {
  // if (navigator.clipboard) {
  //   navigator.clipboard.writeText(cronResult.value).then(() => {
  //     ElMessage.success('Cron 表达式已复制');
  //   });
  // } else {
  //   ElMessage.warning('浏览器不支持剪贴板 API');
  // }
  emit('useCron', cronResult.value);
};
const nextTimes = ref<any[]>([]);
function getNextTimes() {
  api
    .get('/api/v1/sysJob/getNextTimes', {
      params: {
        cronExpression: cronResult.value,
      },
    })
    .then((res: any) => {
      nextTimes.value = res.errorCode === 0 ? res.data : [];
    });
}
</script>

<template>
  <div class="cron-generator">
    <ElCard class="box-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>{{ $t('cron.cronExpressionGenerator') }}</span>
        </div>
      </template>

      <ElTabs v-model="activeTab" type="border-card">
        <!-- 秒 -->
        <ElTabPane :label="$t('common.Second')" name="second">
          <CrontabPane
            v-model="state.second"
            :min="0"
            :max="59"
            :label="$t('common.Second')"
          />
        </ElTabPane>

        <!-- 分 -->
        <ElTabPane :label="$t('common.Min')" name="minute">
          <CrontabPane
            v-model="state.minute"
            :min="0"
            :max="59"
            :label="$t('common.Min')"
          />
        </ElTabPane>

        <!-- 时 -->
        <ElTabPane :label="$t('common.Hour')" name="hour">
          <CrontabPane
            v-model="state.hour"
            :min="0"
            :max="23"
            :label="$t('common.Hour')"
          />
        </ElTabPane>

        <!-- 日 -->
        <ElTabPane :label="$t('common.Day')" name="day">
          <CrontabPane
            v-model="state.day"
            :min="1"
            :max="31"
            :label="$t('common.Day')"
            week-mode-check
            @change="handleDayChange"
          />
        </ElTabPane>

        <!-- 月 -->
        <ElTabPane :label="$t('common.Month')" name="month">
          <CrontabPane
            v-model="state.month"
            :min="1"
            :max="12"
            :label="$t('common.Month')"
          />
        </ElTabPane>

        <!-- 周 -->
        <ElTabPane :label="$t('common.Week')" name="week">
          <CrontabPane
            v-model="state.week"
            :min="1"
            :max="7"
            :label="$t('common.Week')"
            :alias-map="weekAlias"
            day-mode-check
            @change="handleWeekChange"
          />
        </ElTabPane>
      </ElTabs>

      <!-- 结果展示区域 -->
      <div class="result-area">
        <ElDivider content-position="left">
          {{ $t('cron.GenerateResult') }}
        </ElDivider>
        <div class="result-row">
          <ElInput
            v-model="cronResult"
            readonly
            :placeholder="$t('cron.CronExpression')"
          >
            <template #prepend>{{ $t('cron.CronExpression') }}</template>
          </ElInput>
          <ElButton type="primary" @click="copyCron" style="margin-left: 10px">
            {{ $t('cron.UseThisValue') }}
          </ElButton>
          <ElButton
            type="primary"
            @click="getNextTimes"
            style="margin-left: 10px"
          >
            {{ $t('cron.CheckLast5ExecutionTimes') }}
          </ElButton>
        </div>

        <div class="preview-table">
          <ElTable
            :data="resultTableData"
            border
            style="width: 100%"
            size="small"
          >
            <ElTableColumn prop="second" :label="$t('common.Second')" />
            <ElTableColumn prop="minute" :label="$t('common.Min')" />
            <ElTableColumn prop="hour" :label="$t('common.Hour')" />
            <ElTableColumn prop="day" :label="$t('common.Day')" />
            <ElTableColumn prop="month" :label="$t('common.Month')" />
            <ElTableColumn prop="week" :label="$t('common.Week')" />
          </ElTable>
        </div>
        <ElDivider content-position="left">
          {{ $t('cron.Last5ExecutionTimes') }}
        </ElDivider>
        <div v-for="(item, idx) in nextTimes" :key="idx">
          {{ item }}
        </div>
      </div>
    </ElCard>
  </div>
</template>

<style scoped>
.cron-generator {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.result-area {
  margin-top: 20px;
}

.result-row {
  display: flex;
  margin-bottom: 15px;
}
</style>
