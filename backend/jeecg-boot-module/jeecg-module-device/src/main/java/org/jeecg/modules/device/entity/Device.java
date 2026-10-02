package org.jeecg.modules.device.entity;

import java.io.Serializable;
import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @Description: 设备信息
 * @Author: jeecg-boot
 * @Date: 2026-10-02
 * @Version: V1.0
 */
@Data
@TableName("device")
public class Device implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 设备编号（唯一） */
    @Excel(name = "设备编号", width = 20)
    private String deviceCode;

    /** 设备名称 */
    @Excel(name = "设备名称", width = 20)
    private String deviceName;

    /** 设备型号 */
    @Excel(name = "设备型号", width = 20)
    private String deviceModel;

    /**
     * 设备状态：1-在用 2-维修 3-停用 4-报废
     * 新建设备默认 1-在用，状态变更只能通过状态流转接口完成
     */
    @Excel(name = "设备状态", width = 15, replace = {"在用_1", "维修_2", "停用_3", "报废_4"})
    private String status;

    /** 存放位置 */
    @Excel(name = "存放位置", width = 20)
    private String location;

    /** 备注 */
    private String remark;

    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新人 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
