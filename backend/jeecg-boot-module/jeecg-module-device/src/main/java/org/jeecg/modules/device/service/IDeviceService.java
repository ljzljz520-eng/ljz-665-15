package org.jeecg.modules.device.service;

import org.jeecg.common.system.base.service.JeecgService;
import org.jeecg.modules.device.entity.Device;
import org.jeecg.modules.device.vo.DeviceStatusChangeVO;

/**
 * 设备 Service
 */
public interface IDeviceService extends JeecgService<Device> {

    /**
     * 新建设备。默认状态为“在用”，并写入一条初始状态记录。
     *
     * @param device 设备信息
     */
    void addDevice(Device device);

    /**
     * 编辑设备基础信息。不允许通过该接口修改状态，修改状态请走
     * {@link #changeStatus(DeviceStatusChangeVO)}。
     *
     * @param device 设备信息
     */
    void editDevice(Device device);

    /**
     * 设备状态流转：
     * <ol>
     *     <li>校验状态流转是否合法（报废为终态、维修仅能恢复为在用等）；</li>
     *     <li>维修恢复为在用时，变更说明必填；</li>
     *     <li>更新设备状态并写入操作记录（同一事务）。</li>
     * </ol>
     *
     * @param vo 状态流转入参
     */
    void changeStatus(DeviceStatusChangeVO vo);
}
