package com.zoen.sys.service.audit.impl;

import com.zoen.sys.domain.entity.AuditLog;
import com.zoen.sys.infrastructure.mapper.AuditLogMapper;
import com.zoen.sys.service.audit.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author zone
 * @description 审计日志服务实现
 * @since 2026-05-01
 */
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogMapper auditLogMapper;

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
    @Override
    public void write(Long actorUserId, String action, String targetType, Long targetId, String reason, String detailJson) {
        AuditLog log = AuditLog.builder()
                .actorUserId(actorUserId)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .reason(reason)
                .detail(detailJson)
                .build();
        auditLogMapper.insert(log);
    }
}

