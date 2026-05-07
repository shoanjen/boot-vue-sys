package com.zoen.sys.web.auth;

import com.zoen.sys.service.auth.model.UserPrincipal;
import com.zoen.sys.common.exception.BizException;
import com.zoen.sys.common.exception.ErrorCode;

/**
 * @author zone
 * @description 登录上下文持有者（ThreadLocal）
 * @since 2026-05-01
 */
public class AuthContextHolder {

    private static final ThreadLocal<UserPrincipal> HOLDER = new ThreadLocal<>();

    /**
     * @param principal 登录用户信息
     * @description 设置当前请求用户上下文
     * @return 无
     */
    public static void set(UserPrincipal principal) {
        HOLDER.set(principal);
    }

    /**
     * @description 获取当前请求用户上下文
     * @return 登录用户信息
     */
    public static UserPrincipal get() {
        return HOLDER.get();
    }

    /**
     * @description 获取当前用户ID（不存在则抛异常）
     * @return 用户ID
     */
    public static Long requiredUserId() {
        UserPrincipal p = HOLDER.get();
        if (p == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        return p.getUserId();
    }

    /**
     * @description 清理上下文
     * @return 无
     */
    public static void clear() {
        HOLDER.remove();
    }
}
