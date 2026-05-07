package com.zoen.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author zone
 * @description 审计日志表实体
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("audit_log")
public class AuditLog {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 操作人用户ID
     */
    @TableField("actor_user_id")
    private Long actorUserId;

    /**
     * 动作标识
     */
    private String action;

    /**
     * 对象类型
     */
    @TableField("target_type")
    private String targetType;

    /**
     * 对象ID
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 原因
     */
    private String reason;

    /**
     * 扩展详情JSON
     */
    @TableField("detail")
    private String detail;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}

