package org.jeecg.modules.device.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 设备状态流转规则单元测试
 */
class DeviceStatusEnumTest {

    @Test
    void inUseCanTransferToRepairDisabledScrapped() {
        assertTrue(DeviceStatusEnum.IN_USE.canTransferTo(DeviceStatusEnum.REPAIR));
        assertTrue(DeviceStatusEnum.IN_USE.canTransferTo(DeviceStatusEnum.DISABLED));
        assertTrue(DeviceStatusEnum.IN_USE.canTransferTo(DeviceStatusEnum.SCRAPPED));
        // 在用不能“转成”在用
        assertFalse(DeviceStatusEnum.IN_USE.canTransferTo(DeviceStatusEnum.IN_USE));
    }

    @Test
    void repairCanOnlyRestoreToInUse() {
        assertTrue(DeviceStatusEnum.REPAIR.canTransferTo(DeviceStatusEnum.IN_USE));
        assertFalse(DeviceStatusEnum.REPAIR.canTransferTo(DeviceStatusEnum.DISABLED));
        assertFalse(DeviceStatusEnum.REPAIR.canTransferTo(DeviceStatusEnum.SCRAPPED));
        assertFalse(DeviceStatusEnum.REPAIR.canTransferTo(DeviceStatusEnum.REPAIR));
    }

    @Test
    void scrappedIsTerminal() {
        for (DeviceStatusEnum target : DeviceStatusEnum.values()) {
            assertFalse(DeviceStatusEnum.SCRAPPED.canTransferTo(target),
                    "报废设备不能流转到 " + target);
        }
    }

    @Test
    void disabledCanReuseRepairAndScrap() {
        assertTrue(DeviceStatusEnum.DISABLED.canTransferTo(DeviceStatusEnum.IN_USE));
        assertTrue(DeviceStatusEnum.DISABLED.canTransferTo(DeviceStatusEnum.REPAIR));
        assertTrue(DeviceStatusEnum.DISABLED.canTransferTo(DeviceStatusEnum.SCRAPPED));
    }

    @Test
    void ofRejectsIllegalValue() {
        assertThrows(IllegalArgumentException.class, () -> DeviceStatusEnum.of(null));
        assertThrows(IllegalArgumentException.class, () -> DeviceStatusEnum.of("9"));
        assertEquals(DeviceStatusEnum.IN_USE, DeviceStatusEnum.of("1"));
    }
}
