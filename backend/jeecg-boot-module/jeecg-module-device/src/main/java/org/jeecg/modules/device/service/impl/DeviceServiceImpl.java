package org.jeecg.modules.device.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.device.entity.Device;
import org.jeecg.modules.device.enums.DeviceStatusEnum;
import org.jeecg.modules.device.mapper.DeviceMapper;
import org.jeecg.modules.device.service.IDeviceService;
import org.jeecg.modules.device.service.IDeviceStatusRecordService;
import org.jeecg.modules.device.vo.DeviceStatusChangeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 设备台账 Service 实现
 * @Author: device
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {

    @Autowired
    private IDeviceStatusRecordService deviceStatusRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDevice(Device device) {
        // 新建设备默认“在用”，忽略前端传入的状态
        device.setStatus(DeviceStatusEnum.IN_USE.getCode());
        this.save(device);
        // 写入初始化状态记录，作为状态追溯起点
        deviceStatusRecordService.record(
                device.getId(), device.getDeviceCode(), device.getDeviceName(),
                null, device.getStatus(), "新建设备，默认在用");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editDevice(Device device) {
        if (StringUtils.isBlank(device.getId())) {
            throw new JeecgBootException("设备ID不能为空");
        }
        Device dbDevice = this.getById(device.getId());
        if (dbDevice == null) {
            throw new JeecgBootException("设备不存在或已被删除");
        }
        // 编辑接口只允许维护基础信息，状态必须通过状态流转接口变更
        device.setStatus(dbDevice.getStatus());
        this.updateById(device);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(DeviceStatusChangeVO vo) {
        if (vo == null || StringUtils.isBlank(vo.getId())) {
            throw new JeecgBootException("设备ID不能为空");
        }
        Device device = this.getById(vo.getId());
        if (device == null) {
            throw new JeecgBootException("设备不存在或已被删除");
        }

        DeviceStatusEnum fromStatus = DeviceStatusEnum.getByCode(device.getStatus());
        if (fromStatus == null) {
            throw new JeecgBootException("设备当前状态不合法：" + device.getStatus());
        }
        DeviceStatusEnum toStatus = DeviceStatusEnum.getByCode(vo.getStatus());
        if (toStatus == null) {
            throw new JeecgBootException("目标状态不合法：" + vo.getStatus());
        }
        if (fromStatus == toStatus) {
            throw new JeecgBootException("设备已是【" + toStatus.getName() + "】状态，无需变更");
        }
        // 报废为终态，且整体流转受 allowedTargets 白名单约束
        if (!fromStatus.canTransitTo(toStatus)) {
            throw new JeecgBootException("设备状态不允许从【" + fromStatus.getName() + "】变更为【" + toStatus.getName() + "】");
        }
        // 维修恢复在用时必须填写说明
        if (fromStatus.isRepairResume(toStatus) && StringUtils.isBlank(vo.getRemark())) {
            throw new JeecgBootException("设备维修后恢复在用，必须填写维修/恢复说明");
        }

        String oldStatus = device.getStatus();
        device.setStatus(toStatus.getCode());
        this.updateById(device);

        // 状态变化写入操作记录
        deviceStatusRecordService.record(
                device.getId(), device.getDeviceCode(), device.getDeviceName(),
                oldStatus, toStatus.getCode(), vo.getRemark());
    }
}
