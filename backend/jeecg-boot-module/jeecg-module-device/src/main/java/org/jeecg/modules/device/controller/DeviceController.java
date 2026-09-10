package org.jeecg.modules.device.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.device.entity.Device;
import org.jeecg.modules.device.entity.DeviceStatusRecord;
import org.jeecg.modules.device.service.IDeviceService;
import org.jeecg.modules.device.service.IDeviceStatusRecordService;
import org.jeecg.modules.device.vo.DeviceStatusChangeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @Description: 设备台账（含设备状态流转）
 * @Author: device
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "设备管理")
@RestController
@RequestMapping("/device/device")
public class DeviceController extends JeecgController<Device, IDeviceService> {

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IDeviceStatusRecordService deviceStatusRecordService;

    /**
     * 分页列表查询
     */
    @Operation(summary = "设备-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> list(Device device,
                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                          HttpServletRequest req) {
        QueryWrapper<Device> queryWrapper = QueryGenerator.initQueryWrapper(device, req.getParameterMap());
        queryWrapper.orderByDesc("create_time");
        Page<Device> page = new Page<>(pageNo, pageSize);
        IPage<Device> pageList = deviceService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加设备（默认在用，并写入初始化状态记录）
     */
    @AutoLog(value = "设备-新增", operateType = CommonConstant.OPERATE_TYPE_2)
    @Operation(summary = "设备-新增（默认在用）")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Device device) {
        deviceService.addDevice(device);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑设备基础信息（状态不会被该接口修改）
     */
    @AutoLog(value = "设备-编辑", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "设备-编辑（仅基础信息，状态请走状态流转接口）")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> edit(@RequestBody Device device) {
        deviceService.editDevice(device);
        return Result.OK("编辑成功!");
    }

    /**
     * 设备状态流转
     */
    @AutoLog(value = "设备-状态变更", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "设备-状态变更（受流转规则约束，自动写操作记录）")
    @RequestMapping(value = "/changeStatus", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> changeStatus(@RequestBody DeviceStatusChangeVO vo) {
        deviceService.changeStatus(vo);
        return Result.OK("状态变更成功！");
    }

    /**
     * 通过id删除
     */
    @AutoLog(value = "设备-删除", operateType = CommonConstant.OPERATE_TYPE_4)
    @Operation(summary = "设备-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        deviceService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     */
    @AutoLog(value = "设备-批量删除", operateType = CommonConstant.OPERATE_TYPE_4)
    @Operation(summary = "设备-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        this.deviceService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     */
    @Operation(summary = "设备-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@Parameter(name = "id", required = true) @RequestParam(name = "id") String id) {
        Device device = deviceService.getById(id);
        return Result.OK(device);
    }

    /**
     * 查询设备的状态流转操作记录（追溯用）
     */
    @Operation(summary = "设备状态-流转记录分页查询")
    @GetMapping(value = "/statusRecord/list")
    public Result<?> statusRecordList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                      @RequestParam(name = "deviceId") String deviceId) {
        IPage<DeviceStatusRecord> pageList = deviceStatusRecordService.pageByDevice(pageNo, pageSize, deviceId);
        return Result.OK(pageList);
    }
}
