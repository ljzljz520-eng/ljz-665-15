package org.jeecg.modules.device.enums;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备状态流转规则单元测试
 */
class DeviceStatusEnumTest {

    @Test
    void testDefaultStatusIsInUse() {
        // 新建设备默认在用
        assertEquals("1", DeviceStatusEnum.IN_USE.getCode());
        assertEquals("在用", DeviceStatusEnum.IN_USE.getName());
    }

    @Test
    void testInUseTransitions() {
        Set<DeviceStatusEnum> targets = DeviceStatusEnum.IN_USE.allowedTargets();
        assertEquals(3, targets.size());
        assertTrue(targets.contains(DeviceStatusEnum.REPAIR));
        assertTrue(targets.contains(DeviceStatusEnum.DISABLED));
        assertTrue(targets.contains(DeviceStatusEnum.SCRAPPED));
        // 在用不能“流转到在用”
        assertFalse(targets.contains(DeviceStatusEnum.IN_USE));
    }

    @Test
    void testRepairTransitionsAndResumeRequiresRemark() {
        Set<DeviceStatusEnum> targets = DeviceStatusEnum.REPAIR.allowedTargets();
        assertTrue(targets.contains(DeviceStatusEnum.IN_USE));
        assertTrue(targets.contains(DeviceStatusEnum.DISABLED));
        assertTrue(targets.contains(DeviceStatusEnum.SCRAPPED));
        // 维修恢复在用
        assertTrue(DeviceStatusEnum.REPAIR.isRepairResume(DeviceStatusEnum.IN_USE));
        // 其它流转不算“维修恢复”
        assertFalse(DeviceStatusEnum.IN_USE.isRepairResume(DeviceStatusEnum.REPAIR));
        assertFalse(DeviceStatusEnum.REPAIR.isRepairResume(DeviceStatusEnum.SCRAPPED));
    }

    @Test
    void testDisabledTransitions() {
        Set<DeviceStatusEnum> targets = DeviceStatusEnum.DISABLED.allowedTargets();
        assertTrue(targets.contains(DeviceStatusEnum.IN_USE));
        assertTrue(targets.contains(DeviceStatusEnum.REPAIR));
        assertTrue(targets.contains(DeviceStatusEnum.SCRAPPED));
        assertFalse(targets.contains(DeviceStatusEnum.DISABLED));
    }

    @Test
    void testScrappedIsTerminal() {
        // 报废为终态：不能再回到在用，也不能变更为任何状态
        assertTrue(DeviceStatusEnum.SCRAPPED.allowedTargets().isEmpty());
        assertFalse(DeviceStatusEnum.SCRAPPED.canTransitTo(DeviceStatusEnum.IN_USE));
        assertFalse(DeviceStatusEnum.SCRAPPED.canTransitTo(DeviceStatusEnum.REPAIR));
        assertFalse(DeviceStatusEnum.SCRAPPED.canTransitTo(DeviceStatusEnum.DISABLED));
        assertFalse(DeviceStatusEnum.SCRAPPED.canTransitTo(DeviceStatusEnum.SCRAPPED));
    }

    @Test
    void testGetByCode() {
        assertEquals(DeviceStatusEnum.REPAIR, DeviceStatusEnum.getByCode("2"));
        assertNull(DeviceStatusEnum.getByCode(null));
        assertNull(DeviceStatusEnum.getByCode("99"));
    }
}
