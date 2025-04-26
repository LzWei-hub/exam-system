USE exam_system;

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
  code VARCHAR(50) UNIQUE NOT NULL COMMENT '角色编码',
  name VARCHAR(100) NOT NULL COMMENT '角色名称',
  description VARCHAR(255) COMMENT '角色描述',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态(0-禁用 1-启用)',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
  parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
  code VARCHAR(100) UNIQUE NOT NULL COMMENT '权限编码',
  name VARCHAR(100) NOT NULL COMMENT '权限名称',
  type VARCHAR(20) NOT NULL COMMENT '权限类型(menu,button)',
  path VARCHAR(255) COMMENT '菜单路径',
  icon VARCHAR(100) COMMENT '图标',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态(0-禁用 1-启用)',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='权限表';

-- 角色-权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  permission_id BIGINT NOT NULL COMMENT '权限ID',
  INDEX idx_role_id (role_id),
  INDEX idx_permission_id (permission_id),
  UNIQUE KEY uniq_role_permission (role_id, permission_id)
) ENGINE=InnoDB COMMENT='角色-权限关联表';

-- 用户-角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  INDEX idx_user_id (user_id),
  INDEX idx_role_id (role_id),
  UNIQUE KEY uniq_user_role (user_id, role_id)
) ENGINE=InnoDB COMMENT='用户-角色关联表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  title VARCHAR(100) COMMENT '模块标题',
  business_type VARCHAR(50) COMMENT '业务类型',
  method VARCHAR(255) COMMENT '方法名称',
  request_method VARCHAR(10) COMMENT '请求方式',
  operator_type VARCHAR(50) COMMENT '操作类别',
  oper_name VARCHAR(100) COMMENT '操作人员',
  oper_url VARCHAR(255) COMMENT '请求URL',
  oper_ip VARCHAR(50) COMMENT '操作地址',
  oper_location VARCHAR(255) COMMENT '操作位置',
  oper_param TEXT COMMENT '请求参数',
  json_result TEXT COMMENT '返回参数',
  status TINYINT DEFAULT 0 COMMENT '操作状态(0-正常 1-异常)',
  error_msg TEXT COMMENT '错误消息',
  oper_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  INDEX idx_oper_time (oper_time)
) ENGINE=InnoDB COMMENT='操作日志表';

-- 访问日志表
CREATE TABLE IF NOT EXISTS sys_access_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  ip_address VARCHAR(50) COMMENT 'IP地址',
  request_uri VARCHAR(255) COMMENT '请求地址',
  request_method VARCHAR(10) COMMENT '请求方式',
  user_agent VARCHAR(255) COMMENT '用户代理',
  username VARCHAR(100) COMMENT '操作人员',
  status INT COMMENT '状态码',
  response_time BIGINT COMMENT '响应时间(毫秒)',
  access_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  INDEX idx_access_time (access_time),
  INDEX idx_username (username)
) ENGINE=InnoDB COMMENT='访问日志表';

-- 初始数据
INSERT IGNORE INTO sys_role (code, name, description, sort, status)
VALUES
('ADMIN', '系统管理员', '系统管理员，拥有所有权限', 1, 1),
('TEACHER', '教师', '教师角色，管理考试和试卷', 2, 1),
('STUDENT', '学生', '学生角色，参加考试', 3, 1);

-- 初始权限数据（菜单）
INSERT IGNORE INTO sys_permission (parent_id, code, name, type, path, icon, sort, status)
VALUES
(0, 'system', '系统管理', 'menu', '/system', 'setting', 1, 1),
(1, 'user', '用户管理', 'menu', '/system/user', 'user', 1, 1),
(1, 'role', '角色管理', 'menu', '/system/role', 'team', 2, 1),
(1, 'permission', '权限管理', 'menu', '/system/permission', 'safety', 3, 1),
(1, 'monitor', '系统监控', 'menu', '/system/monitor', 'dashboard', 4, 1),
(0, 'exam', '考试管理', 'menu', '/exam', 'book', 2, 1),
(6, 'subject', '科目管理', 'menu', '/exam/subject', 'folder', 1, 1),
(6, 'question', '题库管理', 'menu', '/exam/question', 'file-text', 2, 1),
(6, 'paper', '试卷管理', 'menu', '/exam/paper', 'file', 3, 1),
(6, 'record', '考试记录', 'menu', '/exam/record', 'history', 4, 1),
(0, 'student', '学生端', 'menu', '/student', 'user', 3, 1),
(11, 'exam-list', '考试列表', 'menu', '/student/exam-list', 'ordered-list', 1, 1),
(11, 'my-record', '我的考试', 'menu', '/student/record', 'solution', 2, 1),
(11, 'wrong-book', '错题本', 'menu', '/student/wrong-book', 'exception', 3, 1);

-- 按钮权限
INSERT IGNORE INTO sys_permission (parent_id, code, name, type, path, icon, sort, status)
VALUES
(2, 'user:add', '添加用户', 'button', NULL, NULL, 1, 1),
(2, 'user:edit', '编辑用户', 'button', NULL, NULL, 2, 1),
(2, 'user:delete', '删除用户', 'button', NULL, NULL, 3, 1),
(2, 'user:view', '查看用户', 'button', NULL, NULL, 4, 1),
(2, 'user:assign', '分配角色', 'button', NULL, NULL, 5, 1),
(3, 'role:add', '添加角色', 'button', NULL, NULL, 1, 1),
(3, 'role:edit', '编辑角色', 'button', NULL, NULL, 2, 1),
(3, 'role:delete', '删除角色', 'button', NULL, NULL, 3, 1),
(3, 'role:view', '查看角色', 'button', NULL, NULL, 4, 1),
(3, 'role:assign', '分配权限', 'button', NULL, NULL, 5, 1),
(5, 'monitor:log', '操作日志', 'button', NULL, NULL, 1, 1),
(5, 'monitor:access', '访问日志', 'button', NULL, NULL, 2, 1),
(5, 'monitor:stats', '访问统计', 'button', NULL, NULL, 3, 1),
(5, 'monitor:health', '系统健康', 'button', NULL, NULL, 4, 1);

-- 为管理员角色分配权限（所有权限）
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 为教师角色分配权限（考试管理相关权限）
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE parent_id IN (6, 7, 8, 9, 10) OR id IN (6, 7, 8, 9, 10);

-- 为学生角色分配权限（学生端相关权限）
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 3, id FROM sys_permission WHERE parent_id IN (11, 12, 13, 14) OR id IN (11, 12, 13, 14); 