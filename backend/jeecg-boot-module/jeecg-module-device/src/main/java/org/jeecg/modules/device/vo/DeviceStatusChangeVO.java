package org.jeecg.modules.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备状态变更请求参数
 *
 * @Author: jeecg-boot
 */
@Data
@Schema(description = "设备状态变更请求")
public class DeviceStatusChangeVO {

    /** 设备ID */
    @Schema(description = "设备ID")
    private String id;

    /** 目标状态：1-在用 2-维修 3-停用 4-报废 */
    @Schema(description = "目标状态：1-在用 2-维修 3-停用 4-报废")
    private String targetStatus;

    /** 变更说明（维修恢复为在用时必填） */
    @Schema(description = "变更说明（维修恢复时必填）")
    private String remark;
}
