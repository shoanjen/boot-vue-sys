package com.zoen.sys.web.controller.v1;

import com.zoen.sys.common.model.ApiResponse;
import com.zoen.sys.domain.entity.ClubMember;
import com.zoen.sys.domain.enums.UserRoleEnum;
import com.zoen.sys.service.member.MemberService;
import com.zoen.sys.web.auth.AuthContextHolder;
import com.zoen.sys.web.auth.RequireRole;
import com.zoen.sys.web.model.request.MemberRemoveRequest;
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
 * @description 成员管理接口（社团管理员）
 * @since 2026-05-01
 */
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@RequireRole({UserRoleEnum.CLUB_ADMIN_CODE})
public class MemberController {

    private final MemberService memberService;

    /**
     * @param clubId 社团ID
     * @description 成员列表
     * @return 成员列表
     */
    @GetMapping("/list")
    public ApiResponse<List<ClubMember>> list(@RequestParam(value = "clubId") Long clubId) {
        Long operatorUserId = AuthContextHolder.requiredUserId();
        return ApiResponse.success(memberService.listMembers(operatorUserId, clubId));
    }

    /**
     * @param request 移除成员请求
     * @description 移除成员
     * @return 无
     */
    @PostMapping("/remove")
    public ApiResponse<Void> remove(@Validated @RequestBody MemberRemoveRequest request) {
        Long operatorUserId = AuthContextHolder.requiredUserId();
        memberService.removeMember(operatorUserId, request.getMemberId());
        return ApiResponse.success(null);
    }
}
