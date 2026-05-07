package com.zoen.sys.web.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author zone
 * @description 入社申请请求
 * @since 2026-05-01
 */
@Data
public class JoinApplyRequest {

    /**
     * 社团ID
     */
    @NotNull(message = "clubId不能为空")
    private Long clubId;

    /**
     * 申请理由
     */
    @NotBlank(message = "申请理由不能为空")
    @Size(min = 10, max = 200, message = "申请理由需10-200字")
    private String reason;
}

