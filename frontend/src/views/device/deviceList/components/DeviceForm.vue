<template>
  <a-spin :spinning="confirmLoading">
    <a-form class="antd-modal-form" ref="formRef" :model="formState" :rules="rules">
      <a-row>
        <a-col :span="24">
          <a-form-item label="设备编号" name="deviceCode" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-input v-model:value="formState.deviceCode" placeholder="请输入设备编号" :disabled="isUpdate" />
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="设备名称" name="deviceName" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-input v-model:value="formState.deviceName" placeholder="请输入设备名称" />
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="设备型号" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-input v-model:value="formState.deviceModel" placeholder="请输入设备型号" />
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="设备状态" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-input :value="isUpdate ? deviceStatusText(formState.status) : '在用（新建设备默认）'" disabled />
            <div class="form-tip">状态不可在编辑中修改，请使用列表中的“状态变更”操作</div>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="存放位置" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-input v-model:value="formState.location" placeholder="请输入存放位置" />
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="备注" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-textarea v-model:value="formState.remark" :rows="3" placeholder="请输入备注" />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-spin>
</template>

<script lang="ts" name="DeviceForm" setup>
  import { ref, reactive } from 'vue';
  import { addDevice, editDevice } from '/@/api/device/device.api';
  import { deviceStatusText } from '/@/api/device/device.data';

  const emit = defineEmits(['register', 'ok']);

  const formRef = ref();
  const confirmLoading = ref(false);
  const isUpdate = ref(false);
  const labelCol = reactive({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = reactive({ xs: { span: 24 }, sm: { span: 18 } });

  const formState = reactive<Recordable>({
    id: '',
    deviceCode: '',
    deviceName: '',
    deviceModel: '',
    status: '1',
    location: '',
    remark: '',
  });

  const rules = {
    deviceCode: [{ required: true, message: '请输入设备编号！', trigger: 'blur' }],
    deviceName: [{ required: true, message: '请输入设备名称！', trigger: 'blur' }],
  };

  function resetForm() {
    formState.id = '';
    formState.deviceCode = '';
    formState.deviceName = '';
    formState.deviceModel = '';
    formState.status = '1';
    formState.location = '';
    formState.remark = '';
  }

  function add() {
    resetForm();
    isUpdate.value = false;
  }

  function edit(record) {
    resetForm();
    isUpdate.value = true;
    Object.assign(formState, record);
  }

  async function submitForm() {
    try {
      await formRef.value.validate();
    } catch {
      return;
    }
    confirmLoading.value = true;
    const saveOrUpdate = isUpdate.value ? editDevice : addDevice;
    saveOrUpdate({ ...formState })
      .then(() => {
        emit('ok');
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }

  defineExpose({ add, edit, submitForm });
</script>

<style lang="less" scoped>
  .form-tip {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.45);
    line-height: 1.6;
  }
</style>
