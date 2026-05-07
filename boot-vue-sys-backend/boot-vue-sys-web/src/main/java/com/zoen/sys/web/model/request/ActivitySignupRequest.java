package com.zoen.sys.web.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zone
 * @description 活动报名/取消报名请求
 * @since 2026-05-01
 */
@Data
public class ActivitySignupRequest {

    /**
     * 活动ID
     */
    @NotNull(message = "activityId不能为空")
    private Long activityId;
}

