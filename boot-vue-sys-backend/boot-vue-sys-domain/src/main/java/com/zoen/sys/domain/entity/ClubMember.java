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
 * @description 社团成员表实体
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("club_member")
public class ClubMember {

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
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 成员角色：1成员 2管理员
     */
    private Integer role;

    /**
     * 成员状态：1正常 2已移除
     */
    private Integer status;

    /**
     * 加入时间
     */
    @TableField("joined_at")
    private LocalDateTime joinedAt;

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

