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
 * 设备状态操作记录
 *
 * <p>每次设备状态变化（含新建设备的初始状态）都会写入一条记录，用于后续追溯。
 * create_by / create_time 由框架自动填充为操作人和操作时间。</p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "设备状态操作记录")
@TableName("device_status_record")
public class DeviceStatusRecord extends JeecgEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 设备ID */
    @Schema(description = "设备ID")
    private String deviceId;

    /** 变更前状态：1-在用 2-维修 3-停用 4-报废，新建时为空 */
    @Excel(name = "变更前状态", width = 12, dicCode = "device_status")
    @Schema(description = "变更前状态，新建时为空")
    private String beforeStatus;

    /** 变更后状态：1-在用 2-维修 3-停用 4-报废 */
    @Excel(name = "变更后状态", width = 12, dicCode = "device_status")
    @Schema(description = "变更后状态")
    private String afterStatus;

    /** 操作类型：CREATE-新建入库 CHANGE-状态流转 */
    @Excel(name = "操作类型", width = 12)
    @Schema(description = "操作类型：CREATE-新建入库 CHANGE-状态流转")
    private String operateType;

    /** 变更说明（维修恢复为在用时必填） */
    @Excel(name = "变更说明", width = 50)
    @Schema(description = "变更说明")
    private String remark;

    /** 操作时间（冗余展示用，等于创建时间） */
    @Excel(name = "操作时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "操作时间")
    private Date operateTime;
}
