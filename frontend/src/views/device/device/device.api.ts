import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/device/device/list',
  save = '/device/device/add',
  edit = '/device/device/edit',
  get = '/device/device/queryById',
  delete = '/device/device/delete',
  deleteBatch = '/device/device/deleteBatch',
  changeStatus = '/device/device/changeStatus',
  statusRecordList = '/device/device/statusRecord/list',
}

/**
 * 设备状态枚举（与后端 DeviceStatusEnum 保持一致）
 * 1-在用 2-维修中 3-停用 4-报废
 */
export const DEVICE_STATUS = {
  IN_USE: '1',
  REPAIR: '2',
  DISABLED: '3',
  SCRAPPED: '4',
};

export const DEVICE_STATUS_OPTIONS = [
  { value: '1', label: '在用' },
  { value: '2', label: '维修中' },
  { value: '3', label: '停用' },
  { value: '4', label: '报废' },
];

/**
 * 状态流转规则（与后端 DeviceStatusEnum.allowedTargets 保持一致，用于前端按钮控制）
 */
export const DEVICE_STATUS_TRANSITIONS: Record<string, string[]> = {
  [DEVICE_STATUS.IN_USE]: [DEVICE_STATUS.REPAIR, DEVICE_STATUS.DISABLED, DEVICE_STATUS.SCRAPPED],
  [DEVICE_STATUS.REPAIR]: [DEVICE_STATUS.IN_USE, DEVICE_STATUS.DISABLED, DEVICE_STATUS.SCRAPPED],
  [DEVICE_STATUS.DISABLED]: [DEVICE_STATUS.IN_USE, DEVICE_STATUS.REPAIR, DEVICE_STATUS.SCRAPPED],
  // 报废为终态
  [DEVICE_STATUS.SCRAPPED]: [],
};

/**
 * 查询设备分页列表
 */
export const getDeviceList = (params) => {
  return defHttp.get({ url: Api.list, params });
};

/**
 * 新增 / 编辑设备
 */
export const saveOrUpdateDevice = (params, isUpdate) => {
  const url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url, params });
};

/**
 * 查询设备详情
 */
export const getDeviceById = (params) => {
  return defHttp.get({ url: Api.get, params });
};

/**
 * 删除设备
 */
export const deleteDevice = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.delete, data: params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

/**
 * 批量删除设备
 */
export const batchDeleteDevice = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

/**
 * 设备状态变更（后端校验流转规则；维修恢复在用时 remark 必填）
 */
export const changeDeviceStatus = (params) => {
  return defHttp.put({ url: Api.changeStatus, params });
};

/**
 * 查询设备状态流转操作记录
 */
export const getDeviceStatusRecordList = (params) => {
  return defHttp.get({ url: Api.statusRecordList, params });
};
