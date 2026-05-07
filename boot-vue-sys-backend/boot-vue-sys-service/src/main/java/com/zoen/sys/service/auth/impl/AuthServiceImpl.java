package com.zoen.sys.service.auth.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zoen.sys.common.exception.BizException;
import com.zoen.sys.common.exception.ErrorCode;
import com.zoen.sys.domain.entity.SysUser;
import com.zoen.sys.domain.enums.UserRoleEnum;
import com.zoen.sys.infrastructure.mapper.SysUserMapper;
import com.zoen.sys.service.auth.AuthService;
import com.zoen.sys.service.auth.AuthTokenService;
import com.zoen.sys.service.auth.model.AuthResult;
import com.zoen.sys.service.auth.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author zone
 * @description 账号与权限服务实现
 * @since 2026-05-01
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final AuthTokenService authTokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * @param account 账号
     * @param password 密码
     * @param name 用户名
     * @param studentNo 学号（可选）
     * @param phone 手机号（可选）
     * @param email 邮箱（可选）
     * @description 学生注册
     * @return 登录结果（注册后直接登录）
     */
    @Override
    public AuthResult registerStudent(String account, String password, String name, String studentNo, String phone, String email) {
        try {
            SysUser user = SysUser.builder()
                    .account(account)
                    .passwordHash(passwordEncoder.encode(password))
                    .name(name)
                    .studentNo(studentNo)
                    .phone(phone)
                    .email(email)
                    .role(UserRoleEnum.STUDENT.getCode())
                    .status(1)
                    .lastLoginAt(LocalDateTime.now())
                    .isDeleted(0)
                    .build();
            sysUserMapper.insert(user);
            return buildAuthResult(user);
        } catch (DuplicateKeyException e) {
            throw new BizException(ErrorCode.BIZ_ERROR, "账号或学号已存在");
        }
    }

    /**
     * @param account 账号
     * @param password 密码
     * @description 登录
     * @return 登录结果
     */
    @Override
    public AuthResult login(String account, String password) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getAccount, account)
                .last("limit 1"));
        if (user == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 2) {
            throw new BizException(ErrorCode.FORBIDDEN, "账号已被禁用，请联系管理员");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "账号或密码错误");
        }
        user.setLastLoginAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
        return buildAuthResult(user);
    }

    private AuthResult buildAuthResult(SysUser user) {
        UserPrincipal principal = UserPrincipal.builder()
                .userId(user.getId())
                .role(user.getRole())
                .account(user.getAccount())
                .name(user.getName())
                .build();
        String token = authTokenService.generate(principal);
        user.setPasswordHash(null);
        return AuthResult.builder().token(token).user(user).build();
    }
}

