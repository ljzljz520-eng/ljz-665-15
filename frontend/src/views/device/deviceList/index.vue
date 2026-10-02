<template>
  <div class="p-2">
    <a-card :bordered="false">
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="16">
            <a-col :span="6">
              <a-form-item label="设备编号">
                <a-input v-model:value="queryParam.deviceCode" placeholder="请输入设备编号" allow-clear @keyup.enter="searchQuery" />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="设备名称">
                <a-input v-model:value="queryParam.deviceName" placeholder="请输入设备名称" allow-clear @keyup.enter="searchQuery" />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="设备状态">
                <a-select
                  v-model:value="queryParam.status"
                  placeholder="请选择设备状态"
                  allow-clear
                  :options="statusOptions"
                  :field-names="{ label: 'text', value: 'value' }"
                />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <span>
                <a-button type="primary" @click="searchQuery">查询</a-button>
                <a-button class="ml-2" @click="searchReset">重置</a-button>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 操作按钮区域 -->
      <div class="table-operator">
        <a-button @click="handleAdd" type="primary" preIcon="ant-design:plus">新增</a-button>
        <a-button danger class="ml-2" @click="batchDel" :disabled="selectedRowKeys.length === 0">
          <template #icon><Icon icon="ant-design:delete-outlined" /></template>
          批量删除
        </a-button>
      </div>

      <!-- 表格区域 -->
      <a-table
        size="middle"
        :scroll="{ x: 1200 }"
        bordered
        row-key="id"
        :columns="columns"
        :data-source="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="deviceStatusColor(record.status)">{{ deviceStatusText(record.status) }}</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-dropdown>
              <a>状态变更<Icon icon="mdi:chevron-down" /></a>
              <template #overlay>
                <a-menu>
                  <a-menu-item v-for="opt in nextStatusOptions(record.status)" :key="opt.value" @click="handleChangeStatus(record, opt)">
                    <a-tag :color="deviceStatusColor(opt.value)" style="margin-right: 6px">{{ opt.text }}</a-tag>
                  </a-menu-item>
                  <a-menu-item v-if="nextStatusOptions(record.status).length === 0" disabled>
                    已报废，不可变更
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
            <a-divider type="vertical" />
            <a @click="handleViewRecord(record)">操作记录</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除该设备吗?" @confirm="handleDelete(record.id)">
              <a class="danger-link">删除</a>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>

    <DeviceModal ref="deviceModal" @ok="handleSuccess" />
    <StatusChangeModal ref="statusChangeModal" @ok="handleSuccess" />
    <StatusRecordDrawer ref="statusRecordDrawer" />
  </div>
</template>

<script lang="ts" name="DeviceList" setup>
  import { ref, reactive, onMounted } from 'vue';
  import { Modal } from 'ant-design-vue';
  import Icon from '/@/components/Icon/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getDeviceList, deleteDevice, batchDeleteDevice, getAllowedTargetStatus } from '/@/api/device/device.api';
  import { deviceStatusText, deviceStatusColor, deviceStatusMap } from '/@/api/device/device.data';
  import DeviceModal from './components/DeviceModal.vue';
  import StatusChangeModal from './components/StatusChangeModal.vue';
  import StatusRecordDrawer from './components/StatusRecordDrawer.vue';

  const { createMessage } = useMessage();

  const deviceModal = ref();
  const statusChangeModal = ref();
  const statusRecordDrawer = ref();

  const loading = ref(false);
  const dataSource = ref<any[]>([]);
  const selectedRowKeys = ref<string[]>([]);
  const queryParam = reactive<Recordable>({
    deviceCode: '',
    deviceName: '',
    status: undefined,
  });

  const statusOptions = Object.entries(deviceStatusMap).map(([value, item]) => ({ value, text: item.text }));

  const columns = [
    { title: '设备编号', dataIndex: 'deviceCode', align: 'center', width: 160 },
    { title: '设备名称', dataIndex: 'deviceName', align: 'center', width: 180 },
    { title: '设备型号', dataIndex: 'deviceModel', align: 'center', width: 160 },
    { title: '设备状态', dataIndex: 'status', align: 'center', width: 110 },
    { title: '存放位置', dataIndex: 'location', align: 'center', width: 180, ellipsis: true },
    { title: '创建人', dataIndex: 'createBy', align: 'center', width: 110 },
    { title: '创建时间', dataIndex: 'createTime', align: 'center', width: 170 },
    { title: '操作', dataIndex: 'action', align: 'center', fixed: 'right', width: 280 },
  ];

  const ipagination = reactive({
    current: 1,
    pageSize: 10,
    pageSizeOptions: ['10', '20', '30'],
    showTotal: (total) => `共 ${total} 条`,
    showQuickJumper: true,
    showSizeChanger: true,
    total: 0,
  });

  // 各状态可流转的目标状态（后端规则的本地镜像，用于菜单展示；最终以后端校验为准）
  const transitionMap: Record<string, { value: string; text: string }[]> = {
    '1': [
      { value: '2', text: '转维修' },
      { value: '3', text: '停用' },
      { value: '4', text: '报废' },
    ],
    '2': [
      { value: '1', text: '恢复在用' },
      { value: '3', text: '停用' },
      { value: '4', text: '报废' },
    ],
    '3': [
      { value: '1', text: '启用' },
      { value: '2', text: '转维修' },
      { value: '4', text: '报废' },
    ],
    '4': [],
  };

  function nextStatusOptions(status: string) {
    return transitionMap[status] || [];
  }

  function onSelectChange(keys) {
    selectedRowKeys.value = keys;
  }

  function handleTableChange(pagination) {
    ipagination.current = pagination.current;
    ipagination.pageSize = pagination.pageSize;
    loadData();
  }

  function getQueryParams() {
    return {
      pageNo: ipagination.current,
      pageSize: ipagination.pageSize,
      deviceCode: queryParam.deviceCode || undefined,
      deviceName: queryParam.deviceName || undefined,
      status: queryParam.status || undefined,
    };
  }

  function loadData() {
    loading.value = true;
    getDeviceList(getQueryParams())
      .then((res) => {
        dataSource.value = res.records || [];
        ipagination.total = res.total || 0;
      })
      .finally(() => {
        loading.value = false;
      });
  }

  function searchQuery() {
    ipagination.current = 1;
    loadData();
  }

  function searchReset() {
    queryParam.deviceCode = '';
    queryParam.deviceName = '';
    queryParam.status = undefined;
    ipagination.current = 1;
    loadData();
  }

  function handleAdd() {
    deviceModal.value.add();
  }

  function handleEdit(record) {
    deviceModal.value.edit(record);
  }

  async function handleChangeStatus(record, opt) {
    // 从后端实时获取允许流转的目标状态，避免本地规则与后端不一致
    let allowed: string[] = [];
    try {
      allowed = await getAllowedTargetStatus(record.status);
    } catch (e) {
      allowed = nextStatusOptions(record.status).map((o) => o.value);
    }
    if (!allowed.includes(opt.value)) {
      createMessage.warning('当前状态不允许执行该操作');
      return;
    }
    statusChangeModal.value.open(record, allowed, opt.value);
  }

  function handleViewRecord(record) {
    statusRecordDrawer.value.open(record);
  }

  function handleDelete(id) {
    deleteDevice({ id }, () => {
      createMessage.success('删除成功');
      handleSuccess();
    });
  }

  function batchDel() {
    if (selectedRowKeys.value.length === 0) {
      createMessage.warning('请选择要删除的设备');
      return;
    }
    Modal.confirm({
      title: '确认删除',
      content: `确定删除选中的 ${selectedRowKeys.value.length} 台设备吗？`,
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk: () => {
        return batchDeleteDevice({ ids: selectedRowKeys.value.join(',') }, () => {
          createMessage.success('批量删除成功');
          handleSuccess();
        });
      },
    });
  }

  function handleSuccess() {
    selectedRowKeys.value = [];
    loadData();
  }

  onMounted(() => {
    loadData();
  });
</script>

<style lang="less" scoped>
  .table-operator {
    margin: 0 0 16px 0;
  }
  .danger-link {
    color: #ff4d4f;
  }
  .ml-2 {
    margin-left: 8px;
  }
</style>
