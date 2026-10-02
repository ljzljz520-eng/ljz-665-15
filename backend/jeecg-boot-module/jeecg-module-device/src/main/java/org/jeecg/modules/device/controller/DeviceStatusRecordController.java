package org.jeecg.modules.device.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.device.entity.DeviceStatusRecord;
import org.jeecg.modules.device.service.IDeviceStatusRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
 * @Description: 设备状态流转记录（操作记录）
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Tag(name = "设备状态流转记录")
@RestController
@RequestMapping("/device/statusRecord")
@Slf4j
public class DeviceStatusRecordController {

    @Autowired
    private IDeviceStatusRecordService deviceStatusRecordService;

    /**
     * 分页查询设备状态流转记录，默认按操作时间倒序，方便追溯
     */
    @Operation(summary = "设备状态流转记录-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<DeviceStatusRecord>> queryPageList(DeviceStatusRecord record,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                           HttpServletRequest req) {
        QueryWrapper<DeviceStatusRecord> queryWrapper = QueryGenerator.initQueryWrapper(record, req.getParameterMap());
        queryWrapper.orderByDesc("create_time");
        Page<DeviceStatusRecord> page = new Page<>(pageNo, pageSize);
        return Result.OK(deviceStatusRecordService.page(page, queryWrapper));
    }
}
