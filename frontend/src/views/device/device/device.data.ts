import { BasicColumn, FormSchema } from '/@/components/Table';
import { DEVICE_STATUS_OPTIONS } from './device.api';

/**
 * 状态对应标签颜色
 */
export const STATUS_COLOR_MAP: Record<string, string> = {
  '1': 'green',
  '2': 'orange',
  '3': 'default',
  '4': 'red',
};

export const columns: BasicColumn[] = [
  {
    title: '设备编号',
    dataIndex: 'deviceCode',
    width: 160,
    align: 'left',
  },
  {
    title: '设备名称',
    dataIndex: 'deviceName',
    width: 180,
    align: 'left',
  },
  {
    title: '设备型号',
    dataIndex: 'model',
    width: 140,
  },
  {
    title: '设备状态',
    dataIndex: 'status',
    width: 100,
    align: 'center',
    slots: { customRender: 'status' },
  },
  {
    title: '启用日期',
    dataIndex: 'useDate',
    width: 120,
  },
  {
    title: '存放位置',
    dataIndex: 'location',
    width: 160,
  },
  {
    title: '备注',
    dataIndex: 'remark',
  },
  {
    title: '创建人',
    dataIndex: 'createBy',
    width: 100,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 170,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'deviceCode',
    label: '设备编号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'deviceName',
    label: '设备名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'status',
    label: '设备状态',
    component: 'Select',
    componentProps: { options: DEVICE_STATUS_OPTIONS, allowClear: true, placeholder: '请选择状态' },
    colProps: { span: 6 },
  },
];

export const formSchema: FormSchema[] = [
  {
    label: '主键',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    field: 'deviceCode',
    label: '设备编号',
    component: 'Input',
    required: true,
    componentProps: { placeholder: '请输入设备编号' },
  },
  {
    field: 'deviceName',
    label: '设备名称',
    component: 'Input',
    required: true,
    componentProps: { placeholder: '请输入设备名称' },
  },
  {
    field: 'model',
    label: '设备型号',
    component: 'Input',
  },
  {
    field: 'useDate',
    label: '启用日期',
    component: 'DatePicker',
    componentProps: { valueFormat: 'YYYY-MM-DD', style: { width: '100%' } },
  },
  {
    field: 'location',
    label: '存放位置',
    component: 'Input',
  },
  {
    label: '设备状态',
    field: 'status',
    component: 'Input',
    show: false,
  },
  {
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
    componentProps: { rows: 3, maxlength: 500 },
  },
];

/**
 * 状态流转记录表格列
 */
export const recordColumns: BasicColumn[] = [
  {
    title: '设备编号',
    dataIndex: 'deviceCode',
    width: 140,
  },
  {
    title: '变更前状态',
    dataIndex: 'fromStatus',
    width: 110,
    align: 'center',
    slots: { customRender: 'fromStatus' },
  },
  {
    title: '变更后状态',
    dataIndex: 'toStatus',
    width: 110,
    align: 'center',
    slots: { customRender: 'toStatus' },
  },
  {
    title: '操作说明',
    dataIndex: 'remark',
  },
  {
    title: '操作人',
    dataIndex: 'createBy',
    width: 100,
  },
  {
    title: '操作时间',
    dataIndex: 'operateTime',
    width: 170,
  },
];
