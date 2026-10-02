package org.jeecg.modules.device.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.device.entity.Device;
import org.jeecg.modules.device.entity.DeviceStatusRecord;
import org.jeecg.modules.device.enums.DeviceStatusEnum;
import org.jeecg.modules.device.service.IDeviceService;
import org.jeecg.modules.device.vo.DeviceStatusChangeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 设备信息
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Tag(name = "设备管理")
@RestController
@RequestMapping("/device/device")
@Slf4j
public class DeviceController {

    @Autowired
    private IDeviceService deviceService;

    /**
     * 分页列表查询
     */
    @Operation(summary = "设备-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Device>> queryPageList(Device device,
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
     * 新建设备（默认状态：在用，同时写入一条操作记录）
     */
    @Operation(summary = "设备-新增")
    @PostMapping(value = "/add")
    public Result<Device> add(@RequestBody Device device) {
        // 服务层会强制设置默认状态"在用"，不信任前端传入的 status
        deviceService.addDevice(device);
        return Result.OK("新建设备成功，设备默认状态为【在用】！", device);
    }

    /**
     * 编辑设备基础信息。
     * 注意：状态字段不允许通过普通编辑修改，状态变更必须走 /changeStatus 接口，
     * 以保证状态流转限制和操作记录不被绕过。
     */
    @Operation(summary = "设备-编辑")
    @PutMapping(value = "/edit")
    public Result<Device> edit(@RequestBody Device device) {
        Device exist = deviceService.getById(device.getId());
        if (exist == null) {
            return Result.error("未找到对应设备");
        }
        // 保持原状态不变，忽略请求体中的 status
        device.setStatus(exist.getStatus());
        deviceService.updateById(device);
        return Result.OK("编辑成功！");
    }

    /**
     * 设备状态流转。
     * 规则：报废为终态不能再变更（不能回到在用）；维修恢复为在用必须填写说明；每次变更写入操作记录。
     */
    @Operation(summary = "设备-状态变更")
    @PutMapping(value = "/changeStatus")
    public Result<DeviceStatusRecord> changeStatus(@RequestBody DeviceStatusChangeVO changeVO) {
        DeviceStatusRecord record = deviceService.changeStatus(changeVO);
        return Result.OK("状态变更成功！", record);
    }

    /**
     * 查询某设备当前允许流转的目标状态（供前端按状态显示可用操作按钮）
     */
    @Operation(summary = "设备-可流转的目标状态")
    @GetMapping(value = "/allowedTargetStatus")
    public Result<List<String>> allowedTargetStatus(@RequestParam(name = "status") String status) {
        return Result.OK(DeviceStatusEnum.allowedTargetCodes(status));
    }

    /**
     * 查询设备状态流转记录（操作记录，用于追溯）
     */
    @Operation(summary = "设备-状态流转记录")
    @GetMapping(value = "/statusRecord")
    public Result<List<DeviceStatusRecord>> statusRecord(@RequestParam(name = "id") String id) {
        return Result.OK(deviceService.listStatusRecords(id));
    }

    /**
     * 通过id查询
     */
    @Operation(summary = "设备-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Device> queryById(@RequestParam(name = "id") String id) {
        Device device = deviceService.getById(id);
        if (device == null) {
            return Result.error("未找到对应设备");
        }
        return Result.OK(device);
    }

    /**
     * 通过id删除
     */
    @Operation(summary = "设备-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        deviceService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除
     */
    @Operation(summary = "设备-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        this.deviceService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 状态枚举（供前端下拉/标签使用）
     */
    @Operation(summary = "设备-状态枚举")
    @GetMapping(value = "/statusOptions")
    public Result<List<Map<String, String>>> statusOptions() {
        List<Map<String, String>> list = Arrays.stream(DeviceStatusEnum.values()).map(e -> {
            Map<String, String> m = new HashMap<>(4);
            m.put("value", e.getCode());
            m.put("text", e.getName());
            return m;
        }).collect(java.util.stream.Collectors.toList());
        return Result.OK(list);
    }
}
