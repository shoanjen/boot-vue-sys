package com.zoen.sys.web.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zone
 * @description 创建活动请求
 * @since 2026-05-01
 */
@Data
public class ActivityCreateRequest {

    /**
     * 社团ID
     */
    @NotNull(message = "clubId不能为空")
    private Long clubId;

    /**
     * 活动标题
     */
    @NotBlank(message = "活动标题不能为空")
    @Size(min = 2, max = 50, message = "活动标题需2-50字")
    private String title;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    /**
     * 地点
     */
    @NotBlank(message = "地点不能为空")
    @Size(min = 2, max = 100, message = "地点需2-100字")
    private String place;

    /**
     * 报名截止
     */
    @NotNull(message = "报名截止不能为空")
    private LocalDateTime signupDeadline;

    /**
     * 人数上限：0不限
     */
    @NotNull(message = "人数上限不能为空")
    @Min(value = 0, message = "人数上限不合法")
    private Integer signupLimit;
}

