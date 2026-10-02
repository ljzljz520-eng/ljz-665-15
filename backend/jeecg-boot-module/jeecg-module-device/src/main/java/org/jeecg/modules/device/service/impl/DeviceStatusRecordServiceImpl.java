package org.jeecg.modules.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.device.entity.DeviceStatusRecord;
import org.jeecg.modules.device.mapper.DeviceStatusRecordMapper;
import org.jeecg.modules.device.service.IDeviceStatusRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 设备状态操作记录 Service 实现
 */
@Service
public class DeviceStatusRecordServiceImpl
        extends ServiceImpl<DeviceStatusRecordMapper, DeviceStatusRecord>
        implements IDeviceStatusRecordService {

    @Override
    public void record(String deviceId, String beforeStatus, String afterStatus, String operateType, String remark) {
        DeviceStatusRecord record = new DeviceStatusRecord()
                .setDeviceId(deviceId)
                .setBeforeStatus(beforeStatus)
                .setAfterStatus(afterStatus)
                .setOperateType(operateType)
                .setRemark(remark)
                .setOperateTime(new Date());
        this.save(record);
    }
}
