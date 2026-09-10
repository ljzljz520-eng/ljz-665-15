package org.jeecg.modules.device.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * @Description: 设备状态枚举及流转规则
 *
 * 流转规则：
 * 在用（IN_USE）   -> 维修中、停用、报废
 * 维修中（REPAIR） -> 在用（恢复时必须填写说明）、停用、报废
 * 停用（DISABLED）-> 在用、维修中、报废
 * 报废（SCRAPPED）-> 终态，不允许再变更
 *
 * @Author: device
 * @Date: 2026-09-10
 * @Version: V1.0
 */
public enum DeviceStatusEnum {

    /**
     * 在用（新建设备的默认状态）
     */
    IN_USE("1", "在用"),

    /**
     * 维修中
     */
    REPAIR("2", "维修中"),

    /**
     * 停用
     */
    DISABLED("3", "停用"),

    /**
     * 报废（终态）
     */
    SCRAPPED("4", "报废");

    /**
     * 状态码（入库值）
     */
    private final String code;

    /**
     * 状态中文名
     */
    private final String name;

    DeviceStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据状态码获取枚举，非法状态码返回 null
     */
    public static DeviceStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (DeviceStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 当前状态允许流转到的目标状态集合
     */
    public Set<DeviceStatusEnum> allowedTargets() {
        switch (this) {
            case IN_USE:
                return EnumSet.of(REPAIR, DISABLED, SCRAPPED);
            case REPAIR:
                return EnumSet.of(IN_USE, DISABLED, SCRAPPED);
            case DISABLED:
                return EnumSet.of(IN_USE, REPAIR, SCRAPPED);
            case SCRAPPED:
            default:
                // 报废为终态，不允许任何流转
                return EnumSet.noneOf(DeviceStatusEnum.class);
        }
    }

    /**
     * 判断是否允许流转到目标状态
     */
    public boolean canTransitTo(DeviceStatusEnum target) {
        return target != null && allowedTargets().contains(target);
    }

    /**
     * 判断目标流转是否为“维修恢复在用”
     * （唯一强制要求填写说明的流转）
     */
    public boolean isRepairResume(DeviceStatusEnum target) {
        return this == REPAIR && target == IN_USE;
    }
}
