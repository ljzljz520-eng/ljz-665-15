package org.jeecg.modules.device.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * 设备状态枚举。
 *
 * <p>状态流转规则：</p>
 * <ul>
 *     <li>新建设备默认 {@link #IN_USE}（在用）；</li>
 *     <li>在用 / 停用 可转为 维修、停用、报废；</li>
 *     <li>维修 只能恢复为 在用，且必须填写恢复说明；</li>
 *     <li>报废 为终态，不能再变更（不能再回到在用）。</li>
 * </ul>
 */
public enum DeviceStatusEnum {

    /** 在用 */
    IN_USE("1", "在用"),
    /** 维修 */
    REPAIR("2", "维修"),
    /** 停用 */
    DISABLED("3", "停用"),
    /** 报废（终态） */
    SCRAPPED("4", "报废");

    /** 字典值（对应数据字典 device_status） */
    private final String value;
    private final String label;

    DeviceStatusEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 根据字典值获取枚举，非法值抛异常。
     */
    public static DeviceStatusEnum of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("设备状态不能为空");
        }
        for (DeviceStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("非法的设备状态：" + value);
    }

    /**
     * 判断从当前状态是否可以流转到目标状态。
     *
     * @param target 目标状态
     * @return true 表示允许流转
     */
    public boolean canTransferTo(DeviceStatusEnum target) {
        if (target == null) {
            return false;
        }
        if (this == target) {
            // 不允许“原地变更”
            return false;
        }
        switch (this) {
            case IN_USE:
            case DISABLED:
                // 在用 / 停用 -> 维修、停用（仅在用时）、报废、在用（仅停用时）
                return target == REPAIR || target == SCRAPPED
                        || (this == IN_USE && target == DISABLED)
                        || (this == DISABLED && target == IN_USE);
            case REPAIR:
                // 维修 -> 在用（恢复，须填说明）
                return target == IN_USE;
            case SCRAPPED:
                // 报废是终态
                return false;
            default:
                return false;
        }
    }

    /**
     * 当前状态可选的目标状态（用于前端渲染可用操作）。
     */
    public Set<DeviceStatusEnum> allowedTargets() {
        Set<DeviceStatusEnum> targets = EnumSet.noneOf(DeviceStatusEnum.class);
        for (DeviceStatusEnum target : values()) {
            if (canTransferTo(target)) {
                targets.add(target);
            }
        }
        return targets;
    }
}
