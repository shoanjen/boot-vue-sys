package com.zoen.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author zone
 * @description 入社申请表实体
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("join_request")
public class JoinRequest {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 社团ID
     */
    @TableField("club_id")
    private Long clubId;

    /**
     * 申请人用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 状态：0待审核 1通过 2驳回 3取消
     */
    private Integer status;

    /**
     * 审核人用户ID
     */
    @TableField("reviewed_by")
    private Long reviewedBy;

    /**
     * 审核时间
     */
    @TableField("reviewed_at")
    private LocalDateTime reviewedAt;

    /**
     * 驳回原因
     */
    @TableField("reject_reason")
    private String rejectReason;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}

