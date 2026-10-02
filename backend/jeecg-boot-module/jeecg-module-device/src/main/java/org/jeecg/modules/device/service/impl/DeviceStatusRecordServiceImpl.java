package org.jeecg.modules.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.device.entity.DeviceStatusRecord;
import org.jeecg.modules.device.mapper.DeviceStatusRecordMapper;
import org.jeecg.modules.device.service.IDeviceStatusRecordService;
import org.springframework.stereotype.Service;

/**
 * @Description: 设备状态流转记录
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Service
public class DeviceStatusRecordServiceImpl extends ServiceImpl<DeviceStatusRecordMapper, DeviceStatusRecord> implements IDeviceStatusRecordService {

}
