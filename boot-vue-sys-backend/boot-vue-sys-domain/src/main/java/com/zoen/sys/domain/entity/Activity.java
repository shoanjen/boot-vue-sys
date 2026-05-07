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
 * @description 活动表实体
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("activity")
public class Activity {

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
     * 活动标题
     */
    private String title;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 地点
     */
    @TableField("place")
    private String place;

    /**
     * 报名截止时间
     */
    @TableField("signup_deadline")
    private LocalDateTime signupDeadline;

    /**
     * 报名人数上限：0不限
     */
    @TableField("signup_limit")
    private Integer signupLimit;

    /**
     * 已报名人数
     */
    @TableField("signup_count")
    private Integer signupCount;

    /**
     * 活动状态：0草稿 1已发布 2已下架
     */
    private Integer status;

    /**
     * 创建人用户ID
     */
    @TableField("created_by")
    private Long createdBy;

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

