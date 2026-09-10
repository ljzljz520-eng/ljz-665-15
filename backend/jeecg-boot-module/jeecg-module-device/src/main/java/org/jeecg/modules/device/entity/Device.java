package org.jeecg.modules.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.system.base.entity.JeecgEntity;
import org.jeecg.modules.device.enums.DeviceStatusEnum;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 设备台账
 * @Author: device
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "设备台账")
@TableName("biz_device")
public class Device extends JeecgEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备编号（唯一）
     */
    @Excel(name = "设备编号", width = 20)
    @Schema(description = "设备编号")
    private String deviceCode;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称", width = 25)
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备型号
     */
    @Excel(name = "设备型号", width = 20)
    @Schema(description = "设备型号")
    private String model;

    /**
     * 设备状态：1-在用 2-维修中 3-停用 4-报废（见 DeviceStatusEnum）
     */
    @Excel(name = "设备状态", width = 12, replace = {"在用_1", "维修中_2", "停用_3", "报废_4"})
    @Schema(description = "设备状态：1-在用 2-维修中 3-停用 4-报废")
    private String status;

    /**
     * 启用日期
     */
    @Excel(name = "启用日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "启用日期")
    private Date useDate;

    /**
     * 存放位置
     */
    @Excel(name = "存放位置", width = 20)
    @Schema(description = "存放位置")
    private String location;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 30)
    @Schema(description = "备注")
    private String remark;

    /**
     * 状态中文名（仅用于页面展示，不入库）
     */
    @Excel(name = "状态名称", width = 12)
    @Schema(description = "状态名称（展示用）")
    private transient String statusText;

    public String getStatusText() {
        DeviceStatusEnum statusEnum = DeviceStatusEnum.getByCode(this.status);
        return statusEnum == null ? null : statusEnum.getName();
    }
}
