package com.zoen.sys.web.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zone
 * @description 入社审核请求
 * @since 2026-05-01
 */
@Data
public class JoinReviewRequest {

    /**
     * 入社申请ID
     */
    @NotNull(message = "requestId不能为空")
    private Long requestId;

    /**
     * 是否通过
     */
    @NotNull(message = "pass不能为空")
    private Boolean pass;

    /**
     * 驳回原因
     */
    private String rejectReason;
}

