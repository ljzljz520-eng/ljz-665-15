-- =============================================================
-- 设备管理模块 - MySQL 初始化脚本
-- 包含：设备表、设备状态操作记录表、数据字典、菜单与按钮权限
-- 状态：1-在用(默认) 2-维修 3-停用 4-报废
-- =============================================================

-- ----------------------------
-- 1、设备表
-- ----------------------------
DROP TABLE IF EXISTS `device_info`;
CREATE TABLE `device_info`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `device_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备编号',
  `device_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  `device_model` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备型号',
  `device_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备类型(字典 device_type)',
  `status` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '状态: 1在用 2维修 3停用 4报废',
  `location` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存放位置',
  `owner` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '责任人',
  `use_date` date NULL DEFAULT NULL COMMENT '启用日期',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_device_code`(`device_code`) USING BTREE,
  INDEX `idx_device_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 2、设备状态操作记录表（状态追溯）
-- ----------------------------
DROP TABLE IF EXISTS `device_status_record`;
CREATE TABLE `device_status_record`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `device_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备id',
  `before_status` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '变更前状态',
  `after_status` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '变更后状态',
  `operate_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作类型: CREATE新建 CHANGE流转',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '变更说明',
  `operate_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dsr_device_id`(`device_id`) USING BTREE,
  INDEX `idx_dsr_operate_time`(`operate_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备状态操作记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 3、数据字典：设备状态 device_status
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('a0000000000000000000000000000001', '设备状态', 'device_status', '设备状态：1在用 2维修 3停用 4报废', 0, 'admin', NOW(), NULL, NULL, 0, 0, NULL);
INSERT INTO `sys_dict_item` VALUES ('a0000000000000000000000000000011', 'a0000000000000000000000000000001', '在用', '1', 'green',  NULL, 1, 1, 'admin', NOW(), NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('a0000000000000000000000000000012', 'a0000000000000000000000000000001', '维修', '2', 'orange', NULL, 2, 1, 'admin', NOW(), NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('a0000000000000000000000000000013', 'a0000000000000000000000000000001', '停用', '3', 'default', NULL, 3, 1, 'admin', NOW(), NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('a0000000000000000000000000000014', 'a0000000000000000000000000000001', '报废', '4', 'red', '终态，不能再变更', 4, 1, 'admin', NOW(), NULL, NULL);

-- ----------------------------
-- 4、数据字典：设备类型 device_type（示例项，可在系统管理中维护）
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('a0000000000000000000000000000002', '设备类型', 'device_type', '设备类型字典', 0, 'admin', NOW(), NULL, NULL, 0, 0, NULL);
INSERT INTO `sys_dict_item` VALUES ('a0000000000000000000000000000021', 'a0000000000000000000000000000002', '生产设备', '1', 'blue', NULL, 1, 1, 'admin', NOW(), NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('a0000000000000000000000000000022', 'a0000000000000000000000000000002', '检测设备', '2', 'cyan', NULL, 2, 1, 'admin', NOW(), NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('a0000000000000000000000000000023', 'a0000000000000000000000000000002', '办公设备', '3', 'purple', NULL, 3, 1, 'admin', NOW(), NULL, NULL);

-- ----------------------------
-- 5、菜单与按钮权限（admin 超管自动拥有全部权限，无需分配角色）
-- ----------------------------
-- 一级菜单：设备管理
INSERT INTO `sys_permission` VALUES ('b0000000000000000000000000000001', '', '设备管理', '/device', 'layouts/default/index', 1, NULL, '/device/device', 0, NULL, '1', 5.00, 1, 'ant-design:desktop-outlined', 0, 0, 0, 0, '设备管理', 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
-- 子菜单：设备列表
INSERT INTO `sys_permission` VALUES ('b0000000000000000000000000000002', 'b0000000000000000000000000000001', '设备列表', '/device/device', 'device/device/index', 1, NULL, NULL, 1, NULL, '1', 1.00, 0, 'ant-design:laptop-outlined', 1, 0, 0, 0, '设备列表', 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
-- 按钮权限
INSERT INTO `sys_permission` VALUES ('b0000000000000000000000000000003', 'b0000000000000000000000000000002', '新增', '', '', 1, NULL, NULL, 2, 'device:device:add',    '1', 1.00, 0, NULL, 1, 0, 0, NULL, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
INSERT INTO `sys_permission` VALUES ('b0000000000000000000000000000004', 'b0000000000000000000000000000002', '编辑', '', '', 1, NULL, NULL, 2, 'device:device:edit',   '1', 2.00, 0, NULL, 1, 0, 0, NULL, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
INSERT INTO `sys_permission` VALUES ('b0000000000000000000000000000005', 'b0000000000000000000000000000002', '删除', '', '', 1, NULL, NULL, 2, 'device:device:delete', '1', 3.00, 0, NULL, 1, 0, 0, NULL, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
INSERT INTO `sys_permission` VALUES ('b0000000000000000000000000000006', 'b0000000000000000000000000000002', '状态变更', '', '', 1, NULL, NULL, 2, 'device:device:changeStatus', '1', 4.00, 0, NULL, 1, 0, 0, NULL, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
INSERT INTO `sys_permission` VALUES ('b0000000000000000000000000000007', 'b0000000000000000000000000000002', '操作记录', '', '', 1, NULL, NULL, 2, 'device:device:record', '1', 5.00, 0, NULL, 1, 0, 0, NULL, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);
