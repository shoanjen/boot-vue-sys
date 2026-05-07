package com.zoen.sys.web.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zone
 * @description 移除成员请求
 * @since 2026-05-01
 */
@Data
public class MemberRemoveRequest {

    /**
     * 成员记录ID
     */
    @NotNull(message = "memberId不能为空")
    private Long memberId;
}

