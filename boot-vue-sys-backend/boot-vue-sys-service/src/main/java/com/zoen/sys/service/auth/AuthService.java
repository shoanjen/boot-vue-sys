package com.zoen.sys.service.auth;

import com.zoen.sys.service.auth.model.AuthResult;

/**
 * @author zone
 * @description 账号与权限服务
 * @since 2026-05-01
 */
public interface AuthService {

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
    AuthResult registerStudent(String account, String password, String name, String studentNo, String phone, String email);

    /**
     * @param account 账号
     * @param password 密码
     * @description 登录
     * @return 登录结果
     */
    AuthResult login(String account, String password);
}

