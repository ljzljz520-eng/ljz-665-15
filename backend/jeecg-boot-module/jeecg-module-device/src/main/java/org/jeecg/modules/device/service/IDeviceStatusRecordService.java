package org.jeecg.modules.device.service;

import org.jeecg.common.system.base.service.JeecgService;
import org.jeecg.modules.device.entity.DeviceStatusRecord;

/**
 * 设备状态操作记录 Service
 */
public interface IDeviceStatusRecordService extends JeecgService<DeviceStatusRecord> {

    /**
     * 记录一次状态变化。
     *
     * @param deviceId    设备ID
     * @param beforeStatus 变更前状态（新建时为 null）
     * @param afterStatus  变更后状态
     * @param operateType  操作类型：CREATE / CHANGE
     * @param remark       变更说明
     */
    void record(String deviceId, String beforeStatus, String afterStatus, String operateType, String remark);
}
