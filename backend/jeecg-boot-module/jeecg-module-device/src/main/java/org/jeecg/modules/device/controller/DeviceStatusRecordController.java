package org.jeecg.modules.device.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.device.entity.DeviceStatusRecord;
import org.jeecg.modules.device.service.IDeviceStatusRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 设备状态操作记录（追溯）
 */
@Slf4j
@Tag(name = "设备状态操作记录")
@RestController
@RequestMapping("/device/statusRecord")
public class DeviceStatusRecordController
        extends JeecgController<DeviceStatusRecord, IDeviceStatusRecordService> {

    @Autowired
    private IDeviceStatusRecordService statusRecordService;

    /**
     * 分页查询某台设备（或全部）的状态操作记录，按操作时间倒序
     */
    @Operation(summary = "设备状态操作记录-分页查询")
    @GetMapping(value = "/list")
    public Result<?> list(DeviceStatusRecord record,
                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                          HttpServletRequest req) {
        QueryWrapper<DeviceStatusRecord> queryWrapper =
                QueryGenerator.initQueryWrapper(record, req.getParameterMap());
        queryWrapper.orderByDesc("operate_time", "create_time");
        Page<DeviceStatusRecord> page = new Page<>(pageNo, pageSize);
        IPage<DeviceStatusRecord> pageList = statusRecordService.page(page, queryWrapper);
        return Result.OK(pageList);
    }
}
