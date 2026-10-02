package org.jeecg.modules.device.service.impl;

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
import org.springframework.util.StringUtils;

/**
 * 设备 Service 实现
 */
@Service
public class DeviceServiceImpl
        extends ServiceImpl<DeviceMapper, Device>
        implements IDeviceService {

    @Autowired
    private IDeviceStatusRecordService statusRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDevice(Device device) {
        // 新建设备默认在用（忽略前端传入的状态）
        device.setStatus(DeviceStatusEnum.IN_USE.getValue());
        this.save(device);
        // 记录初始状态，方便后续追溯
        statusRecordService.record(device.getId(), null,
                DeviceStatusEnum.IN_USE.getValue(), "CREATE", "新建设备入库，默认状态：在用");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editDevice(Device device) {
        if (device.getId() == null || device.getId().isEmpty()) {
            throw new JeecgBootException("设备ID不能为空");
        }
        Device dbDevice = this.getById(device.getId());
        if (dbDevice == null) {
            throw new JeecgBootException("设备不存在或已被删除");
        }
        // 状态不能通过编辑接口修改，防止绕过流转限制
        if (StringUtils.hasText(device.getStatus()) && !device.getStatus().equals(dbDevice.getStatus())) {
            throw new JeecgBootException("设备状态不允许直接编辑，请使用状态变更功能");
        }
        // 始终以数据库中的状态为准
        device.setStatus(dbDevice.getStatus());
        this.updateById(device);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(DeviceStatusChangeVO vo) {
        if (vo == null || !StringUtils.hasText(vo.getId())) {
            throw new JeecgBootException("设备ID不能为空");
        }
        if (!StringUtils.hasText(vo.getTargetStatus())) {
            throw new JeecgBootException("目标状态不能为空");
        }

        Device device = this.getById(vo.getId());
        if (device == null) {
            throw new JeecgBootException("设备不存在或已被删除");
        }

        DeviceStatusEnum current;
        DeviceStatusEnum target;
        try {
            current = DeviceStatusEnum.of(device.getStatus());
            target = DeviceStatusEnum.of(vo.getTargetStatus());
        } catch (IllegalArgumentException e) {
            throw new JeecgBootException(e.getMessage());
        }

        // 1. 流转限制：不允许的状态变更直接拒绝（报废为终态，不能再回到在用）
        if (!current.canTransferTo(target)) {
            String message;
            if (current == DeviceStatusEnum.SCRAPPED) {
                message = "设备已报废，报废为终态，不能再变更状态";
            } else if (current == DeviceStatusEnum.REPAIR) {
                message = "维修中的设备只能恢复为在用，请使用“恢复在用”操作";
            } else {
                message = String.format("设备状态不允许从【%s】变更为【%s】", current.getLabel(), target.getLabel());
            }
            throw new JeecgBootException(message);
        }

        // 2. 维修设备恢复（维修 -> 在用）时必须填写说明（不能为纯空格）
        String remark = vo.getRemark();
        if (current == DeviceStatusEnum.REPAIR && target == DeviceStatusEnum.IN_USE) {
            if (!StringUtils.hasText(remark)) {
                throw new JeecgBootException("维修设备恢复为在用时必须填写恢复说明");
            }
        }

        // 3. 更新状态并写入操作记录（同一事务，失败一起回滚）
        String beforeStatus = device.getStatus();
        device.setStatus(target.getValue());
        this.updateById(device);
        statusRecordService.record(device.getId(), beforeStatus, target.getValue(), "CHANGE", remark);
    }
}
