<template>
  <BasicModal
    :title="title"
    :width="width"
    :visible="visible"
    @ok="handleOk"
    :okButtonProps="{ class: { 'jee-hidden': disableSubmit } }"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <DeviceForm ref="realForm" @ok="submitCallback"></DeviceForm>
  </BasicModal>
</template>

<script lang="ts" name="DeviceModal" setup>
  import { ref, nextTick } from 'vue';
  import DeviceForm from './DeviceForm.vue';
  import { BasicModal } from '/@/components/Modal';

  const title = ref<string>('');
  const width = ref<number>(700);
  const visible = ref<boolean>(false);
  const disableSubmit = ref<boolean>(false);
  const realForm = ref();
  const emit = defineEmits(['register', 'ok']);

  function add() {
    title.value = '新建设备';
    visible.value = true;
    disableSubmit.value = false;
    nextTick(() => {
      realForm.value.add();
    });
  }

  function edit(record) {
    title.value = '编辑设备';
    visible.value = true;
    disableSubmit.value = false;
    nextTick(() => {
      realForm.value.edit(record);
    });
  }

  function handleOk() {
    realForm.value.submitForm();
  }

  function submitCallback() {
    handleCancel();
    emit('ok');
  }

  function handleCancel() {
    visible.value = false;
  }

  defineExpose({
    add,
    edit,
    disableSubmit,
  });
</script>

<style lang="less">
  .jee-hidden {
    display: none !important;
  }
</style>
