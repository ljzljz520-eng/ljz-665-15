import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/device/device/list',
  save = '/device/device/add',
  edit = '/device/device/edit',
  get = '/device/device/queryById',
  delete = '/device/device/delete',
  deleteBatch = '/device/device/deleteBatch',
  changeStatus = '/device/device/changeStatus',
  statusRecord = '/device/device/statusRecord',
  allowedTargetStatus = '/device/device/allowedTargetStatus',
}

/**
 * 设备分页列表
 * @param params
 */
export const getDeviceList = (params) => {
  return defHttp.get({ url: Api.list, params });
};

/**
 * 新增设备（后端强制默认状态：在用）
 * @param params
 */
export const addDevice = (params) => {
  return defHttp.post({ url: Api.save, params });
};

/**
 * 编辑设备基础信息（后端不允许通过此接口修改状态）
 * @param params
 */
export const editDevice = (params) => {
  return defHttp.put({ url: Api.edit, params });
};

/**
 * 设备状态流转
 * @param params { id, targetStatus, remark }
 */
export const changeDeviceStatus = (params) => {
  return defHttp.put({ url: Api.changeStatus, params });
};

/**
 * 查询单台设备的状态流转记录
 * @param id 设备ID
 */
export const getDeviceStatusRecord = (id: string) => {
  return defHttp.get({ url: Api.statusRecord, params: { id } });
};

/**
 * 查询某状态下允许流转的目标状态编码
 * @param status 当前状态
 */
export const getAllowedTargetStatus = (status: string) => {
  return defHttp.get({ url: Api.allowedTargetStatus, params: { status } });
};

/**
 * 删除设备
 * @param params
 */
export const deleteDevice = (params, handleSuccess?) => {
  return defHttp.delete({ url: Api.delete, data: params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess && handleSuccess();
  });
};

/**
 * 批量删除设备
 * @param params
 */
export const batchDeleteDevice = (params, handleSuccess?) => {
  return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
    if (handleSuccess) {
      handleSuccess();
    }
  });
};
