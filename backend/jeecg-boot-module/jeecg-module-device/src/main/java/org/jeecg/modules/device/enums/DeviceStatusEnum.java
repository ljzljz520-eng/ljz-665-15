package org.jeecg.modules.device.enums;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 设备状态枚举
 *
 * 状态流转规则：
 * 1. 新建设备默认"在用"；
 * 2. 在用/停用 可以转为 维修、停用、报废；
 * 3. 维修 可以恢复为"在用"（恢复时必须填写说明），也可以转为停用、报废；
 * 4. 报废为终态，报废设备不能再回到在用，也不能再变更为其他状态。
 *
 * @Author: jeecg-boot
 */
public enum DeviceStatusEnum {

    /**
     * 在用
     */
    IN_USE("1", "在用"),
    /**
     * 维修
     */
    REPAIR("2", "维修"),
    /**
     * 停用
     */
    DISABLED("3", "停用"),
    /**
     * 报废（终态）
     */
    SCRAPPED("4", "报废");

    /**
     * 状态编码（存入数据库 status 字段）
     */
    private final String code;
    /**
     * 状态名称
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
     * 新建设备的默认状态：在用
     */
    public static DeviceStatusEnum defaultStatus() {
        return IN_USE;
    }

    public static DeviceStatusEnum byCode(String code) {
        if (code == null) {
            return null;
        }
        for (DeviceStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static boolean isValidCode(String code) {
        return byCode(code) != null;
    }

    public static String nameOf(String code) {
        DeviceStatusEnum status = byCode(code);
        return status == null ? code : status.name;
    }

    /**
     * 允许流转到的目标状态集合
     */
    public Set<DeviceStatusEnum> allowedTargets() {
        switch (this) {
            case IN_USE:
                // 在用 -> 维修 / 停用 / 报废
                return EnumSet.of(REPAIR, DISABLED, SCRAPPED);
            case REPAIR:
                // 维修 -> 在用（恢复，需填写说明）/ 停用 / 报废
                return EnumSet.of(IN_USE, DISABLED, SCRAPPED);
            case DISABLED:
                // 停用 -> 在用 / 维修 / 报废
                return EnumSet.of(IN_USE, REPAIR, SCRAPPED);
            case SCRAPPED:
                // 报废为终态，不允许再变更
                return EnumSet.noneOf(DeviceStatusEnum.class);
            default:
                return EnumSet.noneOf(DeviceStatusEnum.class);
        }
    }

    /**
     * 校验状态流转是否合法，不合法时返回错误提示；合法返回 null。
     *
     * @param fromCode 当前状态编码
     * @param toCode   目标状态编码
     */
    public static String validateTransition(String fromCode, String toCode) {
        DeviceStatusEnum from = byCode(fromCode);
        DeviceStatusEnum to = byCode(toCode);
        if (from == null) {
            return "当前设备状态不合法：" + fromCode;
        }
        if (to == null) {
            return "目标设备状态不合法：" + toCode;
        }
        if (from == to) {
            return "设备当前已是【" + from.name + "】状态，无需重复变更";
        }
        if (from == SCRAPPED) {
            return "设备已报废，报废为终态，不能再变更状态（不能恢复为在用）";
        }
        if (!from.allowedTargets().contains(to)) {
            return "不允许将设备状态从【" + from.name + "】变更为【" + to.name + "】";
        }
        return null;
    }

    /**
     * 获取某状态可流转的目标状态编码列表（供前端控制可选操作）
     */
    public static List<String> allowedTargetCodes(String fromCode) {
        DeviceStatusEnum from = byCode(fromCode);
        if (from == null) {
            return Arrays.stream(values()).map(DeviceStatusEnum::getCode).collect(Collectors.toList());
        }
        return from.allowedTargets().stream().map(DeviceStatusEnum::getCode).collect(Collectors.toList());
    }

    /**
     * 维修恢复（维修 -> 在用）时是否必须填写说明
     */
    public static boolean remarkRequired(String fromCode, String toCode) {
        return REPAIR.code.equals(fromCode) && IN_USE.code.equals(toCode);
    }
}
