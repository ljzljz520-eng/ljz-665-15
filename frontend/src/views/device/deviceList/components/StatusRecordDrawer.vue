<template>
  <a-drawer
    :title="`状态流转记录 - ${record.deviceName || ''}（${record.deviceCode || ''}）`"
    placement="right"
    :width="720"
    :open="visible"
    @close="handleClose"
  >
    <a-spin :spinning="loading">
      <a-empty v-if="!loading && records.length === 0" description="暂无操作记录" />
      <a-timeline v-else>
        <a-timeline-item v-for="(item, index) in records" :key="item.id" :color="index === 0 ? 'green' : 'gray'">
          <div class="record-head">
            <a-tag :color="deviceStatusColor(item.beforeStatus)">{{ item.beforeStatus ? deviceStatusText(item.beforeStatus) : '—' }}</a-tag>
            <Icon icon="ant-design:right-outlined" class="arrow-icon" />
            <a-tag :color="deviceStatusColor(item.afterStatus)">{{ deviceStatusText(item.afterStatus) }}</a-tag>
            <span v-if="index === 0" class="latest">最新</span>
          </div>
          <div class="record-meta">
            操作人：{{ item.createBy || '—' }} ｜ 操作时间：{{ item.createTime || '—' }}
          </div>
          <div class="record-remark" v-if="item.remark">说明：{{ item.remark }}</div>
        </a-timeline-item>
      </a-timeline>
    </a-spin>
  </a-drawer>
</template>

<script lang="ts" name="StatusRecordDrawer" setup>
  import { ref } from 'vue';
  import Icon from '/@/components/Icon/index';
  import { getDeviceStatusRecord } from '/@/api/device/device.api';
  import { deviceStatusText, deviceStatusColor } from '/@/api/device/device.data';

  const visible = ref(false);
  const loading = ref(false);
  const records = ref<any[]>([]);
  const record = ref<Recordable>({});

  function open(row) {
    record.value = row || {};
    visible.value = true;
    loading.value = true;
    records.value = [];
    getDeviceStatusRecord(row.id)
      .then((res) => {
        records.value = res || [];
      })
      .finally(() => {
        loading.value = false;
      });
  }

  function handleClose() {
    visible.value = false;
  }

  defineExpose({ open });
</script>

<style lang="less" scoped>
  .record-head {
    display: flex;
    align-items: center;
    gap: 6px;
    .arrow-icon {
      color: rgba(0, 0, 0, 0.45);
    }
    .latest {
      margin-left: 8px;
      font-size: 12px;
      color: #52c41a;
    }
  }
  .record-meta {
    margin-top: 6px;
    font-size: 12px;
    color: rgba(0, 0, 0, 0.45);
  }
  .record-remark {
    margin-top: 4px;
    font-size: 13px;
    color: rgba(0, 0, 0, 0.75);
    word-break: break-all;
  }
</style>
