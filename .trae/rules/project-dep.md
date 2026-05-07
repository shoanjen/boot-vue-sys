> 项目开发规范
# 开发规范
1. 接口路径规范
   - 接口路径：模块/api/v1/模块名/
   - 示例用户信息编辑功能：/api/v1/user/updateUserInfo
2. controller规范：
   - controller中的方法，不要出现业务逻辑处理相关的代码，统一放到serviceImpl对应的实现类中的方法处理
3. 工具类规范：
   - 在工具类上标识lombok的注解@UtilityClass,具体的方法上就无需static关键字标识
4. 注释规范：
   - 关键功能代码要有具体的注释
   - 类注释模板：
   ```java
   /**
   *@author zone
   *@description 类的具体作用描述
   *@since 2025-xx-xx 当前年月日
   */
   ```
   - 方法注释模板
   ```java
   /**
   *@param 方法参数
   *@description 方法的具体作用描述
   *@return 方法返回值类型
   */
   ```
   - 实体类相关上的字段要有具体的注释
   ```java
   /**
   */
   ```
5.类规范
  - 尽量采用lombok注解进行简略getter和setter方法。
6. 数据库操作规范
  - 不要采用多表关联操作，尽量采用单表查询，最后通过代码逻辑+stream流特性进行结果获取。
  - 数据库操作中，要避免使用原始SQL语句，尽量使用MyBatis-Plus的注解进行操作。
7. 如果使用到API文档相关技术栈比如Swagger或者knife4j，要确保在controller中添加具体的注释，包括接口路径、参数、返回值等。
