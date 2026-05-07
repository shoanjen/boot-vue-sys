package com.zoen.sys.service.system.impl;

import com.zoen.sys.service.system.SystemService;
import org.springframework.stereotype.Service;

/**
 * @author zone
 * @description 系统基础服务实现类
 * @since 2026-05-01
 */
@Service
public class SystemServiceImpl implements SystemService {

    /**
     * @description 健康检查
     * @return 健康状态
     */
    @Override
    public String ping() {
        return "ok";
    }
}
