package com.zoen.sys.web.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zone
 * @description 方法权限控制注解
 * @since 2026-05-01
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /**
     * @description 允许访问的角色码数组
     * @return 角色码数组
     */
    int[] value();
}

