# 功能开发清单：大学生社团管理系统（MVP v1.0）

> 依据文档：[PRD-大学生社团管理系统-MVP-v1.0.md](file:///Users/shoanjen/Desktop/AI%20SpringBoot%20Vue/boot-vue-sys/documents/PRD-%E5%A4%A7%E5%AD%A6%E7%94%9F%E7%A4%BE%E5%9B%A2%E7%AE%A1%E7%90%86%E7%B3%BB%E7%BB%9F-MVP-v1.0.md)  
> 接口规范：统一前缀 `/api/v1/{模块名}/...`（如 `/api/v1/club/list`）

## 1. 目标与范围

### 1.1 MVP 目标（P0）
- 学生端：登录/注册 → 社团广场 → 社团详情 → 入社申请 → 审核通过 → 活动报名 → 我的中心闭环
- 社团管理员端：入社审核、成员管理、活动管理、公告发布
- 平台管理员端：社团创建审核（通过/驳回+原因记录）

### 1.2 非范围（本期不做/可后置）
- 证明材料上传（P1）
- 公告已读、站内通知已读（P1）
- 社团下架/封禁、内容违规自动识别/举报（P1）
- 报名名单导出CSV（P1）

## 2. 功能模块清单（按优先级）

| 模块 | 功能点 | 优先级 | 说明/验收要点 |
|---|---|---|---|
| 账号与权限 | 注册/登录/退出 | P0 | 账号+密码，登录后保持会话/Token；退出清除登录态 |
| 账号与权限 | 角色与权限（学生/社团管理员/平台管理员） | P0 | 至少路由与接口鉴权；防越权 |
| 社团广场 | 社团列表/搜索/筛选 | P0 | 支持关键词、类型筛选；分页 |
| 社团广场 | 社团详情展示 | P0 | 展示介绍、活动/公告入口；不可用社团隐藏关键入口 |
| 社团入驻 | 创建社团申请 | P0 | 提交资料→待审；名称唯一；重复提交提示 |
| 社团入驻 | 平台审核（通过/驳回） | P0 | 驳回需原因；审核记录可追溯；通过后申请人成为社团管理员 |
| 入社流程 | 申请加入社团 | P0 | 申请理由10-200；重复申请校验；社团停招/下架限制 |
| 入社流程 | 社团审核入社申请 | P0 | 通过/驳回；驳回原因可选（建议）；记录审核人/时间 |
| 成员管理 | 成员列表/移除成员 | P0 | 权限：社团管理员；移除留痕 |
| 活动管理 | 创建/编辑/发布/下架活动 | P0 | 状态管理；时间合法性校验；截止<=开始 |
| 活动管理 | 活动报名/取消报名 | P0 | 成员校验、重复报名、满员、截止；取消规则：截止前可取消 |
| 公告通知 | 发布公告 | P0 | 标题2-50；内容10-3000；发布人记录 |
| 公告通知 | 公告列表/详情 | P0 | 列表分页；详情可访问控制（可先不限制） |
| 我的 | 我的社团/我的报名/我的申请记录 | P0 | 核心闭环；状态清晰可追踪 |
| 审计与追踪 | 审核/移除/下架等关键操作记录 | P0 | 记录对象、动作、原因、操作人、时间 |
| 公告通知 | 公告已读状态 | P1 | 个人维度已读标记 |
| 通知 | 站内通知/已读 | P1 | 审核结果、关键动作触达与已读 |
| 运营与风控 | 社团下架/封禁/内容人工审核 | P1 | MVP可仅后台操作 |
| 活动管理 | 报名名单查看/导出CSV | P1 | 导出CSV；权限控制 |
| 社团入驻 | 证明材料上传 | P1 | 图片/文件上传与存储 |

## 3. 后端开发清单（API + 规则）

### 3.1 Auth 模块（/api/v1/auth）
- [P0] `POST /api/v1/auth/register` 注册（账号/密码/基础信息）
- [P0] `POST /api/v1/auth/login` 登录（账号/密码 → Token/会话）
- [P0] `POST /api/v1/auth/logout` 退出
- [P0] 登录态中间件/拦截器：解析Token、注入当前用户上下文
- [P0] RBAC：学生/社团管理员/平台管理员权限矩阵

### 3.2 Club 模块（/api/v1/club）
- [P0] `GET /api/v1/club/list` 社团列表（分页、keyword、type）
- [P0] `GET /api/v1/club/detail` 社团详情（clubId）
- [P0] `POST /api/v1/club/applyCreate` 创建社团申请
- [P0] `GET /api/v1/club/my` 我创建/我管理/我加入社团列表（可拆分）
- [P0] 数据校验：名称唯一、简介长度、联系方式长度
- [P0] 状态机：待审(PENDING)/上架(APPROVED)/驳回(REJECTED)/下架(OFFLINE-P1)

### 3.3 Join 模块（/api/v1/join）
- [P0] `POST /api/v1/join/apply` 提交入社申请（clubId、reason）
- [P0] `GET /api/v1/join/myList` 我的入社申请列表
- [P0] `GET /api/v1/join/pendingList` 社团待审入社列表（clubId）
- [P0] `POST /api/v1/join/review` 审核入社（requestId、action、reason可选）
- [P0] 规则：重复申请（同club同user PENDING）禁止；非上架/停招禁止；通过后写入成员表

### 3.4 Member 模块（/api/v1/member）
- [P0] `GET /api/v1/member/list` 成员列表（clubId）
- [P0] `POST /api/v1/member/remove` 移除成员（memberId/clubId+userId）
- [P1] `POST /api/v1/member/setAdmin` 设置/取消管理员

### 3.5 Activity 模块（/api/v1/activity）
- [P0] `POST /api/v1/activity/create` 创建活动
- [P0] `POST /api/v1/activity/update` 编辑活动
- [P0] `POST /api/v1/activity/publish` 发布活动
- [P0] `POST /api/v1/activity/offline` 下架活动
- [P0] `GET /api/v1/activity/list` 活动列表（clubId、分页）
- [P0] `POST /api/v1/activity/signup` 报名（activityId）
- [P0] `POST /api/v1/activity/cancelSignup` 取消报名（activityId）
- [P1] `GET /api/v1/activity/signupList` 报名名单
- [P1] `GET /api/v1/activity/exportSignupCsv` 导出CSV
- [P0] 并发与约束：`(activityId,userId)` 唯一；capacity>0 时防超卖（事务/乐观锁）
- [P0] 规则：非成员不可报名；截止后不可报名；截止前可取消，截止后不可取消

### 3.6 Announcement 模块（/api/v1/announcement）
- [P0] `POST /api/v1/announcement/publish` 发布公告
- [P0] `GET /api/v1/announcement/list` 公告列表（clubId、分页）
- [P0] `GET /api/v1/announcement/detail` 公告详情（announcementId）
- [P1] `POST /api/v1/announcement/read` 标记已读（个人维度）

### 3.7 Platform Review 模块（/api/v1/platform）
- [P0] `GET /api/v1/platform/club/pendingList` 待审核社团列表
- [P0] `POST /api/v1/platform/club/review` 审核社团（clubId、action、rejectReason）
- [P1] `POST /api/v1/platform/club/offline` 下架/封禁社团

### 3.8 审计/日志模块（/api/v1/audit 或内部写入）
- [P0] 审计写入点：社团审核、入社审核、成员移除、活动发布/下架、公告发布
- [P0] 审计查询（可选）：平台/社团管理员可查询最近操作记录

## 4. 数据库开发清单（表结构与约束）

> 仅列“必须表”与关键约束；字段明细以数据库设计文档为准。

- [P0] users（账号、密码摘要、角色、状态、创建时间）
- [P0] clubs（名称唯一、类型、简介、状态、创建人、负责人/管理员）
- [P0] club_members（clubId+userId 唯一；role；status）
- [P0] join_requests（clubId+userId+PENDING 唯一约束或程序保证；reviewer、reviewedAt、rejectReason）
- [P0] activities（clubId、时间地点、capacity、deadline、status）
- [P0] activity_signups（activityId+userId 唯一；status；createdAt；canceledAt）
- [P0] announcements（clubId、title、content、status、publisher、createdAt）
- [P0] audit_logs（objectType/objectId/action/reason/operatorUserId/createdAt）
- [P1] notification、announcement_reads（个人已读）

## 5. 前端开发清单（页面 + 交互）

### 5.1 公共能力
- [P0] 登录态管理：Token/会话存储、退出清理、路由守卫、重定向回跳
- [P0] 角色路由：学生端/社团管理员端/平台管理员端入口与菜单差异
- [P0] API封装：统一错误提示、加载态、表单校验提示

### 5.2 学生端页面
- [P0] 登录页（角色选择：学生/社团管理员/平台管理员；登录成功跳转对应首页）
- [P0] 注册页（学生注册；字段校验）
- [P0] 社团广场（列表、搜索、类型筛选、分页）
- [P0] 社团详情（活动/公告/介绍 Tab；申请入社弹窗；活动报名弹窗）
- [P0] 创建社团申请（表单校验、提交成功提示、跳转“我的申请”）
- [P0] 我的中心（我的社团/我的报名/我的申请 Tab；取消报名规则）

### 5.3 社团管理员后台
- [P0] 入社审核：列表+通过/驳回（驳回原因）
- [P0] 成员管理：列表+移除（P1：角色设置）
- [P0] 活动管理：创建/发布/下架、活动列表（P1：报名名单/导出）
- [P0] 公告管理：发布公告、公告列表

### 5.4 平台管理员后台
- [P0] 社团创建审核：待审核列表、审核详情弹窗、通过/驳回（原因必填）
- [P1] 社团下架/封禁

## 6. 测试与验收清单（冒烟用例）

### 6.1 核心流程冒烟
- [P0] 注册 → 登录 → 浏览社团 → 查看详情 → 提交入社申请 → 管理员通过 → 成员报名活动 → 我的报名可见
- [P0] 管理员创建活动 → 发布 → 成员报名成功 → 截止后不能报名/不能取消
- [P0] 学生创建社团申请 → 平台管理员通过/驳回（驳回原因） → 状态正确、可追溯

### 6.2 关键校验
- [P0] 重复入社申请禁止
- [P0] 非成员报名提示“加入社团后可报名”
- [P0] 报名满员/截止提示正确
- [P0] 鉴权：未登录访问受限页面自动跳转登录；越权接口拒绝

## 7. 交付物清单

- [P0] 后端：接口实现 + 鉴权/RBAC + 核心表结构 + 审计留痕
- [P0] 前端：学生端/社团后台/平台后台页面可操作可演示
- [P0] 文档：接口清单/联调说明（如需可追加）

