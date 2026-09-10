<template>
  <BasicDrawer @register="registerDrawer" :title="drawerTitle" width="860" destroyOnClose>
    <BasicTable
      @register="registerTable"
      :canResize="false"
      :pagination="{ pageSize: 10 }"
      :showIndexColumn="false"
    >
      <template #fromStatus="{ record }">
        <a-tag v-if="record.fromStatus" :color="STATUS_COLOR_MAP[record.fromStatus] || 'default'">
          {{ statusText(record.fromStatus) }}
        </a-tag>
        <span v-else style="color: #999">—（初始）</span>
      </template>
      <template #toStatus="{ record }">
        <a-tag :color="STATUS_COLOR_MAP[record.toStatus] || 'default'">
          {{ statusText(record.toStatus) }}
        </a-tag>
      </template>
    </BasicTable>
  </BasicDrawer>
</template>

<script lang="ts" setup name="device-status-record-drawer">
  import { ref } from 'vue';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { BasicTable, useTable } from '/@/components/Table';
  import { getDeviceStatusRecordList, DEVICE_STATUS_OPTIONS } from '../device.api';
  import { recordColumns, STATUS_COLOR_MAP } from '../device.data';

  const deviceId = ref('');
  const drawerTitle = ref('设备状态流转记录');

  const [registerDrawer] = useDrawerInner((data) => {
    deviceId.value = data?.record?.id;
    drawerTitle.value = `设备状态流转记录 - ${data?.record?.deviceName ?? ''}`;
    reload();
  });

  const [registerTable, { reload }] = useTable({
    api: getDeviceStatusRecordList,
    columns: recordColumns,
    fetchSetting: {
      pageField: 'pageNo',
      sizeField: 'pageSize',
      listField: 'records',
      totalField: 'total',
    },
    searchInfo: {},
    beforeFetch: (params) => {
      return Object.assign(params, { deviceId: deviceId.value });
    },
  });

  function statusText(code: string) {
    return DEVICE_STATUS_OPTIONS.find((i) => i.value === code)?.label || code;
  }
</script>
