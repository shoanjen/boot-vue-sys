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
 * @description 用户表实体
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user")
public class SysUser {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 密码摘要
     */
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 用户姓名/昵称
     */
    private String name;

    /**
     * 学号
     */
    @TableField("student_no")
    private String studentNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色：1学生 2社团管理员 3平台管理员
     */
    private Integer role;

    /**
     * 状态：1启用 2禁用
     */
    private Integer status;

    /**
     * 最近登录时间
     */
    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;

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
     * 逻辑删除：0否 1是
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}

