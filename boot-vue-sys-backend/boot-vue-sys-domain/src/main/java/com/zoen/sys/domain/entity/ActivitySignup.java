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
 * @description 活动报名表实体
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("activity_signup")
public class ActivitySignup {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 活动ID
     */
    @TableField("activity_id")
    private Long activityId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 报名状态：1已报名 2已取消
     */
    private Integer status;

    /**
     * 报名时间
     */
    @TableField("signup_at")
    private LocalDateTime signupAt;

    /**
     * 取消时间
     */
    @TableField("cancel_at")
    private LocalDateTime cancelAt;

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

