package org.jeecg.modules.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.device.entity.Device;
import org.jeecg.modules.device.entity.DeviceStatusRecord;
import org.jeecg.modules.device.vo.DeviceStatusChangeVO;

import java.util.List;

/**
 * @Description: 设备信息
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
public interface IDeviceService extends IService<Device> {

    /**
     * 新建设备。
     * 无论前端是否传入状态，都强制为默认状态"在用"，并写入一条"新建设备"操作记录。
     *
     * @param device 设备信息
     */
    void addDevice(Device device);

    /**
     * 设备状态流转。
     * 校验流转规则（报废为终态不可再变更等），维修恢复为在用时必须填写说明；
     * 变更成功后写入一条状态流转操作记录，便于后续追溯。
     *
     * @param changeVO 状态变更参数（设备ID、目标状态、说明）
     * @return 状态流转记录
     */
    DeviceStatusRecord changeStatus(DeviceStatusChangeVO changeVO);

    /**
     * 查询指定设备的状态流转记录（按操作时间倒序）
     *
     * @param deviceId 设备ID
     */
    List<DeviceStatusRecord> listStatusRecords(String deviceId);
}
