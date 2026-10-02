import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/device/statusRecord/list',
}

/**
 * 设备状态流转操作记录分页列表
 * @param params
 */
export const getStatusRecordList = (params) => {
  return defHttp.get({ url: Api.list, params });
};
