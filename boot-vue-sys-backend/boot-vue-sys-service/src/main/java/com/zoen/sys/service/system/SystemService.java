package com.zoen.sys.service.system;

/**
 * @author zone
 * @description 系统基础服务
 * @since 2026-05-01
 */
public interface SystemService {

    /**
     * @description 健康检查
     * @return 健康状态
     */
    String ping();
}
