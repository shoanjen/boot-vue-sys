package com.zoen.sys.web.controller.v1;

import com.zoen.sys.common.model.ApiResponse;
import com.zoen.sys.domain.entity.SysUser;
import com.zoen.sys.service.auth.AuthService;
import com.zoen.sys.service.auth.model.AuthResult;
import com.zoen.sys.web.model.request.LoginRequest;
import com.zoen.sys.web.model.request.RegisterRequest;
import com.zoen.sys.web.model.response.LoginResponse;
import com.zoen.sys.web.model.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zone
 * @description 登录注册接口
 * @since 2026-05-01
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * @param request 登录请求
     * @description 登录接口
     * @return 登录响应（token + user）
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        AuthResult result = authService.login(request.getAccount(), request.getPassword());
        return ApiResponse.success(toLoginResponse(result));
    }

    /**
     * @param request 注册请求
     * @description 注册接口（学生）
     * @return 登录响应（token + user）
     */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Validated @RequestBody RegisterRequest request) {
        AuthResult result = authService.registerStudent(
                request.getAccount(),
                request.getPassword(),
                request.getName(),
                request.getStudentNo(),
                request.getPhone(),
                request.getEmail()
        );
        return ApiResponse.success(toLoginResponse(result));
    }

    private LoginResponse toLoginResponse(AuthResult result) {
        SysUser u = result.getUser();
        UserResponse ur = UserResponse.builder()
                .id(u.getId())
                .account(u.getAccount())
                .name(u.getName())
                .studentNo(u.getStudentNo())
                .phone(u.getPhone())
                .email(u.getEmail())
                .role(u.getRole())
                .build();
        return LoginResponse.builder().token(result.getToken()).user(ur).build();
    }
}

