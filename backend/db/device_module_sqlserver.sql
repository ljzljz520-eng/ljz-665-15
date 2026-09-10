-- =============================================================================
-- 设备管理模块初始化脚本（SQL Server 版，由 backend/db/device_module.sql 转换生成）
-- 设备状态：1-在用 2-维修中 3-停用 4-报废
-- 菜单数据如已存在（主键冲突），请删除下方 INSERT 后按需手工维护
-- =============================================================================
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;

CREATE TABLE [biz_device] (
  [id] nvarchar(32) NOT NULL,
  [device_code] nvarchar(64) NOT NULL,
  [device_name] nvarchar(128) NOT NULL,
  [model] nvarchar(128) NULL,
  [status] nvarchar(2) NOT NULL DEFAULT '1',
  [use_date] date NULL,
  [location] nvarchar(128) NULL,
  [remark] nvarchar(500) NULL,
  [create_by] nvarchar(50) NULL,
  [create_time] datetime2 NULL,
  [update_by] nvarchar(50) NULL,
  [update_time] datetime2 NULL,
  PRIMARY KEY ([id]) 
);
CREATE UNIQUE INDEX [uk_device_code] ON [biz_device] ([device_code]);
CREATE INDEX [idx_device_status] ON [biz_device] ([status]);

CREATE TABLE [biz_device_status_record] (
  [id] nvarchar(32) NOT NULL,
  [device_id] nvarchar(32) NOT NULL,
  [device_code] nvarchar(64) NULL,
  [device_name] nvarchar(128) NULL,
  [from_status] nvarchar(2) NULL,
  [to_status] nvarchar(2) NOT NULL,
  [remark] nvarchar(500) NULL,
  [operate_time] datetime2 NULL,
  [create_by] nvarchar(50) NULL,
  [create_time] datetime2 NULL,
  [update_by] nvarchar(50) NULL,
  [update_time] datetime2 NULL,
  PRIMARY KEY ([id]) 
);
CREATE INDEX [idx_dsr_device_id] ON [biz_device_status_record] ([device_id]);
CREATE INDEX [idx_dsr_operate_time] ON [biz_device_status_record] ([operate_time]);

INSERT INTO [sys_permission]([id], [parent_id], [name], [url], [component], [is_route], [component_name], [redirect], [menu_type], [perms], [perms_type], [sort_no], [always_show], [icon], [is_leaf], [keep_alive], [hidden], [hide_tab], [description], [create_by], [create_time], [update_by], [update_time], [del_flag], [rule_flag], [status], [internal_or_external]) VALUES ('1900000000000000001', '', '设备管理', '/device', 'layouts/default/index', 1, NULL, '/device/device', 0, NULL, '1', 10.00, 0, 'ant-design:tool-outlined', 0, 0, 0, 0, '设备管理', 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
INSERT INTO [sys_permission]([id], [parent_id], [name], [url], [component], [is_route], [component_name], [redirect], [menu_type], [perms], [perms_type], [sort_no], [always_show], [icon], [is_leaf], [keep_alive], [hidden], [hide_tab], [description], [create_by], [create_time], [update_by], [update_time], [del_flag], [rule_flag], [status], [internal_or_external]) VALUES ('1900000000000000002', '1900000000000000001', '设备台账', '/device/device', 'device/device/DeviceList', 1, 'DeviceList', NULL, 1, NULL, '1', 1.00, 0, 'ant-design:laptop-outlined', 1, 0, 0, 0, '设备台账与状态流转', 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
INSERT INTO [sys_permission]([id], [parent_id], [name], [url], [component], [is_route], [component_name], [redirect], [menu_type], [perms], [perms_type], [sort_no], [always_show], [icon], [is_leaf], [keep_alive], [hidden], [hide_tab], [description], [create_by], [create_time], [update_by], [update_time], [del_flag], [rule_flag], [status], [internal_or_external]) VALUES ('1900000000000000003', '1900000000000000002', '新增', NULL, NULL, 1, NULL, NULL, 2, 'device:device:add', '1', 1.00, 0, NULL, 1, 0, 0, NULL, '新建设备', 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
INSERT INTO [sys_permission]([id], [parent_id], [name], [url], [component], [is_route], [component_name], [redirect], [menu_type], [perms], [perms_type], [sort_no], [always_show], [icon], [is_leaf], [keep_alive], [hidden], [hide_tab], [description], [create_by], [create_time], [update_by], [update_time], [del_flag], [rule_flag], [status], [internal_or_external]) VALUES ('1900000000000000004', '1900000000000000002', '编辑', NULL, NULL, 1, NULL, NULL, 2, 'device:device:edit', '1', 2.00, 0, NULL, 1, 0, 0, NULL, '编辑设备基础信息', 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
INSERT INTO [sys_permission]([id], [parent_id], [name], [url], [component], [is_route], [component_name], [redirect], [menu_type], [perms], [perms_type], [sort_no], [always_show], [icon], [is_leaf], [keep_alive], [hidden], [hide_tab], [description], [create_by], [create_time], [update_by], [update_time], [del_flag], [rule_flag], [status], [internal_or_external]) VALUES ('1900000000000000005', '1900000000000000002', '删除', NULL, NULL, 1, NULL, NULL, 2, 'device:device:delete', '1', 3.00, 0, NULL, 1, 0, 0, NULL, '删除设备', 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
INSERT INTO [sys_permission]([id], [parent_id], [name], [url], [component], [is_route], [component_name], [redirect], [menu_type], [perms], [perms_type], [sort_no], [always_show], [icon], [is_leaf], [keep_alive], [hidden], [hide_tab], [description], [create_by], [create_time], [update_by], [update_time], [del_flag], [rule_flag], [status], [internal_or_external]) VALUES ('1900000000000000006', '1900000000000000002', '状态变更', NULL, NULL, 1, NULL, NULL, 2, 'device:device:changeStatus', '1', 4.00, 0, NULL, 1, 0, 0, NULL, '设备状态流转', 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
INSERT INTO [sys_permission]([id], [parent_id], [name], [url], [component], [is_route], [component_name], [redirect], [menu_type], [perms], [perms_type], [sort_no], [always_show], [icon], [is_leaf], [keep_alive], [hidden], [hide_tab], [description], [create_by], [create_time], [update_by], [update_time], [del_flag], [rule_flag], [status], [internal_or_external]) VALUES ('1900000000000000007', '1900000000000000002', '流转记录', NULL, NULL, 1, NULL, NULL, 2, 'device:device:statusRecord', '1', 5.00, 0, NULL, 1, 0, 0, NULL, '查看设备状态流转记录', 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
INSERT INTO [sys_role_permission]([id], [role_id], [permission_id]) VALUES ('190000000000000011', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000001');
INSERT INTO [sys_role_permission]([id], [role_id], [permission_id]) VALUES ('190000000000000012', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000002');
INSERT INTO [sys_role_permission]([id], [role_id], [permission_id]) VALUES ('190000000000000013', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000003');
INSERT INTO [sys_role_permission]([id], [role_id], [permission_id]) VALUES ('190000000000000014', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000004');
INSERT INTO [sys_role_permission]([id], [role_id], [permission_id]) VALUES ('190000000000000015', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000005');
INSERT INTO [sys_role_permission]([id], [role_id], [permission_id]) VALUES ('190000000000000016', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000006');
INSERT INTO [sys_role_permission]([id], [role_id], [permission_id]) VALUES ('190000000000000017', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000007');
