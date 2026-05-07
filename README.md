# 大学生社团管理系统demo

# 注意⚠️：
功能或许不完善，存在bug。

# 项目总结与架构说明（boot-vue-sys）

## 1. 项目概览

本项目为“大学生社团管理系统（MVP）”的前后端一体仓库，目标是打通“注册/登录 → 社团广场 → 入社申请/审核 → 活动/公告 → 我的中心/平台审核”的最小闭环。

- 前端：Vue 3 + Vite + TypeScript（开发端口 5174），通过 Vite Proxy 将 `/api` 转发到后端 `http://localhost:8081`
- 后端：Spring Boot 多模块（端口 8081）+ MyBatis-Plus + MySQL
- 数据库：MySQL 库 `club_mgmt`（见 [application.yml](file:///Users/shoanjen/Desktop/AI%20SpringBoot%20Vue/boot-vue-sys/boot-vue-sys-backend/boot-vue-sys-application/src/main/resources/application.yml#L1-L26)）
- 接口规范：`/api/v1/{模块}/...`（例如：`/api/v1/auth/login`）
- 鉴权：`Authorization: Bearer <token>`，后端通过拦截器解析 token，并基于注解做角色访问控制

## 2. 项目目录（注释标注）

```
boot-vue-sys/
├── boot-vue-sys-web/                          # 前端工程（Vue3/Vite）
│   ├── src/
│   │   ├── api/                               # API 调用层（封装 axios + 对接后端 /api/v1/...）
│   │   ├── pages/                             # 业务页面（登录/注册/学生端/管理员端）
│   │   ├── layouts/                           # 布局组件（Auth/Admin/Student）
│   │   ├── stores/                            # Pinia 状态（登录态/全局 UI/应用）
│   │   ├── router/                            # 路由与路由守卫（requiresAuth）
│   │   ├── components/                        # 通用组件（Modal/Toast）
│   │   ├── types/                             # 前端领域类型（UserRole、Club 等）
│   │   └── mock/                              # 演示用本地 mock 数据（非主链路）
│   ├── vite.config.ts                         # Vite 配置（5174 + /api 代理到 8081）
│   └── package.json                           # 前端依赖与脚本
│
├── boot-vue-sys-backend/                      # 后端工程（Spring Boot 多模块聚合）
│   ├── boot-vue-sys-application/              # 应用启动模块（唯一入口、配置、启动初始化）
│   │   ├── .../BootVueSysApplication.java     # Spring Boot 启动类
│   │   ├── .../BootstrapDataInitializer.java  # 空库场景的演示数据初始化
│   │   └── resources/application.yml          # 端口/数据源/MyBatis-Plus/鉴权配置
│   │
│   ├── boot-vue-sys-web/                      # Web 接入层（Controller/Interceptor/DTO/异常）
│   │   ├── auth/                              # 鉴权上下文、拦截器、权限注解
│   │   ├── controller/v1/                     # v1 API Controller（只做参数接收/返回封装）
│   │   ├── model/request/                     # 请求 DTO（xxxRequest）
│   │   ├── model/response/                    # 响应 DTO（xxxResponse）
│   │   └── exception/                         # 全局异常处理（业务异常/系统异常）
│   │
│   ├── boot-vue-sys-service/                  # 业务服务层（Service 接口 + impl 编排业务逻辑）
│   │   ├── auth/                              # 登录注册、token 生成/校验
│   │   ├── club/join/member/activity/...      # 社团/入社/成员/活动/公告/平台审核等服务
│   │   └── .../impl/                          # 业务实现（Controller 不写业务逻辑）
│   │
│   ├── boot-vue-sys-domain/                   # 领域层（实体/枚举/领域模型）
│   │   ├── entity/                            # SysUser/Club/JoinRequest/...（MyBatis-Plus 实体）
│   │   └── enums/                             # 枚举（UserRole/状态机等）
│   │
│   ├── boot-vue-sys-infrastructure/           # 基础设施层（数据访问/第三方集成）
│   │   ├── config/                            # MyBatis-Plus 配置
│   │   └── mapper/                            # BaseMapper（单表 CRUD，避免多表 JOIN）
│   │
│   └── boot-vue-sys-common/                   # 通用层（通用模型/异常/枚举码等）
│       ├── model/ApiResponse.java             # 统一返回体
│       └── exception/*                        # BizException/ErrorCode
│
├── sql/init_schema_mvp.sql                    # MVP 数据库初始化脚本（建表/索引/演示数据）
├── documents/                                 # 项目文档（PRD/清单/设计/进度/本说明）
└── prototype/                                 # 原型参考（HTML 页面）
```

## 3. 后端整体架构说明

### 3.1 分层与职责边界（DDD 借鉴）

后端按“接入层（web）→ 业务层（service）→ 领域层（domain）→ 基础设施（infrastructure）→ 通用（common）→ 启动（application）”拆分：

- web：负责 HTTP 参数校验、DTO 组装、返回 ApiResponse、鉴权拦截；不承载业务逻辑
- service：承载业务逻辑编排与校验（核心规则、状态变更、写审计日志等）
- domain：实体与枚举，表达核心业务概念与状态机
- infrastructure：MyBatis-Plus Mapper / 配置等数据访问能力
- common：ApiResponse、BizException、ErrorCode 等全局通用能力
- application：唯一启动入口、配置、Bootstrap 初始化（空库补演示数据）

### 3.2 典型请求链路（以受保护接口为例）

1) 前端携带 `Authorization: Bearer <token>` 请求后端  
2) [AuthInterceptor](file:///Users/shoanjen/Desktop/AI%20SpringBoot%20Vue/boot-vue-sys/boot-vue-sys-backend/boot-vue-sys-web/src/main/java/com/zoen/sys/web/auth/AuthInterceptor.java) 根据 `@RequireRole` 判断是否需要鉴权  
3) token 校验通过后写入 [AuthContextHolder](file:///Users/shoanjen/Desktop/AI%20SpringBoot%20Vue/boot-vue-sys/boot-vue-sys-backend/boot-vue-sys-web/src/main/java/com/zoen/sys/web/auth/AuthContextHolder.java)（ThreadLocal）  
4) Controller 调用 Service（业务逻辑在 ServiceImpl 内完成）  
5) Service 通过 Mapper 做单表查询/更新（避免多表 JOIN），并用代码聚合结果  
6) 返回 [ApiResponse](file:///Users/shoanjen/Desktop/AI%20SpringBoot%20Vue/boot-vue-sys/boot-vue-sys-backend/boot-vue-sys-common/src/main/java/com/zoen/sys/common/model/ApiResponse.java)；异常由全局异常处理器统一转换

### 3.3 鉴权与权限模型

- token 生成/校验：由 service 层 [AuthTokenServiceImpl](file:///Users/shoanjen/Desktop/AI%20SpringBoot%20Vue/boot-vue-sys/boot-vue-sys-backend/boot-vue-sys-service/src/main/java/com/zoen/sys/service/auth/impl/AuthTokenServiceImpl.java) 实现
- 权限控制：web 层使用 `@RequireRole({角色码...})` 声明允许访问的角色；拦截器做校验

### 3.4 异常与返回体策略

- 业务异常：抛 `BizException`，由 [GlobalExceptionHandler](file:///Users/shoanjen/Desktop/AI%20SpringBoot%20Vue/boot-vue-sys/boot-vue-sys-backend/boot-vue-sys-web/src/main/java/com/zoen/sys/web/exception/GlobalExceptionHandler.java) 转为明确的业务错误码与 message
- 系统异常：统一返回 `INTERNAL_ERROR/系统异常`，并输出服务端错误日志用于定位

## 4. 前端整体架构说明

### 4.1 目录职责

- `src/api/`：统一封装 HTTP（[http.ts](file:///Users/shoanjen/Desktop/AI%20SpringBoot%20Vue/boot-vue-sys/boot-vue-sys-web/src/api/http.ts)），并按业务拆分调用文件（auth/club/join/activity/announcement/platform/me/member/system）
- `src/stores/`：Pinia 管理登录态与 UI（toast 等）；登录态存储 key 为 `boot_vue_sys_auth_v1`
- `src/router/`：路由定义 + `requiresAuth` 守卫（未登录自动跳转 `/login`）
- `src/pages/`：页面按角色分区：`auth/`、`student/`、`admin/`
- `src/layouts/`：布局组件复用顶部导航与侧边栏结构

### 4.2 联调机制

- Vite 开发端口固定为 5174；配置见 [vite.config.ts](file:///Users/shoanjen/Desktop/AI%20SpringBoot%20Vue/boot-vue-sys/boot-vue-sys-web/vite.config.ts#L1-L15)
- 所有 `/api/**` 请求通过代理转发到后端 8081，从而避免跨域与重复写 baseURL

## 5. 核心模块与 MVP 业务闭环映射（简表）

- 认证：`/api/v1/auth/*`（登录/注册），前端对应 `pages/auth/*` + `stores/auth.ts`
- 学生端：
  - 社团广场：`pages/student/ClubPlazaPage.vue`（列表/检索/我的申请）
  - 社团详情：`pages/student/ClubDetailPage.vue`（申请入社/活动报名/公告查看）
  - 创建社团：`pages/student/CreateClubPage.vue`（提交创建申请）
  - 我的中心：`pages/student/MyCenterPage.vue`（我的社团/报名/申请）
- 管理端：
  - 社团管理员后台：`pages/admin/ClubAdminDashboardPage.vue`（入社审核/成员/活动/公告）
  - 平台管理员后台：`pages/admin/PlatformAdminDashboardPage.vue`（社团创建审核）

