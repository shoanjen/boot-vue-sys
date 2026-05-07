package com.zoen.sys.web.controller.v1;

import com.zoen.sys.common.model.ApiResponse;
import com.zoen.sys.domain.entity.Announcement;
import com.zoen.sys.domain.enums.UserRoleEnum;
import com.zoen.sys.service.announcement.AnnouncementService;
import com.zoen.sys.web.auth.AuthContextHolder;
import com.zoen.sys.web.auth.RequireRole;
import com.zoen.sys.web.model.request.AnnouncementPublishRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zone
 * @description 公告接口
 * @since 2026-05-01
 */
@RestController
@RequestMapping("/api/v1/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * @param request 发布公告请求
     * @description 发布公告（社团管理员）
     * @return 公告记录
     */
    @PostMapping("/publish")
    @RequireRole({UserRoleEnum.CLUB_ADMIN_CODE})
    public ApiResponse<Announcement> publish(@Validated @RequestBody AnnouncementPublishRequest request) {
        Long operatorUserId = AuthContextHolder.requiredUserId();
        return ApiResponse.success(
                announcementService.publish(
                        operatorUserId,
                        request.getClubId(),
                        request.getTitle().trim(),
                        request.getContent().trim()
                )
        );
    }

    /**
     * @param clubId 社团ID
     * @description 公告列表
     * @return 公告列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Announcement>> list(@RequestParam(value = "clubId") Long clubId) {
        return ApiResponse.success(announcementService.list(clubId));
    }

    /**
     * @param announcementId 公告ID
     * @description 公告详情
     * @return 公告记录
     */
    @GetMapping("/detail")
    public ApiResponse<Announcement> detail(@RequestParam(value = "announcementId") Long announcementId) {
        return ApiResponse.success(announcementService.detail(announcementId));
    }
}
