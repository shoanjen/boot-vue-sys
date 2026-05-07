package com.zoen.sys.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zone
 * @description MyBatis-Plus 基础配置
 * @since 2026-05-01
 */
@Configuration
@MapperScan("com.zoen.sys.infrastructure.mapper")
public class MyBatisPlusConfig {
}

