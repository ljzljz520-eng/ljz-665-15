package org.jeecg.modules.device.entity;

import java.io.Serializable;
import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @Description: 设备状态流转记录（操作记录，用于追溯）
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Data
@TableName("device_status_record")
public class DeviceStatusRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 设备ID */
    private String deviceId;

    /** 设备编号（冗余，便于记录直接展示） */
    @Excel(name = "设备编号", width = 20)
    private String deviceCode;

    /** 设备名称（冗余） */
    @Excel(name = "设备名称", width = 20)
    private String deviceName;

    /** 变更前状态：1-在用 2-维修 3-停用 4-报废；新建设备为空 */
    @Excel(name = "变更前状态", width = 15, replace = {"在用_1", "维修_2", "停用_3", "报废_4"})
    private String beforeStatus;

    /** 变更后状态：1-在用 2-维修 3-停用 4-报废 */
    @Excel(name = "变更后状态", width = 15, replace = {"在用_1", "维修_2", "停用_3", "报废_4"})
    private String afterStatus;

    /** 变更说明（维修恢复为在用时必填） */
    @Excel(name = "变更说明", width = 40)
    private String remark;

    /** 操作人 */
    @Excel(name = "操作人", width = 15)
    private String createBy;

    /** 操作时间 */
    @Excel(name = "操作时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
