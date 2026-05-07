package com.zoen.sys.service.auth;

import com.zoen.sys.service.auth.model.UserPrincipal;

/**
 * @author zone
 * @description Token 生成与解析
 * @since 2026-05-01
 */
public interface AuthTokenService {

    /**
     * @param principal 登录用户信息
     * @description 生成访问令牌
     * @return token 字符串
     */
    String generate(UserPrincipal principal);

    /**
     * @param token token 字符串
     * @description 校验并解析 Token
     * @return 登录用户信息
     */
    UserPrincipal verify(String token);
}

