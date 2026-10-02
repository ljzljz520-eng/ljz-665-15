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
            <a-col :span="8">
              <a-form-item label="状态变化">
                <a-select
                  v-model:value="queryParam.afterStatus"
                  placeholder="变更后状态"
                  allow-clear
                  style="width: 140px"
                  :options="statusOptions"
                  :field-names="{ label: 'text', value: 'value' }"
                />
              </a-form-item>
            </a-col>
            <a-col :span="4">
              <a-button type="primary" @click="searchQuery">查询</a-button>
              <a-button class="ml-2" @click="searchReset">重置</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 表格区域 -->
      <a-table
        size="middle"
        :scroll="{ x: 1100 }"
        bordered
        row-key="id"
        :columns="columns"
        :data-source="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'statusChange'">
            <a-tag :color="deviceStatusColor(record.beforeStatus)">
              {{ record.beforeStatus ? deviceStatusText(record.beforeStatus) : '新建' }}
            </a-tag>
            <Icon icon="ant-design:right-outlined" class="arrow-icon" />
            <a-tag :color="deviceStatusColor(record.afterStatus)">{{ deviceStatusText(record.afterStatus) }}</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'remark'">
            <span>{{ record.remark || '—' }}</span>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script lang="ts" name="DeviceStatusRecord" setup>
  import { ref, reactive, onMounted } from 'vue';
  import Icon from '/@/components/Icon/index';
  import { getStatusRecordList } from '/@/api/device/statusRecord.api';
  import { deviceStatusText, deviceStatusColor, deviceStatusMap } from '/@/api/device/device.data';

  const loading = ref(false);
  const dataSource = ref<any[]>([]);
  const queryParam = reactive<Recordable>({
    deviceCode: '',
    deviceName: '',
    afterStatus: undefined,
  });

  const statusOptions = Object.entries(deviceStatusMap).map(([value, item]) => ({ value, text: item.text }));

  const columns = [
    { title: '设备编号', dataIndex: 'deviceCode', align: 'center', width: 150 },
    { title: '设备名称', dataIndex: 'deviceName', align: 'center', width: 180 },
    { title: '状态变化', dataIndex: 'statusChange', align: 'center', width: 200 },
    { title: '变更说明', dataIndex: 'remark', align: 'center', ellipsis: true },
    { title: '操作人', dataIndex: 'createBy', align: 'center', width: 120 },
    { title: '操作时间', dataIndex: 'createTime', align: 'center', width: 170 },
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
      afterStatus: queryParam.afterStatus || undefined,
    };
  }

  function loadData() {
    loading.value = true;
    getStatusRecordList(getQueryParams())
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
    queryParam.afterStatus = undefined;
    ipagination.current = 1;
    loadData();
  }

  onMounted(() => {
    loadData();
  });
</script>

<style lang="less" scoped>
  .arrow-icon {
    margin: 0 6px;
    color: rgba(0, 0, 0, 0.45);
  }
  .ml-2 {
    margin-left: 8px;
  }
</style>
