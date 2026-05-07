package com.zoen.sys.web.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author zone
 * @description 创建社团申请请求
 * @since 2026-05-01
 */
@Data
public class ClubApplyCreateRequest {

    /**
     * 社团名称
     */
    @NotBlank(message = "社团名称不能为空")
    @Size(min = 2, max = 30, message = "社团名称需2-30字")
    private String name;

    /**
     * 社团类型
     */
    @NotNull(message = "社团类型不能为空")
    @Min(value = 1, message = "社团类型不合法")
    @Max(value = 4, message = "社团类型不合法")
    private Integer type;

    /**
     * 社团简介
     */
    @NotBlank(message = "社团简介不能为空")
    @Size(min = 10, max = 500, message = "社团简介需10-500字")
    private String intro;

    /**
     * 指导老师（可选）
     */
    @Size(max = 30, message = "指导老师最长30字")
    private String advisorName;

    /**
     * 联系方式
     */
    @NotBlank(message = "联系方式不能为空")
    @Size(max = 50, message = "联系方式最长50字")
    private String contact;
}

