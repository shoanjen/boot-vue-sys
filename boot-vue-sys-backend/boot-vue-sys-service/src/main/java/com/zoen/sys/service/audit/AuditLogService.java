package com.zoen.sys.service.audit;

/**
 * @author zone
 * @description 审计日志服务
 * @since 2026-05-01
 */
public interface AuditLogService {

    /**
     * @param actorUserId 操作人用户ID
     * @param action 动作标识
     * @param targetType 对象类型
     * @param targetId 对象ID
     * @param reason 原因
     * @param detailJson 扩展详情JSON
     * @description 记录审计日志
     * @return 无
     */
    void write(Long actorUserId, String action, String targetType, Long targetId, String reason, String detailJson);
}

