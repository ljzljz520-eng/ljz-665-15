<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit" :width="640">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts" setup name="device-device-modal">
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from '../device.data';
  import { saveOrUpdateDevice, getDeviceById } from '../device.api';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(true);

  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetFields();
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;
    if (unref(isUpdate)) {
      const record = await getDeviceById({ id: data.record.id });
      await setFieldsValue({ ...record });
    }
  });

  const getTitle = computed(() => (!unref(isUpdate) ? '新建设备' : '编辑设备'));

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      // 状态由后端控制：新建默认在用；编辑时不允许通过此表单修改状态
      await saveOrUpdateDevice(values, isUpdate.value);
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
