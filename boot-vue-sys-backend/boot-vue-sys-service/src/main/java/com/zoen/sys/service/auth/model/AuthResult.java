package com.zoen.sys.service.auth.model;

import com.zoen.sys.domain.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zone
 * @description 登录结果
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResult {

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 用户信息
     */
    private SysUser user;
}

