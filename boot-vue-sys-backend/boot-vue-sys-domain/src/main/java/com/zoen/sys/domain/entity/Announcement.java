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
 * @description 公告表实体
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("announcement")
public class Announcement {

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
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告状态：1已发布 2已下架
     */
    private Integer status;

    /**
     * 发布时间
     */
    @TableField("published_at")
    private LocalDateTime publishedAt;

    /**
     * 发布人用户ID
     */
    @TableField("published_by")
    private Long publishedBy;

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

