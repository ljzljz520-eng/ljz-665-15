package org.jeecg.modules.device.service;

import org.jeecg.common.system.base.service.JeecgService;
import org.jeecg.modules.device.entity.Device;
import org.jeecg.modules.device.vo.DeviceStatusChangeVO;

/**
 * @Description: 设备台账 Service
 * @Author: device
 * @Date: 2026-09-10
 * @Version: V1.0
 */
public interface IDeviceService extends JeecgService<Device> {

    /**
     * 新建设备（默认状态为“在用”，并写入一条状态初始化记录）
     *
     * @param device 设备信息
     */
    void addDevice(Device device);

    /**
     * 编辑设备基础信息。
     * 状态不允许通过编辑接口直接修改，必须走 {@link #changeStatus(DeviceStatusChangeVO)}
     *
     * @param device 设备信息
     */
    void editDevice(Device device);

    /**
     * 设备状态流转。
     * 校验流转规则后更新设备状态，并写入一条操作记录。
     *
     * @param vo 状态变更参数（设备ID、目标状态、操作说明）
     */
    void changeStatus(DeviceStatusChangeVO vo);
}
