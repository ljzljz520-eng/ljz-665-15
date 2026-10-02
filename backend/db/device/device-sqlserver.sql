-- =============================================================
-- 设备管理模块 - SQL Server 初始化脚本
-- 包含：设备表、设备状态操作记录表、数据字典、菜单与按钮权限
-- 状态：1-在用(默认) 2-维修 3-停用 4-报废
-- 在初始化后的业务库（默认 temp1218）中执行
-- =============================================================

-- ----------------------------
-- 1、设备表
-- ----------------------------
IF OBJECT_ID(N'device_info', N'U') IS NOT NULL DROP TABLE [device_info];
CREATE TABLE [device_info] (
  [id]           NVARCHAR(32)  NOT NULL,
  [device_code]  NVARCHAR(50)  NULL,
  [device_name]  NVARCHAR(100) NULL,
  [device_model] NVARCHAR(100) NULL,
  [device_type]  NVARCHAR(20)  NULL,
  [status]       NVARCHAR(2)   NOT NULL CONSTRAINT [df_device_info_status] DEFAULT (N'1'),
  [location]     NVARCHAR(200) NULL,
  [owner]        NVARCHAR(50)  NULL,
  [use_date]     DATE          NULL,
  [remark]       NVARCHAR(500) NULL,
  [create_by]    NVARCHAR(50)  NULL,
  [create_time]  DATETIME      NULL,
  [update_by]    NVARCHAR(50)  NULL,
  [update_time]  DATETIME      NULL,
  CONSTRAINT [pk_device_info] PRIMARY KEY ([id])
);
CREATE UNIQUE INDEX [uk_device_code] ON [device_info] ([device_code]);
CREATE INDEX [idx_device_status] ON [device_info] ([status]);
GO

-- ----------------------------
-- 2、设备状态操作记录表（状态追溯）
-- ----------------------------
IF OBJECT_ID(N'device_status_record', N'U') IS NOT NULL DROP TABLE [device_status_record];
CREATE TABLE [device_status_record] (
  [id]            NVARCHAR(32)  NOT NULL,
  [device_id]     NVARCHAR(32)  NOT NULL,
  [before_status] NVARCHAR(2)   NULL,
  [after_status]  NVARCHAR(2)   NOT NULL,
  [operate_type]  NVARCHAR(10)  NOT NULL,
  [remark]        NVARCHAR(500) NULL,
  [operate_time]  DATETIME      NULL,
  [create_by]     NVARCHAR(50)  NULL,
  [create_time]   DATETIME      NULL,
  [update_by]     NVARCHAR(50)  NULL,
  [update_time]   DATETIME      NULL,
  CONSTRAINT [pk_device_status_record] PRIMARY KEY ([id])
);
CREATE INDEX [idx_dsr_device_id] ON [device_status_record] ([device_id]);
CREATE INDEX [idx_dsr_operate_time] ON [device_status_record] ([operate_time]);
GO

-- ----------------------------
-- 3、数据字典：设备状态 device_status
-- ----------------------------
IF NOT EXISTS (SELECT 1 FROM [sys_dict] WHERE [dict_code] = N'device_status')
INSERT INTO [sys_dict] ([id],[dict_name],[dict_code],[description],[del_flag],[create_by],[create_time],[type],[tenant_id])
VALUES (N'a0000000000000000000000000000001', N'设备状态', N'device_status', N'设备状态：1在用 2维修 3停用 4报废', 0, N'admin', GETDATE(), 0, 0);

INSERT INTO [sys_dict_item] ([id],[dict_id],[item_text],[item_value],[item_color],[description],[sort_order],[status],[create_by],[create_time])
VALUES
(N'a0000000000000000000000000000011', N'a0000000000000000000000000000001', N'在用', N'1', N'green',   NULL,          1, 1, N'admin', GETDATE()),
(N'a0000000000000000000000000000012', N'a0000000000000000000000000000001', N'维修', N'2', N'orange',  NULL,          2, 1, N'admin', GETDATE()),
(N'a0000000000000000000000000000013', N'a0000000000000000000000000000001', N'停用', N'3', N'default', NULL,          3, 1, N'admin', GETDATE()),
(N'a0000000000000000000000000000014', N'a0000000000000000000000000000001', N'报废', N'4', N'red',     N'终态，不能再变更', 4, 1, N'admin', GETDATE());
GO

-- ----------------------------
-- 4、数据字典：设备类型 device_type（示例项）
-- ----------------------------
IF NOT EXISTS (SELECT 1 FROM [sys_dict] WHERE [dict_code] = N'device_type')
INSERT INTO [sys_dict] ([id],[dict_name],[dict_code],[description],[del_flag],[create_by],[create_time],[type],[tenant_id])
VALUES (N'a0000000000000000000000000000002', N'设备类型', N'device_type', N'设备类型字典', 0, N'admin', GETDATE(), 0, 0);

INSERT INTO [sys_dict_item] ([id],[dict_id],[item_text],[item_value],[item_color],[description],[sort_order],[status],[create_by],[create_time])
VALUES
(N'a0000000000000000000000000000021', N'a0000000000000000000000000000002', N'生产设备', N'1', N'blue',   NULL, 1, 1, N'admin', GETDATE()),
(N'a0000000000000000000000000000022', N'a0000000000000000000000000000002', N'检测设备', N'2', N'cyan',   NULL, 2, 1, N'admin', GETDATE()),
(N'a0000000000000000000000000000023', N'a0000000000000000000000000000002', N'办公设备', N'3', N'purple', NULL, 3, 1, N'admin', GETDATE());
GO

-- ----------------------------
-- 5、菜单与按钮权限（admin 超管自动拥有全部权限）
-- ----------------------------
IF NOT EXISTS (SELECT 1 FROM [sys_permission] WHERE [id] = N'b0000000000000000000000000000001')
INSERT INTO [sys_permission] ([id],[parent_id],[name],[url],[component],[is_route],[redirect],[menu_type],[perms_type],[sort_no],[always_show],[icon],[is_leaf],[keep_alive],[hidden],[hide_tab],[description],[create_by],[create_time],[del_flag],[rule_flag],[status],[internal_or_external])
VALUES (N'b0000000000000000000000000000001', N'', N'设备管理', N'/device', N'layouts/default/index', 1, N'/device/device', 0, N'1', 5.00, 1, N'ant-design:desktop-outlined', 0, 0, 0, 0, N'设备管理', N'admin', GETDATE(), 0, 0, N'1', 0);

IF NOT EXISTS (SELECT 1 FROM [sys_permission] WHERE [id] = N'b0000000000000000000000000000002')
INSERT INTO [sys_permission] ([id],[parent_id],[name],[url],[component],[is_route],[menu_type],[perms_type],[sort_no],[always_show],[icon],[is_leaf],[keep_alive],[hidden],[hide_tab],[description],[create_by],[create_time],[del_flag],[rule_flag],[status],[internal_or_external])
VALUES (N'b0000000000000000000000000000002', N'b0000000000000000000000000000001', N'设备列表', N'/device/device', N'device/device/index', 1, 1, N'1', 1.00, 0, N'ant-design:laptop-outlined', 1, 0, 0, 0, N'设备列表', N'admin', GETDATE(), 0, 0, N'1', 0);

INSERT INTO [sys_permission] ([id],[parent_id],[name],[url],[component],[is_route],[menu_type],[perms],[perms_type],[sort_no],[always_show],[is_leaf],[keep_alive],[hidden],[create_by],[create_time],[del_flag],[rule_flag],[status],[internal_or_external])
VALUES
(N'b0000000000000000000000000000003', N'b0000000000000000000000000000002', N'新增',     N'', N'', 1, 2, N'device:device:add',          N'1', 1.00, 0, 1, 0, 0, N'admin', GETDATE(), 0, 0, N'1', 0),
(N'b0000000000000000000000000000004', N'b0000000000000000000000000000002', N'编辑',     N'', N'', 1, 2, N'device:device:edit',         N'1', 2.00, 0, 1, 0, 0, N'admin', GETDATE(), 0, 0, N'1', 0),
(N'b0000000000000000000000000000005', N'b0000000000000000000000000000002', N'删除',     N'', N'', 1, 2, N'device:device:delete',       N'1', 3.00, 0, 1, 0, 0, N'admin', GETDATE(), 0, 0, N'1', 0),
(N'b0000000000000000000000000000006', N'b0000000000000000000000000000002', N'状态变更', N'', N'', 1, 2, N'device:device:changeStatus', N'1', 4.00, 0, 1, 0, 0, N'admin', GETDATE(), 0, 0, N'1', 0),
(N'b0000000000000000000000000000007', N'b0000000000000000000000000000002', N'操作记录', N'', N'', 1, 2, N'device:device:record',       N'1', 5.00, 0, 1, 0, 0, N'admin', GETDATE(), 0, 0, N'1', 0);
GO
