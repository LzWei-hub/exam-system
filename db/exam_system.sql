-- 创建数据库
CREATE DATABASE IF NOT EXISTS exam_system 
DEFAULT CHARSET utf8mb4 
COLLATE utf8mb4_general_ci;

USE exam_system;

-- 用户表（支持三种角色）
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) UNIQUE NOT NULL COMMENT '登录账号',
  password VARCHAR(100) NOT NULL COMMENT '加密密码',
  real_name VARCHAR(20) NOT NULL COMMENT '真实姓名',
  role ENUM('ADMIN','TEACHER','STUDENT') NOT NULL COMMENT '系统角色',
  email VARCHAR(50) COMMENT '联系邮箱',
  avatar VARCHAR(255) COMMENT '头像URL',
  status TINYINT DEFAULT 1 COMMENT '账号状态(0-禁用 1-启用)',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username(username),
  INDEX idx_role(role)
) ENGINE=InnoDB COMMENT='系统用户表';

-- 科目分类表（支持多级分类）
CREATE TABLE exam_subject (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '科目ID',
  name VARCHAR(100) NOT NULL COMMENT '科目名称',
  parent_id BIGINT DEFAULT 0 COMMENT '父级科目ID',
  level TINYINT DEFAULT 1 COMMENT '分类层级',
  sort INT DEFAULT 0 COMMENT '排序权重',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (parent_id) REFERENCES exam_subject(id)
) ENGINE=InnoDB COMMENT='考试科目表';

-- 试题表（支持4种题型）
CREATE TABLE exam_question (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '试题ID',
  subject_id BIGINT NOT NULL COMMENT '所属科目',
  question_type ENUM('SINGLE','MULTI','JUDGE','FILL') NOT NULL COMMENT '题目类型',
  content TEXT NOT NULL COMMENT '题干内容',
  options JSON COMMENT '选项内容（JSON数组）',
  answer TEXT NOT NULL COMMENT '参考答案',
  analysis TEXT COMMENT '题目解析',
  difficulty TINYINT DEFAULT 3 COMMENT '难度系数(1-5)',
  score DECIMAL(5,1) NOT NULL DEFAULT 2.0 COMMENT '单题分值',
  creator_id BIGINT NOT NULL COMMENT '创建人',
  review_status TINYINT DEFAULT 0 COMMENT '审核状态(0-待审核 1-已通过 2-已驳回)',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (subject_id) REFERENCES exam_subject(id),
  FOREIGN KEY (creator_id) REFERENCES sys_user(id),
  INDEX idx_subject(subject_id),
  INDEX idx_difficulty(difficulty)
) ENGINE=InnoDB COMMENT='题库表';

-- 试卷表（包含题目元数据）
CREATE TABLE exam_paper (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '试卷ID',
  title VARCHAR(200) NOT NULL COMMENT '试卷标题',
  subject_id BIGINT NOT NULL COMMENT '所属科目',
  total_score DECIMAL(5,1) NOT NULL COMMENT '试卷总分',
  time_limit INT NOT NULL COMMENT '考试时长(分钟)',
  question_data JSON NOT NULL COMMENT '试题元数据（包含题目ID、分值、顺序）',
  creator_id BIGINT NOT NULL COMMENT '组卷人',
  exam_start DATETIME COMMENT '考试开始时间',
  exam_end DATETIME COMMENT '考试结束时间',
  status TINYINT DEFAULT 1 COMMENT '试卷状态(0-草稿 1-已发布 2-已归档)',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (subject_id) REFERENCES exam_subject(id),
  FOREIGN KEY (creator_id) REFERENCES sys_user(id),
  INDEX idx_exam_time(exam_start, exam_end)
) ENGINE=InnoDB COMMENT='试卷表';

-- 考试记录表（核心业务表）
CREATE TABLE exam_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '考试ID',
  user_id BIGINT NOT NULL COMMENT '考生ID',
  paper_id BIGINT NOT NULL COMMENT '试卷ID',
  start_time DATETIME NOT NULL COMMENT '开始时间',
  submit_time DATETIME COMMENT '提交时间',
  answer_snapshot JSON COMMENT '答题快照（存储题目和答案）',
  auto_score DECIMAL(5,1) DEFAULT 0.0 COMMENT '系统评分',
  manual_score DECIMAL(5,1) DEFAULT 0.0 COMMENT '人工评分',
  final_score DECIMAL(5,1) GENERATED ALWAYS AS (auto_score + manual_score) STORED COMMENT '最终得分',
  status ENUM('PROGRESS','SUBMITTED','TIMEOUT','REVIEWING') DEFAULT 'PROGRESS' COMMENT '考试状态',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES sys_user(id),
  FOREIGN KEY (paper_id) REFERENCES exam_paper(id),
  INDEX idx_user_exam(user_id, paper_id)
) ENGINE=InnoDB COMMENT='考试记录表';

-- 错题本表（学习分析）
CREATE TABLE exam_wrong_book (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  user_id BIGINT NOT NULL COMMENT '学生ID',
  question_id BIGINT NOT NULL COMMENT '错题ID',
  wrong_count INT DEFAULT 1 COMMENT '错误次数',
  last_wrong_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后错误时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES sys_user(id),
  FOREIGN KEY (question_id) REFERENCES exam_question(id),
  UNIQUE KEY uniq_user_question (user_id, question_id)
) ENGINE=InnoDB COMMENT='错题本表';