-- =============================================================================
-- 设备管理模块初始化脚本（MySQL 5.7 / 8.0）
-- 包含：
--   1. 设备台账表 biz_device
--   2. 设备状态流转记录表 biz_device_status_record
--   3. 菜单与按钮权限 sys_permission
-- 设备状态：1-在用 2-维修中 3-停用 4-报废
-- =============================================================================

-- ----------------------------
-- 1、设备台账表
-- ----------------------------
-- 表已存在时跳过（幂等）
CREATE TABLE IF NOT EXISTS `biz_device`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `device_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备编号',
  `device_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备名称',
  `model` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备型号',
  `status` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '设备状态：1在用 2维修中 3停用 4报废',
  `use_date` date NULL DEFAULT NULL COMMENT '启用日期',
  `location` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存放位置',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_device_code`(`device_code`) USING BTREE,
  INDEX `idx_device_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备台账表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 2、设备状态流转操作记录表
-- ----------------------------
-- 表已存在时跳过（幂等）
CREATE TABLE IF NOT EXISTS `biz_device_status_record`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `device_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备ID',
  `device_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备编号（冗余）',
  `device_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备名称（冗余）',
  `from_status` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '变更前状态：1在用 2维修中 3停用 4报废（新建为空）',
  `to_status` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '变更后状态：1在用 2维修中 3停用 4报废',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作说明（维修恢复在用时必填）',
  `operate_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dsr_device_id`(`device_id`) USING BTREE,
  INDEX `idx_dsr_operate_time`(`operate_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备状态流转操作记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 3、菜单与按钮权限（INSERT IGNORE 保证可重复执行）
-- 一级菜单：设备管理  子菜单：设备台账
-- ----------------------------
INSERT IGNORE INTO `sys_permission`(`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`) VALUES ('1900000000000000001', '', '设备管理', '/device', 'layouts/default/index', 1, NULL, '/device/device', 0, NULL, '1', 10.00, 0, 'ant-design:tool-outlined', 0, 0, 0, 0, '设备管理', 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
INSERT IGNORE INTO `sys_permission`(`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`) VALUES ('1900000000000000002', '1900000000000000001', '设备台账', '/device/device', 'device/device/DeviceList', 1, 'DeviceList', NULL, 1, NULL, '1', 1.00, 0, 'ant-design:laptop-outlined', 1, 0, 0, 0, '设备台账与状态流转', 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
INSERT IGNORE INTO `sys_permission`(`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`) VALUES ('1900000000000000003', '1900000000000000002', '新增', NULL, NULL, 1, NULL, NULL, 2, 'device:device:add', '1', 1.00, 0, NULL, 1, 0, 0, NULL, '新建设备', 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
INSERT IGNORE INTO `sys_permission`(`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`) VALUES ('1900000000000000004', '1900000000000000002', '编辑', NULL, NULL, 1, NULL, NULL, 2, 'device:device:edit', '1', 2.00, 0, NULL, 1, 0, 0, NULL, '编辑设备基础信息', 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
INSERT IGNORE INTO `sys_permission`(`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`) VALUES ('1900000000000000005', '1900000000000000002', '删除', NULL, NULL, 1, NULL, NULL, 2, 'device:device:delete', '1', 3.00, 0, NULL, 1, 0, 0, NULL, '删除设备', 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
INSERT IGNORE INTO `sys_permission`(`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`) VALUES ('1900000000000000006', '1900000000000000002', '状态变更', NULL, NULL, 1, NULL, NULL, 2, 'device:device:changeStatus', '1', 4.00, 0, NULL, 1, 0, 0, NULL, '设备状态流转', 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
INSERT IGNORE INTO `sys_permission`(`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`) VALUES ('1900000000000000007', '1900000000000000002', '流转记录', NULL, NULL, 1, NULL, NULL, 2, 'device:device:statusRecord', '1', 5.00, 0, NULL, 1, 0, 0, NULL, '查看设备状态流转记录', 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);

-- admin 角色授权（role_id 为系统管理员角色，如环境不同请调整）
INSERT IGNORE INTO `sys_role_permission`(`id`, `role_id`, `permission_id`) VALUES ('190000000000000011', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000001');
INSERT IGNORE INTO `sys_role_permission`(`id`, `role_id`, `permission_id`) VALUES ('190000000000000012', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000002');
INSERT IGNORE INTO `sys_role_permission`(`id`, `role_id`, `permission_id`) VALUES ('190000000000000013', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000003');
INSERT IGNORE INTO `sys_role_permission`(`id`, `role_id`, `permission_id`) VALUES ('190000000000000014', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000004');
INSERT IGNORE INTO `sys_role_permission`(`id`, `role_id`, `permission_id`) VALUES ('190000000000000015', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000005');
INSERT IGNORE INTO `sys_role_permission`(`id`, `role_id`, `permission_id`) VALUES ('190000000000000016', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000006');
INSERT IGNORE INTO `sys_role_permission`(`id`, `role_id`, `permission_id`) VALUES ('190000000000000017', 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000007');
