package org.jeecg.modules.device.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.device.entity.Device;
import org.jeecg.modules.device.service.IDeviceService;
import org.jeecg.modules.device.vo.DeviceStatusChangeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 设备管理
 *
 * <p>状态流转限制见 {@link org.jeecg.modules.device.enums.DeviceStatusEnum}。</p>
 */
@Slf4j
@Tag(name = "设备管理")
@RestController
@RequestMapping("/device/device")
public class DeviceController extends JeecgController<Device, IDeviceService> {

    @Autowired
    private IDeviceService deviceService;

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
     * 添加设备（默认状态：在用）
     */
    @AutoLog(value = "设备-新增")
    @Operation(summary = "设备-新增（默认在用）")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Device device) {
        deviceService.addDevice(device);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑设备基础信息（不能通过该接口改状态）
     */
    @AutoLog(value = "设备-编辑", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "设备-编辑（不含状态变更）")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> edit(@RequestBody Device device) {
        deviceService.editDevice(device);
        return Result.OK("编辑成功!");
    }

    /**
     * 设备状态流转（维修/停用/报废/恢复），带流转校验与操作记录
     */
    @AutoLog(value = "设备-状态变更", operateType = CommonConstant.OPERATE_TYPE_3)
    @Operation(summary = "设备-状态变更（带流转限制与操作记录）")
    @PostMapping(value = "/changeStatus")
    public Result<?> changeStatus(@RequestBody DeviceStatusChangeVO vo) {
        deviceService.changeStatus(vo);
        return Result.OK("状态变更成功！");
    }

    /**
     * 通过id删除（已报废设备同样可删除基础数据；如需保留实物档案可由业务侧限制）
     */
    @AutoLog(value = "设备-删除")
    @Operation(summary = "设备-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        deviceService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     */
    @AutoLog(value = "设备-批量删除")
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
    public Result<?> queryById(@RequestParam(name = "id") String id) {
        Device device = deviceService.getById(id);
        return Result.OK(device);
    }

    /**
     * 导出excel
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Device device) {
        return super.exportXls(request, device, Device.class, "设备列表");
    }

    /**
     * 通过excel导入数据
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Device.class);
    }
}
