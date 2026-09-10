<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="设备状态变更" :width="520" @ok="handleSubmit">
    <a-descriptions bordered size="small" :column="1" class="mb-3">
      <a-descriptions-item label="设备编号">{{ record.deviceCode }}</a-descriptions-item>
      <a-descriptions-item label="设备名称">{{ record.deviceName }}</a-descriptions-item>
      <a-descriptions-item label="当前状态">
        <a-tag :color="currentColor">{{ currentStatusName }}</a-tag>
      </a-descriptions-item>
    </a-descriptions>

    <a-form layout="vertical">
      <a-form-item label="目标状态" required>
        <a-select v-model:value="targetStatus" placeholder="请选择目标状态">
          <a-select-option v-for="item in targetOptions" :key="item.value" :value="item.value">
            <a-tag :color="STATUS_COLOR_MAP[item.value]" style="margin-right: 0">{{ item.label }}</a-tag>
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="操作说明" :required="remarkRequired">
        <a-textarea
          v-model:value="remark"
          :rows="3"
          :maxlength="500"
          show-count
          :placeholder="remarkRequired ? '设备维修后恢复在用，必须填写维修/恢复说明' : '请输入操作说明（选填）'"
        />
      </a-form-item>
      <a-alert v-if="isScrap" type="warning" show-icon message="报废为终态，报废后设备不能再变更为其他状态（含在用），请谨慎操作！" />
    </a-form>
  </BasicModal>
</template>

<script lang="ts" setup name="device-status-change-modal">
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import {
    changeDeviceStatus,
    DEVICE_STATUS,
    DEVICE_STATUS_OPTIONS,
    DEVICE_STATUS_TRANSITIONS,
  } from '../device.api';
  import { STATUS_COLOR_MAP } from '../device.data';

  const emit = defineEmits(['success', 'register']);
  const { createMessage } = useMessage();

  const record = ref<any>({});
  const targetStatus = ref<string | undefined>(undefined);
  const remark = ref('');

  const currentStatusName = computed(() => {
    return DEVICE_STATUS_OPTIONS.find((i) => i.value === record.value.status)?.label || '';
  });
  const currentColor = computed(() => STATUS_COLOR_MAP[record.value.status] || 'default');

  /**
   * 仅展示当前状态允许流转到的目标状态（报废无目标可选）
   */
  const targetOptions = computed(() => {
    const allowed = DEVICE_STATUS_TRANSITIONS[record.value.status] || [];
    return DEVICE_STATUS_OPTIONS.filter((i) => allowed.includes(i.value));
  });

  /**
   * 是否为“维修中 -> 在用”（恢复），恢复必须填写说明
   */
  const remarkRequired = computed(
    () => record.value.status === DEVICE_STATUS.REPAIR && targetStatus.value === DEVICE_STATUS.IN_USE,
  );

  /**
   * 目标是否为报废
   */
  const isScrap = computed(() => targetStatus.value === DEVICE_STATUS.SCRAPPED);

  const [registerModal, { setModalProps, closeModal }] = useModalInner((data) => {
    record.value = data?.record || {};
    targetStatus.value = undefined;
    remark.value = '';
    setModalProps({ confirmLoading: false });
  });

  async function handleSubmit() {
    if (!targetStatus.value) {
      createMessage.warning('请选择目标状态');
      return;
    }
    if (remarkRequired.value && !remark.value?.trim()) {
      createMessage.warning('设备维修后恢复在用，必须填写维修/恢复说明');
      return;
    }
    try {
      setModalProps({ confirmLoading: true });
      await changeDeviceStatus({
        id: record.value.id,
        status: targetStatus.value,
        remark: remark.value?.trim() || undefined,
      });
      createMessage.success('状态变更成功');
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
