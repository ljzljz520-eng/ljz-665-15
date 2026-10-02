-- =====================================================================
-- 设备管理模块升级脚本（SQL Server）
-- 版本: V20261002
-- 说明: 幂等脚本，可重复执行。适用于已初始化过 JeecgBoot 数据库的环境。
--       全新环境执行 docker compose up -d 会自动创建，无需手工执行本脚本。
-- =====================================================================

-- 1. 设备表
IF OBJECT_ID(N'[device]', N'U') IS NULL
CREATE TABLE [device] (
  [id] nvarchar(32) NOT NULL,
  [device_code] nvarchar(64) NOT NULL,
  [device_name] nvarchar(128) NOT NULL,
  [device_model] nvarchar(128) NULL,
  [status] nvarchar(2) NOT NULL DEFAULT '1',
  [location] nvarchar(255) NULL,
  [remark] nvarchar(500) NULL,
  [create_by] nvarchar(50) NULL,
  [create_time] datetime2 NULL,
  [update_by] nvarchar(50) NULL,
  [update_time] datetime2 NULL,
  PRIMARY KEY ([id])
);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'uk_device_code' AND object_id = OBJECT_ID(N'[device]'))
  CREATE INDEX [uk_device_code] ON [device] ([device_code]);

-- 2. 设备状态流转记录表
IF OBJECT_ID(N'[device_status_record]', N'U') IS NULL
CREATE TABLE [device_status_record] (
  [id] nvarchar(32) NOT NULL,
  [device_id] nvarchar(32) NOT NULL,
  [device_code] nvarchar(64) NULL,
  [device_name] nvarchar(128) NULL,
  [before_status] nvarchar(2) NULL,
  [after_status] nvarchar(2) NOT NULL,
  [remark] nvarchar(500) NULL,
  [create_by] nvarchar(50) NULL,
  [create_time] datetime2 NULL,
  PRIMARY KEY ([id])
);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_dsr_device_id' AND object_id = OBJECT_ID(N'[device_status_record]'))
  CREATE INDEX [idx_dsr_device_id] ON [device_status_record] ([device_id]);

-- 3. 菜单与按钮权限（幂等）
IF NOT EXISTS (SELECT 1 FROM [sys_permission] WHERE [id] = '9001000000000000001')
INSERT INTO [sys_permission] VALUES ('9001000000000000001', '', '设备管理', '/device', 'layouts/default/index', 1, NULL, NULL, 0, NULL, '1', 5.00, 0, 'ant-design:laptop-outlined', 0, 0, 0, 0, '设备管理', 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
IF NOT EXISTS (SELECT 1 FROM [sys_permission] WHERE [id] = '9001000000000000002')
INSERT INTO [sys_permission] VALUES ('9001000000000000002', '9001000000000000001', '设备列表', '/device/deviceList', 'device/deviceList/index', 1, NULL, NULL, 1, NULL, '1', 1.00, 0, 'ant-design:desktop-outlined', 1, 1, 0, 0, '设备列表与状态流转', 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
IF NOT EXISTS (SELECT 1 FROM [sys_permission] WHERE [id] = '9001000000000000003')
INSERT INTO [sys_permission] VALUES ('9001000000000000003', '9001000000000000001', '操作记录', '/device/statusRecord', 'device/statusRecord/index', 1, NULL, NULL, 1, NULL, '1', 2.00, 0, 'ant-design:history-outlined', 1, 1, 0, 0, '设备状态流转操作记录', 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
IF NOT EXISTS (SELECT 1 FROM [sys_permission] WHERE [id] = '9001000000000000010')
INSERT INTO [sys_permission] VALUES ('9001000000000000010', '9001000000000000002', '新增', NULL, NULL, 1, NULL, NULL, 2, 'device:device:add', '1', 1.00, 0, NULL, 1, 0, 0, NULL, NULL, 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
IF NOT EXISTS (SELECT 1 FROM [sys_permission] WHERE [id] = '9001000000000000011')
INSERT INTO [sys_permission] VALUES ('9001000000000000011', '9001000000000000002', '编辑', NULL, NULL, 1, NULL, NULL, 2, 'device:device:edit', '1', 2.00, 0, NULL, 1, 0, 0, NULL, NULL, 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
IF NOT EXISTS (SELECT 1 FROM [sys_permission] WHERE [id] = '9001000000000000012')
INSERT INTO [sys_permission] VALUES ('9001000000000000012', '9001000000000000002', '删除', NULL, NULL, 1, NULL, NULL, 2, 'device:device:delete', '1', 3.00, 0, NULL, 1, 0, 0, NULL, NULL, 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);
IF NOT EXISTS (SELECT 1 FROM [sys_permission] WHERE [id] = '9001000000000000013')
INSERT INTO [sys_permission] VALUES ('9001000000000000013', '9001000000000000002', '状态变更', NULL, NULL, 1, NULL, NULL, 2, 'device:device:changeStatus', '1', 4.00, 0, NULL, 1, 0, 0, NULL, NULL, 'admin', GETDATE(), NULL, NULL, 0, 0, '1', 0);

-- 4. 执行后请清理/刷新后端缓存并重新登录（或重启后端），左侧菜单将出现"设备管理"。
