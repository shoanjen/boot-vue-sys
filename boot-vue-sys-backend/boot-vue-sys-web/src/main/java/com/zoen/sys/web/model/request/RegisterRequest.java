package com.zoen.sys.web.model.request;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author zone
 * @description 注册请求（学生）
 * @since 2026-05-01
 */
@Data
public class RegisterRequest {

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度需在8-20位")
    private String password;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 32, message = "用户名长度需在2-32字")
    private String name;

    /**
     * 学号（可选）
     */
    private String studentNo;

    /**
     * 手机号（可选）
     */
    private String phone;

    /**
     * 邮箱（可选）
     */
    @Email(message = "邮箱格式不正确")
    private String email;
}

