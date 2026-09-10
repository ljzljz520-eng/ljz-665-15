package org.jeecg.modules.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.device.entity.DeviceStatusRecord;
import org.jeecg.modules.device.mapper.DeviceStatusRecordMapper;
import org.jeecg.modules.device.service.IDeviceStatusRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description: 设备状态流转记录 Service 实现
 * @Author: device
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Service
public class DeviceStatusRecordServiceImpl
        extends ServiceImpl<DeviceStatusRecordMapper, DeviceStatusRecord>
        implements IDeviceStatusRecordService {

    @Override
    public void record(String deviceId, String deviceCode, String deviceName,
                       String fromStatus, String toStatus, String remark) {
        Date now = new Date();
        DeviceStatusRecord statusRecord = new DeviceStatusRecord();
        statusRecord.setDeviceId(deviceId);
        statusRecord.setDeviceCode(deviceCode);
        statusRecord.setDeviceName(deviceName);
        statusRecord.setFromStatus(fromStatus);
        statusRecord.setToStatus(toStatus);
        statusRecord.setRemark(remark);
        statusRecord.setOperateTime(now);
        // create_time 与操作时间保持一致，便于追溯
        statusRecord.setCreateTime(now);
        this.save(statusRecord);
    }

    @Override
    public IPage<DeviceStatusRecord> pageByDevice(Integer pageNo, Integer pageSize, String deviceId) {
        LambdaQueryWrapper<DeviceStatusRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceStatusRecord::getDeviceId, deviceId)
                .orderByDesc(DeviceStatusRecord::getOperateTime)
                .orderByDesc(DeviceStatusRecord::getCreateTime);
        return this.page(new Page<>(pageNo, pageSize), queryWrapper);
    }
}
