<template>
  <BasicModal
    title="设备状态变更"
    :width="600"
    :visible="visible"
    :confirm-loading="confirmLoading"
    :ok-text="okText"
    :ok-type="formState.targetStatus === '4' ? 'danger' : 'primary'"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="取消"
  >
    <a-descriptions :column="1" bordered size="small" class="mb-3">
      <a-descriptions-item label="设备编号">{{ record.deviceCode }}</a-descriptions-item>
      <a-descriptions-item label="设备名称">{{ record.deviceName }}</a-descriptions-item>
      <a-descriptions-item label="当前状态">
        <a-tag :color="deviceStatusColor(record.status)">{{ deviceStatusText(record.status) }}</a-tag>
      </a-descriptions-item>
    </a-descriptions>

    <a-form ref="formRef" :model="formState" :rules="rules">
      <a-form-item label="变更为" name="targetStatus" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-radio-group v-model:value="formState.targetStatus" @change="onTargetChange">
          <a-radio v-for="opt in targetOptions" :key="opt.value" :value="opt.value">
            <a-tag :color="deviceStatusColor(opt.value)" style="margin-right: 0">{{ opt.text }}</a-tag>
          </a-radio>
        </a-radio-group>
        <div v-if="!targetOptions.length" class="form-tip">
          当前设备已报废，报废为终态，不能再变更状态（不能恢复为在用）。
        </div>
      </a-form-item>
      <a-form-item label="变更说明" name="remark" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-textarea
          v-model:value="formState.remark"
          :rows="4"
          :placeholder="remarkRequired ? '维修恢复为在用必须填写恢复说明，请输入维修/恢复情况' : '请输入变更说明（选填）'"
          :maxlength="500"
          show-count
        />
      </a-form-item>
      <a-alert
        v-if="formState.targetStatus === '4'"
        type="warning"
        show-icon
        message="报废后设备不能再回到在用或其他状态，请谨慎操作！"
        class="mb-2"
      />
    </a-form>
  </BasicModal>
</template>

<script lang="ts" name="StatusChangeModal" setup>
  import { ref, reactive, computed, nextTick } from 'vue';
  import { BasicModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { changeDeviceStatus } from '/@/api/device/device.api';
  import { deviceStatusMap, deviceStatusText, deviceStatusColor } from '/@/api/device/device.data';

  const emit = defineEmits(['ok']);
  const { createMessage } = useMessage();

  const visible = ref(false);
  const confirmLoading = ref(false);
  const record = ref<Recordable>({});
  const formRef = ref();
  const labelCol = reactive({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = reactive({ xs: { span: 24 }, sm: { span: 18 } });

  const formState = reactive({
    targetStatus: undefined as string | undefined,
    remark: '',
  });

  // 当前状态允许流转的目标状态
  const targetOptions = computed(() => {
    const allowed: string[] = record.value._allowedTargets || [];
    return allowed.map((v) => ({ value: v, text: deviceStatusMap[v]?.text ?? v }));
  });

  // 是否为"维修 -> 在用"的恢复操作
  const remarkRequired = computed(() => record.value.status === '2' && formState.targetStatus === '1');

  const okText = computed(() => (formState.targetStatus === '4' ? '确认报废' : '确定'));

  const rules = computed(() => ({
    targetStatus: [{ required: true, message: '请选择目标状态！' }],
    remark: remarkRequired.value ? [{ required: true, message: '维修恢复为在用时必须填写恢复说明！', trigger: 'blur' }] : [],
  }));

  function onTargetChange() {
    nextTick(() => formRef.value?.clearValidate(['remark']));
  }

  function open(row, allowedTargets: string[], defaultTarget?: string) {
    record.value = { ...row, _allowedTargets: allowedTargets };
    formState.targetStatus = allowedTargets.includes(defaultTarget || '') ? defaultTarget : undefined;
    formState.remark = '';
    visible.value = true;
  }

  async function handleOk() {
    if (!formState.targetStatus) {
      createMessage.warning('请选择目标状态');
      return;
    }
    try {
      await formRef.value.validate();
    } catch {
      return;
    }
    confirmLoading.value = true;
    changeDeviceStatus({
      id: record.value.id,
      targetStatus: formState.targetStatus,
      remark: formState.remark,
    })
      .then(() => {
        createMessage.success('状态变更成功');
        visible.value = false;
        emit('ok');
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }

  function handleCancel() {
    visible.value = false;
  }

  defineExpose({ open });
</script>

<style lang="less" scoped>
  .form-tip {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.45);
    line-height: 1.6;
  }
</style>
