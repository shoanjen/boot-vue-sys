# 技术选型方案：大学生社团管理系统（MVP）

> 约束：整体框架采用 Spring Boot + Vue + JDK 21；数据库采用 MySQL；API 文档采用 Swagger 3。

## 1. 文档信息

| 项目 | 内容 |
|------|------|
| 文档版本 | v1.0 |
| 创建日期 | 2026-05-01 |
| 适用范围 | MVP（社团广场/入社/活动/公告/管理后台/平台审核） |
| 对应 PRD | documents/PRD-大学生社团管理系统-MVP-v1.0.md |

## 2. 总体架构

### 2.1 架构形态
- 后端：单体 Spring Boot（分层/DDD 借鉴的模块组织），对外 REST API
- 前端：Vue 3 SPA（PC 端 Web 优先）
- 数据：MySQL 单库（避免多表关联查询，单表查询 + 代码聚合符合当前项目规范）

### 2.2 模块边界（与 PRD P0 对齐）
- auth：登录/注册/退出、Token 下发与校验
- club：社团广场、社团详情、创建社团申请、平台审核
- join：入社申请、入社审核、成员关系
- activity：活动 CRUD、报名/取消报名、报名名单（P1 导出）
- announcement：公告发布、列表/详情（已读 P1）
- notify：站内通知（MVP 可以先落库，读取展示）

### 2.3 接口路径规范（遵循项目规范）
- 统一风格：`/api/v1/{模块名}/...`
- 示例：
  - `/api/v1/auth/login`
  - `/api/v1/club/list`
  - `/api/v1/club/applyCreate`
  - `/api/v1/club/review`
  - `/api/v1/join/apply`
  - `/api/v1/join/review`
  - `/api/v1/activity/create`
  - `/api/v1/activity/signup`
  - `/api/v1/announcement/publish`

## 3. 后端技术选型（Spring Boot + JDK21）

### 3.1 运行时与构建
- JDK：21（LTS）
- Spring Boot：3.3.x（兼容 Java 21，Spring Framework 6）
- 构建工具：Maven（建议统一依赖版本管理）

### 3.2 Web 与基础能力
- Web 框架：`spring-boot-starter-web`（Spring MVC）
- 参数校验：`spring-boot-starter-validation`（JSR 380）
- JSON：Jackson（Spring Boot 默认）
- 配置：`application.yml` 分环境（dev/test/prod）

### 3.3 鉴权与权限（RBAC 最小集）
推荐方案（MVP 优先落地、长期可演进）：
- Spring Security 6（与 Boot 3 配套）
- Token：JWT（无状态，适合前后端分离）
- 权限模型：角色（学生/社团管理员/平台管理员） + 资源接口鉴权（可先做到角色级别）

备选方案（更轻量、开发效率更高）：
- Sa-Token（如果团队更偏向快速落地与简化配置）

### 3.4 数据访问（MySQL）
- 数据库：MySQL 8.0+
- ORM/DAO：MyBatis-Plus（符合项目“避免原始 SQL、尽量使用 MyBatis-Plus 注解”的约束）
- 连接池：HikariCP（Spring Boot 默认）
- 分页：MyBatis-Plus 分页插件

关键约束（与项目规范一致）：
- 尽量避免多表 JOIN；复杂汇总用「单表查询 + Java 代码聚合/stream」实现
- 报名防重复/防超卖：建议数据库唯一约束（`activity_id + user_id`）+ 事务/乐观锁策略

### 3.5 API 文档（Swagger 3）
- 方案：springdoc-openapi（OpenAPI 3 / Swagger UI）
- 依赖建议：`springdoc-openapi-starter-webmvc-ui` 2.x
- 规范要求：Controller 需要补全接口注释（路径、参数、返回、错误码），满足项目注释规范

### 3.6 统一返回与异常处理
- 统一响应结构：`success/data/error` 或 `code/message/data`（建议全局统一）
- 全局异常处理：`@ControllerAdvice` + 自定义业务异常
- 校验异常：统一转为明确可读的错误信息（字段级提示）

### 3.7 日志与审计
- 日志：Logback（默认）+ 结构化字段（建议记录 traceId、userId、module、action）
- 审计：对审核/下架/成员移除/活动下架等关键动作写入 `audit_log`（MVP 可先落库）

### 3.8 可选组件（按 MVP 复杂度开关）
- 缓存：Redis（若列表访问量大或需通知未读数，后续可引入；MVP 可不引）
- 文件上传（P1）：对象存储（MinIO/阿里 OSS/腾讯 COS）+ 回调/白名单校验
- 数据库迁移：Flyway（推荐）或 Liquibase（保证多环境一致性）

## 4. 前端技术选型（Vue）

### 4.1 基础框架与工程化
- Vue：Vue 3
- 构建：Vite
- 语言：TypeScript（推荐，降低后期维护成本）
- 路由：Vue Router 4
- 状态管理：Pinia
- HTTP：Axios（统一封装拦截器：Token、错误码处理、重试策略可选）

### 4.2 UI 与体验
推荐（后台管理常用）：
- 组件库：Element Plus
- 图表（可选）：ECharts（用于社团/平台统计 P1）
- 表格：优先 Element Plus Table（MVP 足够）

### 4.3 权限与路由控制
- 路由守卫：根据角色控制访问（学生端/社团后台/平台后台）
- 菜单渲染：按角色动态生成
- Token 存储：优先 `localStorage`（MVP）+ 过期处理；若强调安全可改为 httpOnly Cookie（需要后端配合）

### 4.4 代码规范与质量
- ESLint + Prettier（统一代码风格）
- 提交规范：建议采用 Conventional Commits（可选）

## 5. 数据库设计建议（与 PRD 实体对齐）

### 5.1 核心表（MVP）
- user：用户（账号/密码摘要/角色/状态）
- club：社团（名称/类型/简介/状态：待审/上架/驳回/下架）
- join_request：入社申请（理由/状态/审核信息）
- club_member：成员关系（角色/状态/加入时间）
- activity：活动（时间地点/上限/截止/状态）
- activity_signup：报名（唯一约束：activity_id + user_id）
- announcement：公告（标题/内容/状态）
- notification：站内通知（type、refId、已读标记 P1）
- audit_log：审计日志（对象、动作、原因、操作人、时间）

### 5.2 索引与约束（关键）
- 唯一约束：
  - `club.name`（防止社团重名）
  - `activity_signup(activity_id, user_id)`（防重复报名）
- 常用索引：
  - `club(status, type)`、`club(created_at)`
  - `join_request(club_id, status)`、`join_request(user_id, status)`
  - `activity(club_id, status, start_time)`
  - `announcement(club_id, status, publish_time)`

## 6. 开发落地建议（与项目规范对齐）

### 6.1 分层与职责
- Controller：只做参数接收/校验/鉴权/调用 Service，不写业务逻辑
- ServiceImpl：承载业务流程编排（入社审核、报名校验、状态流转）
- Infrastructure：MyBatis-Plus Mapper、第三方集成、缓存等

### 6.2 关键业务点实现策略
- 入社申请防重复：同一用户对同一社团同一状态（待审核）限制提交
- 报名并发控制：
  - 唯一约束防重复报名
  - 上限控制：建议在报名表插入前做余量校验，并在事务中更新活动报名数或使用乐观锁版本字段
- 审核可追溯：审核表/审计表记录原因与操作人

## 7. 版本建议（可在落地时锁定）

> 版本以落地开发时的仓库依赖管理为准，以下为建议区间。

- Spring Boot：3.3.x
- Spring Security：6.x
- springdoc-openapi：2.x（Swagger 3 / OpenAPI 3）
- MyBatis-Plus：3.5.x
- MySQL：8.0.x
- Vue：3.x
- Vite：5.x/6.x
- Element Plus：2.x
- Pinia：2.x
- Vue Router：4.x
- Axios：1.x

## 8. 风险与取舍

- MVP 先单体：降低运维与联调成本，后续按模块拆分为微服务/独立部署组件
- 避免多表关联：提升可控性，但需要在 Service 层做聚合与缓存策略（后续可优化）
- Swagger 文档维护成本：通过统一注解规范与接口返回模型约束降低成本

