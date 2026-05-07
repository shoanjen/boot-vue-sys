package com.zoen.sys.web.controller.v1;

import com.zoen.sys.common.model.ApiResponse;
import com.zoen.sys.domain.entity.Club;
import com.zoen.sys.domain.enums.UserRoleEnum;
import com.zoen.sys.service.platform.PlatformService;
import com.zoen.sys.web.auth.AuthContextHolder;
import com.zoen.sys.web.auth.RequireRole;
import com.zoen.sys.web.model.request.PlatformClubReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zone
 * @description 平台管理接口
 * @since 2026-05-01
 */
@RestController
@RequestMapping("/api/v1/platform")
@RequiredArgsConstructor
@RequireRole({UserRoleEnum.PLATFORM_ADMIN_CODE})
public class PlatformController {

    private final PlatformService platformService;

    /**
     * @description 待审核社团列表
     * @return 社团列表
     */
    @GetMapping("/club/pendingList")
    public ApiResponse<List<Club>> pendingClubs() {
        return ApiResponse.success(platformService.listPendingClubs());
    }

    /**
     * @param request 审核请求
     * @description 审核社团创建申请
     * @return 无
     */
    @PostMapping("/club/review")
    public ApiResponse<Void> review(@Validated @RequestBody PlatformClubReviewRequest request) {
        Long operatorUserId = AuthContextHolder.requiredUserId();
        platformService.reviewClub(operatorUserId, request.getClubId(), Boolean.TRUE.equals(request.getPass()), request.getRejectReason());
        return ApiResponse.success(null);
    }
}

