<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="设备状态变更" @ok="handleSubmit" width="520px">
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="设备名称">
        <span>{{ form.deviceName }}</span>
      </a-form-item>
      <a-form-item label="当前状态">
        <a-tag :color="beforeColor">{{ beforeLabel }}</a-tag>
      </a-form-item>
      <a-form-item label="目标状态" name="targetStatus">
        <a-radio-group v-model:value="form.targetStatus">
          <a-radio v-for="opt in targetOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</a-radio>
        </a-radio-group>
        <div v-if="targetOptions.length === 0" style="color: #999; line-height: 32px">
          当前为报废终态，设备不能再变更状态
        </div>
      </a-form-item>
      <a-form-item label="变更说明" name="remark">
        <a-textarea
          v-model:value="form.remark"
          :placeholder="remarkRequired ? '维修恢复为在用，请填写恢复说明（必填）' : '请输入变更说明（选填）'"
          :rows="3"
          :maxlength="500"
        />
      </a-form-item>
    </a-form>
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, reactive, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { changeDeviceStatus } from '/@/api/device/device.api';
  import { DEVICE_STATUS, STATUS_TRANSITIONS } from '../device.data';

  const emit = defineEmits(['register', 'success']);
  const { createMessage } = useMessage();
  const formRef = ref();

  // 与数据字典 device_status 的文案、颜色保持一致
  const STATUS_META: Record<string, { label: string; color: string }> = {
    [DEVICE_STATUS.IN_USE]: { label: '在用', color: 'green' },
    [DEVICE_STATUS.REPAIR]: { label: '维修', color: 'orange' },
    [DEVICE_STATUS.DISABLED]: { label: '停用', color: 'default' },
    [DEVICE_STATUS.SCRAPPED]: { label: '报废', color: 'red' },
  };

  const form = reactive<{
    id: string;
    deviceName: string;
    beforeStatus: string;
    targetStatus: string | undefined;
    remark: string;
  }>({
    id: '',
    deviceName: '',
    beforeStatus: '',
    targetStatus: undefined,
    remark: '',
  });

  const beforeLabel = computed(() => STATUS_META[form.beforeStatus]?.label || '');
  const beforeColor = computed(() => STATUS_META[form.beforeStatus]?.color || 'default');

  // 当前状态允许流转到的目标状态（流转矩阵与后端 DeviceStatusEnum 一致）
  const targetOptions = computed(() =>
    (STATUS_TRANSITIONS[form.beforeStatus] || []).map((v) => ({
      value: v,
      label: STATUS_META[v]?.label,
    })),
  );

  // 维修 -> 在用（恢复）必须填写说明
  const remarkRequired = computed(
    () => form.beforeStatus === DEVICE_STATUS.REPAIR && form.targetStatus === DEVICE_STATUS.IN_USE,
  );

  const rules = computed(() => ({
    targetStatus: [{ required: true, message: '请选择目标状态' }],
    remark: [{ required: remarkRequired.value, whitespace: true, message: '维修恢复为在用时必须填写恢复说明' }],
  }));

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    form.id = data.record.id;
    form.deviceName = data.record.deviceName;
    form.beforeStatus = data.record.status;
    form.targetStatus = undefined;
    form.remark = '';
    setModalProps({ confirmLoading: false });
  });

  async function handleSubmit() {
    try {
      await formRef.value.validate();
      setModalProps({ confirmLoading: true });
      await changeDeviceStatus({
        id: form.id,
        targetStatus: form.targetStatus,
        remark: form.remark,
      });
      createMessage.success('状态变更成功');
      closeModal();
      emit('success');
    } catch (e) {
      // 表单校验未通过，或后端按流转限制返回错误（错误信息由全局拦截器统一提示）
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
