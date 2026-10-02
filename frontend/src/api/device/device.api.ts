import { defHttp } from '/@/utils/http/axios';
import { Modal } from 'ant-design-vue';

enum Api {
  list = '/device/device/list',
  save = '/device/device/add',
  edit = '/device/device/edit',
  get = '/device/device/queryById',
  delete = '/device/device/delete',
  deleteBatch = '/device/device/deleteBatch',
  changeStatus = '/device/device/changeStatus',
  exportXls = '/device/device/exportXls',
  importExcel = '/device/device/importExcel',
  recordList = '/device/statusRecord/list',
}

export const getExportUrl = Api.exportXls;
export const getImportUrl = Api.importExcel;

/**
 * 设备分页列表
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
 * 设备详情
 */
export const getDeviceById = (params) => {
  return defHttp.get({ url: Api.get, params });
};

/**
 * 设备状态流转（维修 / 停用 / 报废 / 恢复）
 * @param params { id, targetStatus, remark }
 */
export const changeDeviceStatus = (params) => {
  return defHttp.post({ url: Api.changeStatus, params });
};

/**
 * 设备状态操作记录（追溯）
 */
export const getStatusRecordList = (params) => {
  return defHttp.get({ url: Api.recordList, params });
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
  Modal.confirm({
    title: '确认删除',
    content: '是否删除选中设备',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};
