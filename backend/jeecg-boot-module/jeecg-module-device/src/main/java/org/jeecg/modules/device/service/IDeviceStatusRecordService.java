package org.jeecg.modules.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.common.system.base.service.JeecgService;
import org.jeecg.modules.device.entity.DeviceStatusRecord;

/**
 * @Description: 设备状态流转记录 Service
 * @Author: device
 * @Date: 2026-09-10
 * @Version: V1.0
 */
public interface IDeviceStatusRecordService extends JeecgService<DeviceStatusRecord> {

    /**
     * 记录一次状态流转（设备新建时 fromStatus 为空）
     *
     * @param deviceId   设备ID
     * @param deviceCode 设备编号
     * @param deviceName 设备名称
     * @param fromStatus 变更前状态（新建时为 null）
     * @param toStatus   变更后状态
     * @param remark     操作说明
     */
    void record(String deviceId, String deviceCode, String deviceName, String fromStatus, String toStatus, String remark);

    /**
     * 分页查询某台设备的状态流转记录（按操作时间倒序）
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param deviceId 设备ID
     * @return 分页记录
     */
    IPage<DeviceStatusRecord> pageByDevice(Integer pageNo, Integer pageSize, String deviceId);
}
