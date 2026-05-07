package com.zoen.sys.service.auth.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoen.sys.common.exception.BizException;
import com.zoen.sys.common.exception.ErrorCode;
import com.zoen.sys.service.auth.AuthTokenService;
import com.zoen.sys.service.auth.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zone
 * @description HMAC Token 实现（JWT风格，但不依赖第三方库）
 * @since 2026-05-01
 */
@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final ObjectMapper objectMapper;

    @Value("${app.auth.token-secret}")
    private String tokenSecret;

    @Value("${app.auth.token-expire-seconds:604800}")
    private long tokenExpireSeconds;

    /**
     * @param principal 登录用户信息
     * @description 生成访问令牌
     * @return token 字符串
     */
    @Override
    public String generate(UserPrincipal principal) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            long now = Instant.now().getEpochSecond();
            Map<String, Object> payload = new HashMap<>();
            payload.put("uid", principal.getUserId());
            payload.put("role", principal.getRole());
            payload.put("acc", principal.getAccount());
            payload.put("name", principal.getName());
            payload.put("iat", now);
            payload.put("exp", now + tokenExpireSeconds);

            String headerJson = objectMapper.writeValueAsString(header);
            String payloadJson = objectMapper.writeValueAsString(payload);

            String h = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
            String p = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
            String unsigned = h + "." + p;
            String sig = sign(unsigned, tokenSecret);
            return unsigned + "." + sig;
        } catch (Exception e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "生成Token失败");
        }
    }

    /**
     * @param token token 字符串
     * @description 校验并解析 Token
     * @return 登录用户信息
     */
    @Override
    public UserPrincipal verify(String token) {
        try {
            if (token == null || token.isBlank()) {
                throw new BizException(ErrorCode.UNAUTHORIZED, "未登录");
            }
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new BizException(ErrorCode.UNAUTHORIZED, "Token非法");
            }
            String unsigned = parts[0] + "." + parts[1];
            String expected = sign(unsigned, tokenSecret);
            if (!constantTimeEquals(expected, parts[2])) {
                throw new BizException(ErrorCode.UNAUTHORIZED, "Token签名错误");
            }

            byte[] payloadBytes = base64UrlDecode(parts[1]);
            Map<String, Object> payload = objectMapper.readValue(payloadBytes, new TypeReference<Map<String, Object>>() {});
            long exp = ((Number) payload.get("exp")).longValue();
            long now = Instant.now().getEpochSecond();
            if (now >= exp) {
                throw new BizException(ErrorCode.UNAUTHORIZED, "登录已过期");
            }

            return UserPrincipal.builder()
                    .userId(((Number) payload.get("uid")).longValue())
                    .role(((Number) payload.get("role")).intValue())
                    .account((String) payload.get("acc"))
                    .name((String) payload.get("name"))
                    .build();
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "Token解析失败");
        }
    }

    private String sign(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] out = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(out);
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private byte[] base64UrlDecode(String s) {
        return Base64.getUrlDecoder().decode(s);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r |= a.charAt(i) ^ b.charAt(i);
        }
        return r == 0;
    }
}
