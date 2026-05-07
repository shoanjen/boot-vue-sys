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
 * @description 社团表实体
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("club")
public class Club {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 社团名称
     */
    private String name;

    /**
     * 社团类型：1学术 2文体 3公益 4兴趣
     */
    private Integer type;

    /**
     * 社团简介
     */
    private String intro;

    /**
     * 指导老师
     */
    @TableField("advisor_name")
    private String advisorName;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 状态：0待审 1上架 2驳回 3下架
     */
    private Integer status;

    /**
     * 创建人/负责人用户ID
     */
    @TableField("owner_user_id")
    private Long ownerUserId;

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

