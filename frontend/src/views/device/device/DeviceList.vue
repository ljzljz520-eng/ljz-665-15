<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleAdd">新建设备</a-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                批量删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button>批量操作 <Icon icon="ant-design:down-outlined" /></a-button>
        </a-dropdown>
      </template>

      <!-- 状态列 -->
      <template #status="{ record }">
        <a-tag :color="STATUS_COLOR_MAP[record.status] || 'default'">
          {{ statusText(record.status) }}
        </a-tag>
      </template>

      <template #action="{ record }">
        <TableAction :actions="getActions(record)" />
      </template>
    </BasicTable>

    <DeviceModal @register="registerDeviceModal" @success="reload" />
    <StatusChangeModal @register="registerStatusModal" @success="reload" />
    <StatusRecordDrawer @register="registerRecordDrawer" />
  </div>
</template>

<script lang="ts" setup name="device-device-list">
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useDrawer } from '/@/components/Drawer';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useListPage } from '/@/hooks/system/useListPage';
  import {
    getDeviceList,
    deleteDevice,
    batchDeleteDevice,
    DEVICE_STATUS_TRANSITIONS,
  } from './device.api';
  import { columns, searchFormSchema, STATUS_COLOR_MAP } from './device.data';
  import DeviceModal from './components/DeviceModal.vue';
  import StatusChangeModal from './components/StatusChangeModal.vue';
  import StatusRecordDrawer from './components/StatusRecordDrawer.vue';

  const { createConfirm } = useMessage();
  const [registerDeviceModal, { openModal: openDeviceModal }] = useModal();
  const [registerStatusModal, { openModal: openStatusModal }] = useModal();
  const [registerRecordDrawer, { openDrawer: openRecordDrawer }] = useDrawer();

  const { tableContext } = useListPage({
    designScope: 'device-device-list',
    tableProps: {
      title: '设备台账',
      api: getDeviceList,
      columns,
      formConfig: { schemas: searchFormSchema },
      actionColumn: { width: 220 },
      showIndexColumn: true,
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  const STATUS_NAME: Record<string, string> = {
    '1': '在用',
    '2': '维修中',
    '3': '停用',
    '4': '报废',
  };

  function statusText(code: string) {
    return STATUS_NAME[code] || code;
  }

  /**
   * 是否允许状态变更（报废为终态）
   */
  function canChangeStatus(record) {
    return (DEVICE_STATUS_TRANSITIONS[record.status] || []).length > 0;
  }

  function getActions(record) {
    const actions: any[] = [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
      {
        label: '流转记录',
        onClick: handleViewRecord.bind(null, record),
      },
    ];
    if (canChangeStatus(record)) {
      actions.push({
        label: '状态变更',
        onClick: handleChangeStatus.bind(null, record),
      });
    }
    actions.push({
      label: '删除',
      color: 'error',
      popConfirm: {
        title: '是否确认删除该设备？',
        confirm: handleDelete.bind(null, record),
      },
    });
    return actions;
  }

  function handleAdd() {
    openDeviceModal(true, { isUpdate: false });
  }

  function handleEdit(record) {
    openDeviceModal(true, { record, isUpdate: true });
  }

  function handleChangeStatus(record) {
    openStatusModal(true, { record });
  }

  function handleViewRecord(record) {
    openRecordDrawer(true, { record });
  }

  async function handleDelete(record) {
    await deleteDevice({ id: record.id }, reload);
  }

  function batchHandleDelete() {
    createConfirm({
      iconType: 'warning',
      title: '确认删除',
      content: `是否删除选中的 ${selectedRowKeys.value.length} 条设备数据？`,
      onOk: async () => {
        await batchDeleteDevice({ ids: selectedRowKeys.value }, () => {
          selectedRowKeys.value = [];
          reload();
        });
      },
    });
  }
</script>
