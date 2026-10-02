package org.jeecg.modules.device.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 设备状态流转规则单元测试
 */
class DeviceStatusEnumTest {

    private static final String IN_USE = DeviceStatusEnum.IN_USE.getCode();
    private static final String REPAIR = DeviceStatusEnum.REPAIR.getCode();
    private static final String DISABLED = DeviceStatusEnum.DISABLED.getCode();
    private static final String SCRAPPED = DeviceStatusEnum.SCRAPPED.getCode();

    @Test
    void defaultStatusIsInUse() {
        assertEquals(IN_USE, DeviceStatusEnum.defaultStatus().getCode());
    }

    @Test
    void legalTransitions() {
        // 在用 -> 维修 / 停用 / 报废
        assertNull(DeviceStatusEnum.validateTransition(IN_USE, REPAIR));
        assertNull(DeviceStatusEnum.validateTransition(IN_USE, DISABLED));
        assertNull(DeviceStatusEnum.validateTransition(IN_USE, SCRAPPED));
        // 维修 -> 在用 / 停用 / 报废
        assertNull(DeviceStatusEnum.validateTransition(REPAIR, IN_USE));
        assertNull(DeviceStatusEnum.validateTransition(REPAIR, DISABLED));
        assertNull(DeviceStatusEnum.validateTransition(REPAIR, SCRAPPED));
        // 停用 -> 在用 / 维修 / 报废
        assertNull(DeviceStatusEnum.validateTransition(DISABLED, IN_USE));
        assertNull(DeviceStatusEnum.validateTransition(DISABLED, REPAIR));
        assertNull(DeviceStatusEnum.validateTransition(DISABLED, SCRAPPED));
    }

    @Test
    void scrappedIsTerminalAndCannotReturnToInUse() {
        // 报废 -> 任何状态都非法
        assertTrue(DeviceStatusEnum.validateTransition(SCRAPPED, IN_USE).contains("报废"));
        assertTrue(DeviceStatusEnum.validateTransition(SCRAPPED, REPAIR) != null);
        assertTrue(DeviceStatusEnum.validateTransition(SCRAPPED, DISABLED) != null);
        assertTrue(DeviceStatusEnum.validateTransition(SCRAPPED, SCRAPPED) != null);
        assertTrue(DeviceStatusEnum.allowedTargetCodes(SCRAPPED).isEmpty());
    }

    @Test
    void sameStatusTransitionRejected() {
        assertTrue(DeviceStatusEnum.validateTransition(IN_USE, IN_USE) != null);
    }

    @Test
    void invalidStatusCodeRejected() {
        assertFalse(DeviceStatusEnum.isValidCode("99"));
        assertTrue(DeviceStatusEnum.validateTransition(IN_USE, "99") != null);
    }

    @Test
    void remarkRequiredOnlyWhenRepairBackToInUse() {
        assertTrue(DeviceStatusEnum.remarkRequired(REPAIR, IN_USE));
        assertFalse(DeviceStatusEnum.remarkRequired(IN_USE, REPAIR));
        assertFalse(DeviceStatusEnum.remarkRequired(DISABLED, IN_USE));
        assertFalse(DeviceStatusEnum.remarkRequired(IN_USE, SCRAPPED));
    }
}
