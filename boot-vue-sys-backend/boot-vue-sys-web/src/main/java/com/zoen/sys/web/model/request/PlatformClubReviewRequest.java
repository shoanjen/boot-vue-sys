package com.zoen.sys.web.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zone
 * @description 平台社团审核请求
 * @since 2026-05-01
 */
@Data
public class PlatformClubReviewRequest {

    /**
     * 社团ID
     */
    @NotNull(message = "clubId不能为空")
    private Long clubId;

    /**
     * 是否通过
     */
    @NotNull(message = "pass不能为空")
    private Boolean pass;

    /**
     * 驳回原因（驳回必填）
     */
    private String rejectReason;
}

