package com.zoen.sys.service.me;

import com.zoen.sys.service.me.model.MyCenterData;

/**
 * @author zone
 * @description 我的中心服务
 * @since 2026-05-01
 */
public interface MeService {

    /**
     * @param userId 用户ID
     * @description 获取我的中心数据
     * @return 我的中心数据聚合
     */
    MyCenterData getMyCenter(Long userId);
}

