-- 大学生社团管理系统（MVP）初始化建表脚本
-- 数据库：MySQL 8.0+
-- 字符集：utf8mb4

CREATE DATABASE IF NOT EXISTS club_mgmt
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE club_mgmt;

SET NAMES utf8mb4;

DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS announcement;
DROP TABLE IF EXISTS activity_signup;
DROP TABLE IF EXISTS activity;
DROP TABLE IF EXISTS club_member;
DROP TABLE IF EXISTS join_request;
DROP TABLE IF EXISTS club;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  account VARCHAR(64) NOT NULL COMMENT '登录账号（手机号/邮箱/学号等统一入口）',
  password_hash VARCHAR(100) NOT NULL COMMENT '密码摘要（不可存明文）',
  name VARCHAR(32) NOT NULL COMMENT '用户姓名/昵称',
  student_no VARCHAR(32) NULL COMMENT '学号（可为空）',
  phone VARCHAR(20) NULL COMMENT '手机号（可为空）',
  email VARCHAR(128) NULL COMMENT '邮箱（可为空）',
  role TINYINT NOT NULL COMMENT '角色：1学生 2社团管理员 3平台管理员',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 2禁用',
  last_login_at DATETIME(3) NULL COMMENT '最近登录时间',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_user_account (account),
  UNIQUE KEY uk_sys_user_student_no (student_no),
  KEY idx_sys_user_role_status (role, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

CREATE TABLE club (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  name VARCHAR(64) NOT NULL COMMENT '社团名称（唯一）',
  type TINYINT NOT NULL COMMENT '社团类型：1学术 2文体 3公益 4兴趣',
  intro VARCHAR(800) NOT NULL COMMENT '社团简介（10-500字，预留长度）',
  advisor_name VARCHAR(32) NULL COMMENT '指导老师（可为空）',
  contact VARCHAR(64) NOT NULL COMMENT '联系方式（手机号/邮箱/微信等）',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '社团状态：0待审 1上架 2驳回 3下架',
  owner_user_id BIGINT NOT NULL COMMENT '创建人/负责人用户ID',
  reviewed_by BIGINT NULL COMMENT '审核人用户ID（平台管理员）',
  reviewed_at DATETIME(3) NULL COMMENT '审核时间',
  reject_reason VARCHAR(200) NULL COMMENT '驳回原因（可为空）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_club_name (name),
  KEY idx_club_status_type (status, type),
  KEY idx_club_owner (owner_user_id),
  KEY idx_club_review (reviewed_by, reviewed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社团表（含创建申请与审核状态）';

CREATE TABLE join_request (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  club_id BIGINT NOT NULL COMMENT '社团ID',
  user_id BIGINT NOT NULL COMMENT '申请人用户ID',
  reason VARCHAR(400) NOT NULL COMMENT '申请理由（10-200字，预留长度）',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '申请状态：0待审核 1通过 2驳回 3取消',
  reviewed_by BIGINT NULL COMMENT '审核人用户ID（社团管理员）',
  reviewed_at DATETIME(3) NULL COMMENT '审核时间',
  reject_reason VARCHAR(200) NULL COMMENT '驳回原因（可为空）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_join_request_club_user (club_id, user_id),
  KEY idx_join_request_club_status (club_id, status),
  KEY idx_join_request_user_status (user_id, status),
  KEY idx_join_request_review (reviewed_by, reviewed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='入社申请表';

CREATE TABLE club_member (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  club_id BIGINT NOT NULL COMMENT '社团ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role TINYINT NOT NULL DEFAULT 1 COMMENT '成员角色：1成员 2管理员（P1）',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '成员状态：1正常 2已移除',
  joined_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '加入时间',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_club_member_club_user (club_id, user_id),
  KEY idx_club_member_club (club_id),
  KEY idx_club_member_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社团成员表';

CREATE TABLE activity (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  club_id BIGINT NOT NULL COMMENT '社团ID',
  title VARCHAR(80) NOT NULL COMMENT '活动标题（2-50字，预留长度）',
  start_time DATETIME(3) NOT NULL COMMENT '开始时间',
  end_time DATETIME(3) NOT NULL COMMENT '结束时间',
  place VARCHAR(120) NOT NULL COMMENT '地点（2-100字，预留长度）',
  signup_deadline DATETIME(3) NOT NULL COMMENT '报名截止时间（<=开始时间）',
  signup_limit INT NOT NULL DEFAULT 0 COMMENT '报名人数上限：0不限',
  signup_count INT NOT NULL DEFAULT 0 COMMENT '已报名人数（汇总字段）',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '活动状态：0草稿 1已发布 2已下架',
  created_by BIGINT NOT NULL COMMENT '创建人用户ID（社团管理员）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_activity_club_status_start (club_id, status, start_time),
  KEY idx_activity_deadline (signup_deadline),
  KEY idx_activity_creator (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动表';

CREATE TABLE activity_signup (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  activity_id BIGINT NOT NULL COMMENT '活动ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '报名状态：1已报名 2已取消',
  signup_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '报名时间',
  cancel_at DATETIME(3) NULL COMMENT '取消时间（可为空）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_activity_signup_activity_user (activity_id, user_id),
  KEY idx_activity_signup_user (user_id),
  KEY idx_activity_signup_activity (activity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动报名表';

CREATE TABLE announcement (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  club_id BIGINT NOT NULL COMMENT '社团ID',
  title VARCHAR(80) NOT NULL COMMENT '公告标题（2-50字，预留长度）',
  content TEXT NOT NULL COMMENT '公告内容',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '公告状态：1已发布 2已下架',
  published_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '发布时间',
  published_by BIGINT NOT NULL COMMENT '发布人用户ID（社团管理员）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_announcement_club_status_time (club_id, status, published_at),
  KEY idx_announcement_publisher (published_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社团公告表';

CREATE TABLE notification (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '接收人用户ID',
  type TINYINT NOT NULL COMMENT '通知类型：1入社结果 2社团审核 3活动 4系统',
  ref_type TINYINT NULL COMMENT '关联类型：1社团 2入社申请 3活动 4公告',
  ref_id BIGINT NULL COMMENT '关联对象ID（可为空）',
  title VARCHAR(80) NOT NULL COMMENT '通知标题',
  content VARCHAR(500) NOT NULL COMMENT '通知内容摘要',
  is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0未读 1已读（P1）',
  read_at DATETIME(3) NULL COMMENT '已读时间（可为空，P1）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_notification_user_read_time (user_id, is_read, created_at),
  KEY idx_notification_ref (ref_type, ref_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='站内通知表';

CREATE TABLE audit_log (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  actor_user_id BIGINT NOT NULL COMMENT '操作人用户ID',
  action VARCHAR(64) NOT NULL COMMENT '动作标识（如：CLUB_REVIEW_PASS/JOIN_REJECT/ACTIVITY_OFFLINE等）',
  target_type VARCHAR(32) NULL COMMENT '对象类型（如：club/join_request/activity/announcement）',
  target_id BIGINT NULL COMMENT '对象ID（可为空）',
  reason VARCHAR(255) NULL COMMENT '原因（可为空）',
  detail JSON NULL COMMENT '扩展详情JSON（可为空）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_audit_target (target_type, target_id),
  KEY idx_audit_actor_time (actor_user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审计日志表';
