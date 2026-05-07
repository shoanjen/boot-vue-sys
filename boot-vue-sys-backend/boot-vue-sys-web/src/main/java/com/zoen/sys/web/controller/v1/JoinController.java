package com.zoen.sys.web.controller.v1;

import com.zoen.sys.common.model.ApiResponse;
import com.zoen.sys.domain.entity.JoinRequest;
import com.zoen.sys.domain.enums.UserRoleEnum;
import com.zoen.sys.service.join.JoinService;
import com.zoen.sys.web.auth.AuthContextHolder;
import com.zoen.sys.web.auth.RequireRole;
import com.zoen.sys.web.model.request.JoinApplyRequest;
import com.zoen.sys.web.model.request.JoinReviewRequest;
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
 * @description 入社流程接口
 * @since 2026-05-01
 */
@RestController
@RequestMapping("/api/v1/join")
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    /**
     * @param request 入社申请请求
     * @description 提交入社申请（学生）
     * @return 入社申请记录
     */
    @PostMapping("/apply")
    @RequireRole({UserRoleEnum.STUDENT_CODE})
    public ApiResponse<JoinRequest> apply(@Validated @RequestBody JoinApplyRequest request) {
        Long userId = AuthContextHolder.requiredUserId();
        return ApiResponse.success(joinService.apply(userId, request.getClubId(), request.getReason().trim()));
    }

    /**
     * @description 我的入社申请列表
     * @return 入社申请列表
     */
    @GetMapping("/myList")
    @RequireRole({UserRoleEnum.STUDENT_CODE, UserRoleEnum.CLUB_ADMIN_CODE, UserRoleEnum.PLATFORM_ADMIN_CODE})
    public ApiResponse<List<JoinRequest>> myList() {
        Long userId = AuthContextHolder.requiredUserId();
        return ApiResponse.success(joinService.myList(userId));
    }

    /**
     * @param clubId 社团ID
     * @description 社团待审核入社申请列表（社团管理员）
     * @return 入社申请列表
     */
    @GetMapping("/pendingList")
    @RequireRole({UserRoleEnum.CLUB_ADMIN_CODE})
    public ApiResponse<List<JoinRequest>> pendingList(@RequestParam(value = "clubId") Long clubId) {
        Long operatorUserId = AuthContextHolder.requiredUserId();
        return ApiResponse.success(joinService.pendingList(operatorUserId, clubId));
    }

    /**
     * @param request 审核请求
     * @description 审核入社申请（社团管理员）
     * @return 无
     */
    @PostMapping("/review")
    @RequireRole({UserRoleEnum.CLUB_ADMIN_CODE})
    public ApiResponse<Void> review(@Validated @RequestBody JoinReviewRequest request) {
        Long operatorUserId = AuthContextHolder.requiredUserId();
        joinService.review(operatorUserId, request.getRequestId(), Boolean.TRUE.equals(request.getPass()), request.getRejectReason());
        return ApiResponse.success(null);
    }
}
