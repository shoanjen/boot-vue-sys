package com.zoen.sys.web.auth;

import com.zoen.sys.common.exception.BizException;
import com.zoen.sys.common.exception.ErrorCode;
import com.zoen.sys.service.auth.AuthTokenService;
import com.zoen.sys.service.auth.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author zone
 * @description 登录鉴权拦截器（基于 @RequireRole）
 * @since 2026-05-01
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthTokenService authTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod hm = (HandlerMethod) handler;
        RequireRole onMethod = hm.getMethodAnnotation(RequireRole.class);
        RequireRole onClass = hm.getBeanType().getAnnotation(RequireRole.class);
        RequireRole requireRole = onMethod != null ? onMethod : onClass;
        if (requireRole == null) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring("Bearer ".length()) : authHeader;

        UserPrincipal principal = authTokenService.verify(token);
        if (principal == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录");
        }

        boolean allowed = Arrays.stream(requireRole.value()).anyMatch(x -> x == principal.getRole());
        if (!allowed) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限访问");
        }

        AuthContextHolder.set(principal);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContextHolder.clear();
    }
}
