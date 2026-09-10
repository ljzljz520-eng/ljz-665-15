package org.jeecg.modules.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.system.base.entity.JeecgEntity;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 设备状态流转操作记录（每次状态变化写入一条，用于追溯）
 * @Author: device
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "设备状态流转记录")
@TableName("biz_device_status_record")
public class DeviceStatusRecord extends JeecgEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @Schema(description = "设备ID")
    private String deviceId;

    /**
     * 设备编号（冗余，便于记录直接阅读）
     */
    @Excel(name = "设备编号", width = 20)
    @Schema(description = "设备编号")
    private String deviceCode;

    /**
     * 设备名称（冗余）
     */
    @Excel(name = "设备名称", width = 25)
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 变更前状态：1-在用 2-维修中 3-停用 4-报废；新建时为空
     */
    @Excel(name = "变更前状态", width = 12, replace = {"在用_1", "维修中_2", "停用_3", "报废_4"})
    @Schema(description = "变更前状态")
    private String fromStatus;

    /**
     * 变更后状态：1-在用 2-维修中 3-停用 4-报废
     */
    @Excel(name = "变更后状态", width = 12, replace = {"在用_1", "维修中_2", "停用_3", "报废_4"})
    @Schema(description = "变更后状态")
    private String toStatus;

    /**
     * 操作说明（维修恢复在用时必填）
     */
    @Excel(name = "操作说明", width = 40)
    @Schema(description = "操作说明")
    private String remark;

    /**
     * 操作时间（create_time 的冗余，方便按业务字段查询展示）
     */
    @Excel(name = "操作时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "操作时间")
    private Date operateTime;
}
