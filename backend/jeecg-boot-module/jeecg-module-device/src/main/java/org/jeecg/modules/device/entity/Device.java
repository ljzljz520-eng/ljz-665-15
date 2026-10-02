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

/**
 * 设备
 *
 * <p>设备状态（字典 device_status）：1-在用（默认）、2-维修、3-停用、4-报废。
 * 状态不允许通过普通编辑接口修改，只能通过专用的状态流转接口变更，变更会写入操作记录。</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "设备")
@TableName("device_info")
public class Device extends JeecgEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 设备编号 */
    @Excel(name = "设备编号", width = 20)
    @Schema(description = "设备编号")
    private String deviceCode;

    /** 设备名称 */
    @Excel(name = "设备名称", width = 25)
    @Schema(description = "设备名称")
    private String deviceName;

    /** 设备型号 */
    @Excel(name = "设备型号", width = 20)
    @Schema(description = "设备型号")
    private String deviceModel;

    /** 设备类型 */
    @Excel(name = "设备类型", width = 15, dicCode = "device_type")
    @Schema(description = "设备类型（字典 device_type）")
    private String deviceType;

    /** 状态：1-在用 2-维修 3-停用 4-报废 */
    @Excel(name = "状态", width = 12, dicCode = "device_status")
    @Schema(description = "状态：1-在用 2-维修 3-停用 4-报废")
    private String status;

    /** 存放位置 */
    @Excel(name = "存放位置", width = 25)
    @Schema(description = "存放位置")
    private String location;

    /** 责任人 */
    @Excel(name = "责任人", width = 15)
    @Schema(description = "责任人")
    private String owner;

    /** 启用日期 */
    @Excel(name = "启用日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "启用日期")
    private java.util.Date useDate;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
