import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';

/**
 * 设备状态常量（与数据字典 device_status、后端 DeviceStatusEnum 保持一致）
 * 1-在用 2-维修 3-停用 4-报废
 */
export const DEVICE_STATUS = {
  IN_USE: '1',
  REPAIR: '2',
  DISABLED: '3',
  SCRAPPED: '4',
};

/**
 * 状态流转矩阵：当前状态 -> 允许变更到的状态
 * 报废为终态（空数组）；维修只能恢复为在用（恢复时必须填写说明）
 */
export const STATUS_TRANSITIONS: Record<string, string[]> = {
  [DEVICE_STATUS.IN_USE]: [DEVICE_STATUS.REPAIR, DEVICE_STATUS.DISABLED, DEVICE_STATUS.SCRAPPED],
  [DEVICE_STATUS.REPAIR]: [DEVICE_STATUS.IN_USE],
  [DEVICE_STATUS.DISABLED]: [DEVICE_STATUS.IN_USE, DEVICE_STATUS.REPAIR, DEVICE_STATUS.SCRAPPED],
  [DEVICE_STATUS.SCRAPPED]: [],
};

export const columns: BasicColumn[] = [
  {
    title: '设备编号',
    dataIndex: 'deviceCode',
    width: 140,
    resizable: true,
  },
  {
    title: '设备名称',
    dataIndex: 'deviceName',
    width: 180,
    resizable: true,
  },
  {
    title: '设备型号',
    dataIndex: 'deviceModel',
    width: 140,
    resizable: true,
  },
  {
    title: '设备类型',
    dataIndex: 'deviceType',
    width: 110,
    resizable: true,
    customRender: ({ record }) => render.renderDict(record.deviceType, 'device_type'),
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    resizable: true,
    customRender: ({ record }) => render.renderDict(record.status, 'device_status', true),
  },
  {
    title: '存放位置',
    dataIndex: 'location',
    width: 160,
    resizable: true,
  },
  {
    title: '责任人',
    dataIndex: 'owner',
    width: 100,
    resizable: true,
  },
  {
    title: '启用日期',
    dataIndex: 'useDate',
    width: 120,
    resizable: true,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'deviceCode',
    label: '设备编号',
    component: 'Input',
    componentProps: { trim: true },
    colProps: { span: 6 },
  },
  {
    field: 'deviceName',
    label: '设备名称',
    component: 'Input',
    componentProps: { trim: true },
    colProps: { span: 6 },
  },
  {
    field: 'status',
    label: '状态',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'device_status',
      placeholder: '请选择状态',
    },
    colProps: { span: 6 },
  },
];

export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: 'id',
    component: 'Input',
    show: false,
  },
  {
    field: 'deviceCode',
    label: '设备编号',
    component: 'Input',
    required: true,
    componentProps: { placeholder: '请输入设备编号', maxlength: 50 },
  },
  {
    field: 'deviceName',
    label: '设备名称',
    component: 'Input',
    required: true,
    componentProps: { placeholder: '请输入设备名称', maxlength: 100 },
  },
  {
    field: 'deviceModel',
    label: '设备型号',
    component: 'Input',
    componentProps: { placeholder: '请输入设备型号', maxlength: 100 },
  },
  {
    field: 'deviceType',
    label: '设备类型',
    component: 'JDictSelectTag',
    componentProps: { dictCode: 'device_type', placeholder: '请选择设备类型' },
  },
  {
    field: 'location',
    label: '存放位置',
    component: 'Input',
    componentProps: { placeholder: '请输入存放位置', maxlength: 200 },
  },
  {
    field: 'owner',
    label: '责任人',
    component: 'Input',
    componentProps: { placeholder: '请输入责任人', maxlength: 50 },
  },
  {
    field: 'useDate',
    label: '启用日期',
    component: 'DatePicker',
    componentProps: { valueFormat: 'YYYY-MM-DD', style: { width: '100%' } },
  },
  {
    // 状态只能通过“状态变更”按钮修改，表单中仅做只读展示
    field: 'status',
    label: '当前状态',
    component: 'Input',
    ifShow: false,
  },
  {
    label: '',
    field: 'statusTip',
    component: 'Input',
    ifShow: false,
  },
  {
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
    componentProps: { placeholder: '请输入备注', rows: 3, maxlength: 500 },
  },
];
