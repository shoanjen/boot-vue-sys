> 项目结构
> 
# 前言
后端项目遵循借鉴DDD领域驱动模型，参照以下结构
```
shoan-boot
├── pom.xml                        # 根 POM，聚合与依赖版本管理
├── shoan-boot-common             # 通用模块（工具类/常量/通用配置等）
├── shoan-boot-domain             # 领域模块（实体/值对象/领域服务）
├── shoan-boot-infrastructure     # 基础设施模块（数据访问/MyBatis-Plus/第三方集成）
├── shoan-boot-service            # 业务服务模块（业务逻辑编排）
├── shoan-boot-web                # Web 接入层（Controller/Interceptor/Exception/Swagger配置）
└── shoan-boot-application        # 应用启动模块（唯一入口，打包为可执行Jar）
```
# 项目创建
1. 项目名称：boot-vue-sys，包含前端以及后端项目名称
2. 包名前缀：com.zoen.项目名称最后一个‘-’后的单词
3. 前端项目名称为：boot-vue-sys-web
4. 后端项目名称为：boot-vue-sys-backend
<!-- # 其他规范
8. 前端请求的实体请求参数应该以xxxRequest为格式，返回实体的响应参数应该以xxxResponse为格式。并且这两个请求实体类和响应实体类应当放到`boot-vue-sys-web/src/main/java/boot/deploy/platform/web/model/request`和`boot-vue-sys-web/src/main/java/boot/deploy/platform/web/model/response`包下 -->
