<template>
  <div>
    <BasicTable
      @register="registerTable"
      :rowSelection="rowSelection"
      :canResize="false"
    >
      <template #tableTitle>
        <a-button preIcon="ant-design:plus-outlined" type="primary" @click="handleAdd" v-auth="'device:device:add'">新增</a-button>
        <a-button
          preIcon="ant-design:export-outlined"
          type="primary"
          @click="handleExportXls('设备列表', getExportUrl)"
          >导出</a-button
        >
        <a-dropdown v-if="checkedKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined" />
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button>
            批量操作
            <Icon icon="ant-design:down-outlined" />
          </a-button>
        </a-dropdown>
      </template>

      <template #action="{ record }">
        <TableAction :actions="getActions(record)" :dropDownActions="getDropDownActions(record)" />
      </template>
    </BasicTable>

    <DeviceModal @register="registerModal" @success="reload" />
    <StatusChangeModal @register="registerStatusModal" @success="reload" />
    <RecordDrawer @register="registerRecordDrawer" />
  </div>
</template>
<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useDrawer } from '/@/components/Drawer';
  import { useMethods } from '/@/hooks/system/useMethods';
  import DeviceModal from './components/DeviceModal.vue';
  import StatusChangeModal from './components/StatusChangeModal.vue';
  import RecordDrawer from './components/RecordDrawer.vue';
  import {
    getDeviceList,
    deleteDevice,
    batchDeleteDevice,
    getExportUrl,
  } from '/@/api/device/device.api';
  import { columns, searchFormSchema, STATUS_TRANSITIONS, DEVICE_STATUS } from './device.data';

  defineOptions({ name: 'DeviceList' });

  const checkedKeys = ref<Array<string | number>>([]);
  const [registerModal, { openModal }] = useModal();
  const [registerStatusModal, { openModal: openStatusModal }] = useModal();
  const [registerRecordDrawer, { openDrawer }] = useDrawer();
  const { handleExportXls } = useMethods();

  const [registerTable, { reload }] = useTable({
    title: '设备列表',
    api: getDeviceList,
    columns,
    formConfig: {
      schemas: searchFormSchema,
      autoAdvancedCol: 3,
    },
    striped: true,
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    rowKey: 'id',
    actionColumn: {
      width: 200,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
  });

  const rowSelection = {
    type: 'checkbox',
    columnWidth: 40,
    selectedRowKeys: checkedKeys,
    onChange: (keys: (string | number)[]) => {
      checkedKeys.value = keys;
    },
  };

  function handleAdd() {
    openModal(true, { isUpdate: false });
  }

  function handleEdit(record) {
    openModal(true, { record, isUpdate: true });
  }

  function handleChangeStatus(record) {
    openStatusModal(true, { record });
  }

  function handleViewRecords(record) {
    openDrawer(true, { record });
  }

  async function handleDelete(record) {
    await deleteDevice({ id: record.id }, reload);
  }

  async function batchHandleDelete() {
    await batchDeleteDevice({ ids: checkedKeys.value }, reload);
  }

  /**
   * 行内操作：
   * - 报废设备不允许再变更状态（按钮置灰提示终态）
   * - 其他状态提供“状态变更”入口，可选项由前后端共同按流转矩阵限制
   */
  function getActions(record) {
    const canChange = (STATUS_TRANSITIONS[record.status] || []).length > 0;
    const statusLabel: Record<string, string> = {
      [DEVICE_STATUS.IN_USE]: '在用',
      [DEVICE_STATUS.REPAIR]: '维修',
      [DEVICE_STATUS.DISABLED]: '停用',
      [DEVICE_STATUS.SCRAPPED]: '报废',
    };
    return [
      {
        label: '编辑',
        auth: 'device:device:edit',
        onClick: handleEdit.bind(null, record),
      },
      {
        label: canChange ? '状态变更' : `${statusLabel[record.status]}(终态)`,
        disabled: !canChange,
        ifShow: true,
        onClick: canChange ? handleChangeStatus.bind(null, record) : undefined,
        tooltip: canChange ? '' : '报废为终态，不能再变更状态',
      },
    ];
  }

  function getDropDownActions(record) {
    return [
      {
        label: '操作记录',
        auth: 'device:device:record',
        onClick: handleViewRecords.bind(null, record),
      },
      {
        label: '删除',
        auth: 'device:device:delete',
        popConfirm: {
          title: '是否确认删除该设备？',
          confirm: handleDelete.bind(null, record),
        },
      },
    ];
  }
</script>
