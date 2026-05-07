package com.zoen.sys.web.controller.v1;

import com.zoen.sys.common.model.ApiResponse;
import com.zoen.sys.domain.entity.Activity;
import com.zoen.sys.domain.enums.UserRoleEnum;
import com.zoen.sys.service.activity.ActivityService;
import com.zoen.sys.web.auth.AuthContextHolder;
import com.zoen.sys.web.auth.RequireRole;
import com.zoen.sys.web.model.request.ActivityCreateRequest;
import com.zoen.sys.web.model.request.ActivitySignupRequest;
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
 * @description 活动管理接口
 * @since 2026-05-01
 */
@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /**
     * @param request 创建活动请求
     * @description 创建并发布活动（社团管理员）
     * @return 活动记录
     */
    @PostMapping("/create")
    @RequireRole({UserRoleEnum.CLUB_ADMIN_CODE})
    public ApiResponse<Activity> create(@Validated @RequestBody ActivityCreateRequest request) {
        Long operatorUserId = AuthContextHolder.requiredUserId();
        return ApiResponse.success(
                activityService.createAndPublish(
                        operatorUserId,
                        request.getClubId(),
                        request.getTitle().trim(),
                        request.getStartTime(),
                        request.getEndTime(),
                        request.getPlace().trim(),
                        request.getSignupDeadline(),
                        request.getSignupLimit()
                )
        );
    }

    /**
     * @param clubId 社团ID
     * @description 活动列表（已发布）
     * @return 活动列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Activity>> list(@RequestParam(value = "clubId") Long clubId) {
        return ApiResponse.success(activityService.listPublished(clubId));
    }

    /**
     * @param request 报名请求
     * @description 报名活动（学生）
     * @return 无
     */
    @PostMapping("/signup")
    @RequireRole({UserRoleEnum.STUDENT_CODE})
    public ApiResponse<Void> signup(@Validated @RequestBody ActivitySignupRequest request) {
        Long userId = AuthContextHolder.requiredUserId();
        activityService.signup(userId, request.getActivityId());
        return ApiResponse.success(null);
    }

    /**
     * @param request 取消报名请求
     * @description 取消报名（学生）
     * @return 无
     */
    @PostMapping("/cancelSignup")
    @RequireRole({UserRoleEnum.STUDENT_CODE})
    public ApiResponse<Void> cancelSignup(@Validated @RequestBody ActivitySignupRequest request) {
        Long userId = AuthContextHolder.requiredUserId();
        activityService.cancelSignup(userId, request.getActivityId());
        return ApiResponse.success(null);
    }
}
