package com.zoen.sys.web.controller.v1;

import com.zoen.sys.common.model.ApiResponse;
import com.zoen.sys.domain.enums.UserRoleEnum;
import com.zoen.sys.service.me.MeService;
import com.zoen.sys.service.me.model.MyCenterData;
import com.zoen.sys.web.auth.AuthContextHolder;
import com.zoen.sys.web.auth.RequireRole;
import com.zoen.sys.web.model.response.MyCenterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zone
 * @description 我的中心接口
 * @since 2026-05-01
 */
@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {

    private final MeService meService;

    /**
     * @description 获取我的中心数据
     * @return 我的中心数据
     */
    @GetMapping("/center")
    @RequireRole({UserRoleEnum.STUDENT_CODE, UserRoleEnum.CLUB_ADMIN_CODE, UserRoleEnum.PLATFORM_ADMIN_CODE})
    public ApiResponse<MyCenterResponse> center() {
        Long userId = AuthContextHolder.requiredUserId();
        MyCenterData data = meService.getMyCenter(userId);
        MyCenterResponse resp = MyCenterResponse.builder()
                .myCreatedClubs(data.getMyCreatedClubs())
                .myJoinRequests(data.getMyJoinRequests())
                .myClubMembers(data.getMyClubMembers())
                .myClubs(data.getMyClubs())
                .mySignups(data.getMySignups())
                .myActivities(data.getMyActivities())
                .build();
        return ApiResponse.success(resp);
    }
}

