# 数据库设计文档：大学生社团管理系统（MVP）

> 数据库：MySQL 8.0+（utf8mb4）；与 PRD「大学生社团管理系统（MVP）」对齐。  
> 对应建表脚本：`sql/init_schema_mvp.sql`

## 1. 文档信息

| 项目 | 内容 |
|------|------|
| 文档版本 | v1.0 |
| 创建日期 | 2026-05-01 |
| 适用范围 | MVP（社团广场/入社/活动/公告/管理后台/平台审核） |
| 对应 PRD | documents/PRD-大学生社团管理系统-MVP-v1.0.md |

## 2. 设计原则

- 单库单体优先：降低 MVP 交付复杂度
- 可追溯：对审核/下架/移除成员/活动下架等关键行为记录审计日志
- 可演进：预留状态字段与扩展字段（如 JSON detail、通知类型等）
- 查询友好：为高频筛选字段建立索引；避免多表关联查询场景下，通过冗余字段/汇总字段或服务层聚合实现

## 3. 命名与约定

### 3.1 表命名
- 统一使用 snake_case
- 业务表建议前缀：无强制（MVP 简化）；系统用户表使用 `sys_user`

### 3.2 主键与时间字段
- 主键：`BIGINT` 自增
- 通用字段：
  - `created_at`：创建时间（DATETIME(3)）
  - `updated_at`：更新时间（DATETIME(3)）
  - `is_deleted`：逻辑删除（TINYINT，0/1）

### 3.3 枚举字段约定（建议）
- 所有状态/类型字段使用 `TINYINT`，并在列注释中标注含义
- 枚举值在后端以常量/枚举统一管理（避免魔法数字）

## 4. ER 关系（文字版）

- 用户（`sys_user`）与社团（`club`）
  - 1:N：一个用户可创建多个社团（`club.owner_user_id`）
- 用户与社团成员关系（`club_member`）
  - N:N：用户可加入多个社团；社团包含多个成员（唯一约束 `club_id + user_id`）
- 入社申请（`join_request`）
  - 用户 1:N 入社申请；社团 1:N 入社申请
  - MVP 为便于“修改理由/重复提交控制”，建议对同一 `club_id + user_id` 仅保留 1 条申请记录（唯一约束），通过更新状态实现复用
- 活动（`activity`）与报名（`activity_signup`）
  - 1:N：一个活动有多个报名记录（唯一约束 `activity_id + user_id`，防重复报名）
- 公告（`announcement`）
  - 社团 1:N 公告
- 站内通知（`notification`）
  - 用户 1:N 通知；通知通过 `ref_type/ref_id` 关联业务对象
- 审计日志（`audit_log`）
  - 对关键动作记录：操作人、对象、原因、扩展详情（JSON）

## 5. 表结构说明

> 字段类型与长度为 MVP 建议值；后续可按实际需求扩展。

### 5.1 sys_user（用户）

**用途**：登录/注册、角色（学生/社团管理员/平台管理员）、账号状态。

关键字段：
- `account`：登录账号（手机号/邮箱/学号等统一入口）
- `password_hash`：密码摘要（不存明文）
- `role`：角色（1学生/2社团管理员/3平台管理员）
- `status`：状态（1启用/2禁用）

索引与约束：
- `uk_sys_user_account`：账号唯一
- `uk_sys_user_student_no`：学号唯一（允许 NULL）

### 5.2 club（社团）

**用途**：社团基本信息、创建申请与平台审核状态（MVP 将申请与实体合并，用状态字段区分）。

关键字段：
- `name`：社团名称（唯一）
- `type`：社团类型（建议：1学术/2文体/3公益/4兴趣）
- `status`：状态（0待审/1上架/2驳回/3下架）
- `reject_reason/reviewed_by/reviewed_at`：审核信息

索引与约束：
- `uk_club_name`：社团名唯一
- `idx_club_status_type`：广场筛选

### 5.3 join_request（入社申请）

**用途**：学生申请加入社团、社团管理员审核。

关键字段：
- `club_id/user_id`：申请主体
- `status`：0待审核/1通过/2驳回/3取消
- `reject_reason/reviewed_by/reviewed_at`：审核信息

索引与约束：
- `uk_join_request_club_user`：同一用户对同一社团仅 1 条申请（便于“修改理由/重复提交控制”）
- `idx_join_request_club_status`：后台列表按社团与状态筛选

### 5.4 club_member（社团成员）

**用途**：成员关系、成员角色（P1）与移除。

关键字段：
- `role`：1普通成员/2管理员（P1）
- `status`：1正常/2已移除

索引与约束：
- `uk_club_member_club_user`：成员唯一
- `idx_club_member_club`：按社团查成员
- `idx_club_member_user`：按用户查我的社团

### 5.5 activity（活动）

**用途**：活动创建/发布/下架、报名规则（上限/截止）。

关键字段：
- `signup_limit`：0 表示不限
- `signup_count`：报名人数汇总（避免每次统计全表；由报名事务维护）
- `status`：0草稿/1已发布/2已下架

索引与约束：
- `idx_activity_club_status_start`：社团活动列表
- `idx_activity_deadline`：截止时间筛选/任务（可选）

### 5.6 activity_signup（报名）

**用途**：活动报名/取消报名（MVP：截止前可取消）。

关键字段：
- `status`：1已报名/2已取消
- 唯一约束：`activity_id + user_id` 防止重复报名

索引与约束：
- `uk_activity_signup_activity_user`
- `idx_activity_signup_user`：我的报名

### 5.7 announcement（公告）

**用途**：社团公告发布、阅读展示（已读 P1 可扩展为阅读表）。

关键字段：
- `content`：TEXT
- `status`：1已发布/2已下架
- `published_at/published_by`：发布时间/发布人

索引与约束：
- `idx_announcement_club_status_time`：社团公告列表

### 5.8 notification（站内通知）

**用途**：审核结果/入社结果/系统通知等统一承载；已读 P1。

关键字段：
- `type`：通知类型（如：1入社结果/2社团审核/3活动/4系统）
- `ref_type/ref_id`：关联业务对象（如：club/join_request/activity/announcement）
- `is_read/read_at`：已读标记（P1）

索引与约束：
- `idx_notification_user_read_time`：消息列表与未读统计

### 5.9 audit_log（审计日志）

**用途**：关键动作留痕（审核通过/驳回、成员移除、活动下架、公告下架等）。

关键字段：
- `actor_user_id`：操作人
- `action`：动作标识（字符串，便于扩展）
- `target_type/target_id`：对象
- `reason`：原因（可空）
- `detail`：JSON 扩展（可空）

索引与约束：
- `idx_audit_target`：按对象查审计
- `idx_audit_actor_time`：按操作人/时间查询

## 6. 索引策略与查询示例（MVP）

- 社团广场：
  - 条件：`status=1` + `type` + 关键字（可使用 name 前缀/模糊匹配）
  - 索引：`idx_club_status_type` + `uk_club_name`
- 入社审核列表：
  - 条件：`club_id` + `status=0`
  - 索引：`idx_join_request_club_status`
- 我的报名：
  - 条件：`user_id` + `status`
  - 索引：`idx_activity_signup_user`
- 防重复报名：
  - 约束：`uk_activity_signup_activity_user`

## 7. 初始化脚本

- 文件位置：`sql/init_schema_mvp.sql`
- 内容：建库、建表、索引、唯一约束、字段注释

