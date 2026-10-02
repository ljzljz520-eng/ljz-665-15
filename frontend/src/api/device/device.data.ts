/**
 * 设备状态枚举
 * 与后端 org.jeecg.modules.device.enums.DeviceStatusEnum 保持一致
 */
export const deviceStatusMap: Record<string, { text: string; color: string }> = {
  '1': { text: '在用', color: 'green' },
  '2': { text: '维修', color: 'orange' },
  '3': { text: '停用', color: 'default' },
  '4': { text: '报废', color: 'red' },
};

/** 报废为终态 */
export const DEVICE_STATUS_SCRAPPED = '4';
/** 维修 */
export const DEVICE_STATUS_REPAIR = '2';
/** 在用 */
export const DEVICE_STATUS_IN_USE = '1';

export function deviceStatusText(status?: string) {
  if (!status) return '';
  return deviceStatusMap[status]?.text ?? status;
}

export function deviceStatusColor(status?: string) {
  if (!status) return 'default';
  return deviceStatusMap[status]?.color ?? 'default';
}
