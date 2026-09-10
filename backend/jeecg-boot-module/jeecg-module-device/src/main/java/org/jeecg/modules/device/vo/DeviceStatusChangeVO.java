package org.jeecg.modules.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 设备状态变更请求参数
 * @Author: device
 * @Date: 2026-09-10
 * @Version: V1.0
 */
@Data
@Schema(description = "设备状态变更请求参数")
public class DeviceStatusChangeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @Schema(description = "设备ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    /**
     * 目标状态：1-在用 2-维修中 3-停用 4-报废
     */
    @Schema(description = "目标状态：1-在用 2-维修中 3-停用 4-报废", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    /**
     * 操作说明（维修中恢复在用时必填）
     */
    @Schema(description = "操作说明（维修恢复在用时必填）")
    private String remark;
}
