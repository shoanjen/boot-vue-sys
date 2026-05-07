package com.zoen.sys.web.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author zone
 * @description 发布公告请求
 * @since 2026-05-01
 */
@Data
public class AnnouncementPublishRequest {

    /**
     * 社团ID
     */
    @NotNull(message = "clubId不能为空")
    private Long clubId;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 50, message = "标题需2-50字")
    private String title;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    @Size(min = 10, max = 3000, message = "内容需10-3000字")
    private String content;
}

