package org.jeecg.modules.device.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.device.entity.Device;
import org.jeecg.modules.device.entity.DeviceStatusRecord;
import org.jeecg.modules.device.enums.DeviceStatusEnum;
import org.jeecg.modules.device.mapper.DeviceMapper;
import org.jeecg.modules.device.service.IDeviceService;
import org.jeecg.modules.device.service.IDeviceStatusRecordService;
import org.jeecg.modules.device.vo.DeviceStatusChangeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 设备信息
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Slf4j
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {

    private final IDeviceStatusRecordService deviceStatusRecordService;

    public DeviceServiceImpl(IDeviceStatusRecordService deviceStatusRecordService) {
        this.deviceStatusRecordService = deviceStatusRecordService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDevice(Device device) {
        // 新建设备强制为默认状态"在用"，忽略前端传入的 status
        device.setStatus(DeviceStatusEnum.defaultStatus().getCode());
        this.save(device);
        // 写入一条"新建设备"的操作记录，方便追溯设备初始状态
        DeviceStatusRecord record = new DeviceStatusRecord();
        record.setDeviceId(device.getId());
        record.setDeviceCode(device.getDeviceCode());
        record.setDeviceName(device.getDeviceName());
        record.setBeforeStatus(null);
        record.setAfterStatus(device.getStatus());
        record.setRemark("新建设备，默认状态：" + DeviceStatusEnum.defaultStatus().getName());
        record.setCreateBy(currentUsername());
        record.setCreateTime(new Date());
        deviceStatusRecordService.save(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceStatusRecord changeStatus(DeviceStatusChangeVO changeVO) {
        if (changeVO == null || oConvertUtils.isEmpty(changeVO.getId())) {
            throw new JeecgBootException("设备ID不能为空");
        }
        if (oConvertUtils.isEmpty(changeVO.getTargetStatus())) {
            throw new JeecgBootException("目标状态不能为空");
        }
        Device device = this.getById(changeVO.getId());
        if (device == null) {
            throw new JeecgBootException("未找到对应设备");
        }
        String fromStatus = device.getStatus();
        String toStatus = changeVO.getTargetStatus();
        String remark = changeVO.getRemark();

        // 1. 校验目标状态是否合法
        if (!DeviceStatusEnum.isValidCode(toStatus)) {
            throw new JeecgBootException("目标状态不合法：" + toStatus);
        }
        // 2. 校验状态流转规则（含"报废为终态不能再变更/不能回到在用"的限制）
        String transitionError = DeviceStatusEnum.validateTransition(fromStatus, toStatus);
        if (transitionError != null) {
            throw new JeecgBootException(transitionError);
        }
        // 3. 维修恢复（维修 -> 在用）必须填写说明
        if (DeviceStatusEnum.remarkRequired(fromStatus, toStatus) && oConvertUtils.isEmpty(remark)) {
            throw new JeecgBootException("维修设备恢复为在用时必须填写恢复说明");
        }

        // 4. 更新设备状态
        Device update = new Device();
        update.setId(device.getId());
        update.setStatus(toStatus);
        this.updateById(update);

        // 5. 写入状态流转操作记录
        DeviceStatusRecord record = new DeviceStatusRecord();
        record.setDeviceId(device.getId());
        record.setDeviceCode(device.getDeviceCode());
        record.setDeviceName(device.getDeviceName());
        record.setBeforeStatus(fromStatus);
        record.setAfterStatus(toStatus);
        record.setRemark(remark);
        record.setCreateBy(currentUsername());
        record.setCreateTime(new Date());
        deviceStatusRecordService.save(record);
        log.info("设备[{}]状态由【{}】变更为【{}】，操作人：{}",
                device.getDeviceCode(),
                DeviceStatusEnum.nameOf(fromStatus),
                DeviceStatusEnum.nameOf(toStatus),
                record.getCreateBy());
        return record;
    }

    @Override
    public List<DeviceStatusRecord> listStatusRecords(String deviceId) {
        if (oConvertUtils.isEmpty(deviceId)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<DeviceStatusRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceStatusRecord::getDeviceId, deviceId)
                .orderByDesc(DeviceStatusRecord::getCreateTime)
                .orderByDesc(DeviceStatusRecord::getId);
        return deviceStatusRecordService.list(wrapper);
    }

    /**
     * 获取当前登录人账号（未登录场景，如定时任务，返回 null）
     */
    private String currentUsername() {
        try {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            if (principal instanceof LoginUser) {
                return ((LoginUser) principal).getUsername();
            }
        } catch (Exception e) {
            log.warn("获取当前登录人失败：{}", e.getMessage());
        }
        return null;
    }
}
