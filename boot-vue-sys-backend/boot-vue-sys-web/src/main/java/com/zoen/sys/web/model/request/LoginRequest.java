package com.zoen.sys.web.model.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @author zone
 * @description 登录请求
 * @since 2026-05-01
 */
@Data
public class LoginRequest {

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
