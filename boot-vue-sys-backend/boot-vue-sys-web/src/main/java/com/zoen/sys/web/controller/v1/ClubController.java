package com.zoen.sys.web.controller.v1;

import com.zoen.sys.common.model.ApiResponse;
import com.zoen.sys.domain.entity.Club;
import com.zoen.sys.domain.enums.UserRoleEnum;
import com.zoen.sys.service.club.ClubService;
import com.zoen.sys.web.auth.AuthContextHolder;
import com.zoen.sys.web.auth.RequireRole;
import com.zoen.sys.web.model.request.ClubApplyCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author zone
 * @description 社团相关接口
 * @since 2026-05-01
 */
@RestController
@RequestMapping("/api/v1/club")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    /**
     * @param keyword 关键词
     * @param type 社团类型
     * @description 社团列表（仅上架）
     * @return 社团列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Club>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "type", required = false) Integer type) {
        return ApiResponse.success(clubService.listApproved(keyword, type));
    }

    /**
     * @param clubId 社团ID
     * @description 社团详情
     * @return 社团信息
     */
    @GetMapping("/detail")
    public ApiResponse<Club> detail(@RequestParam(value = "clubId") Long clubId) {
        return ApiResponse.success(clubService.detail(clubId));
    }

    /**
     * @param request 创建社团申请请求
     * @description 创建社团申请（学生）
     * @return 创建的社团记录
     */
    @PostMapping("/applyCreate")
    @RequireRole({UserRoleEnum.STUDENT_CODE})
    public ApiResponse<Club> applyCreate(@Validated @RequestBody ClubApplyCreateRequest request) {
        Long userId = AuthContextHolder.requiredUserId();
        return ApiResponse.success(
                clubService.applyCreate(
                        userId,
                        request.getName().trim(),
                        request.getType(),
                        request.getIntro().trim(),
                        request.getAdvisorName() == null ? null : request.getAdvisorName().trim(),
                        request.getContact().trim()
                )
        );
    }

    /**
     * @description 我创建的社团列表
     * @return 社团列表
     */
    @GetMapping("/myCreated")
    @RequireRole({UserRoleEnum.STUDENT_CODE, UserRoleEnum.CLUB_ADMIN_CODE, UserRoleEnum.PLATFORM_ADMIN_CODE})
    public ApiResponse<List<Club>> myCreated() {
        Long userId = AuthContextHolder.requiredUserId();
        return ApiResponse.success(clubService.listMyCreated(userId));
    }
}
