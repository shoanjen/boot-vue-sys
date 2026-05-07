package com.zoen.sys.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zone
 * @description 应用启动入口
 * @since 2026-05-01
 */
@SpringBootApplication(scanBasePackages = "com.zoen.sys")
public class BootVueSysApplication {

    /**
     * @param args 启动参数
     * @description 应用启动入口
     * @return 无
     */
    public static void main(String[] args) {
        SpringApplication.run(BootVueSysApplication.class, args);
    }
}
