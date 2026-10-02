<template>
  <BasicDrawer v-bind="$attrs" @register="registerDrawer" title="设备状态操作记录" width="820px">
    <a-alert
      type="info"
      show-icon
      :message="`设备：${deviceName}（状态变化全部留痕，用于追溯）`"
      style="margin-bottom: 12px"
    />
    <BasicTable
      @register="registerTable"
      :canResize="false"
      row-key="id"
      :pagination="{ pageSize: 10 }"
    />
  </BasicDrawer>
</template>
<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { BasicTable, useTable } from '/@/components/Table';
  import { render } from '/@/utils/common/renderUtils';
  import { getStatusRecordList } from '/@/api/device/device.api';

  const deviceName = ref('');
  const deviceId = ref('');

  const columns = [
    {
      title: '操作时间',
      dataIndex: 'operateTime',
      width: 160,
    },
    {
      title: '操作类型',
      dataIndex: 'operateType',
      width: 90,
      customRender: ({ text }) => (text === 'CREATE' ? '新建入库' : '状态变更'),
    },
    {
      title: '变更前状态',
      dataIndex: 'beforeStatus',
      width: 100,
      customRender: ({ record }) =>
        record.beforeStatus ? render.renderDict(record.beforeStatus, 'device_status', true) : '—',
    },
    {
      title: '变更后状态',
      dataIndex: 'afterStatus',
      width: 100,
      customRender: ({ record }) => render.renderDict(record.afterStatus, 'device_status', true),
    },
    {
      title: '操作人',
      dataIndex: 'createBy',
      width: 100,
    },
    {
      title: '变更说明',
      dataIndex: 'remark',
    },
  ];

  // 抽屉内容随主页面一起挂载，id 在打开时才确定，首帧未就绪时拦截请求
  const [registerDrawer] = useDrawerInner(async (data) => {
    deviceName.value = data?.record?.deviceName || '';
    deviceId.value = data?.record?.id || '';
    await reload({ page: 1 });
  });

  const [registerTable, { reload }] = useTable({
    api: getStatusRecordList,
    columns,
    bordered: true,
    showIndexColumn: false,
    immediate: false,
    pagination: { pageSize: 10 },
    beforeFetch: (params) => {
      if (!deviceId.value) {
        return false;
      }
      return { ...params, deviceId: deviceId.value };
    },
  });
</script>
