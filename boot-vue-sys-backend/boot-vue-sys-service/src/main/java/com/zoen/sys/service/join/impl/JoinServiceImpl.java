package com.zoen.sys.service.join.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zoen.sys.common.exception.BizException;
import com.zoen.sys.common.exception.ErrorCode;
import com.zoen.sys.domain.entity.Club;
import com.zoen.sys.domain.entity.ClubMember;
import com.zoen.sys.domain.entity.JoinRequest;
import com.zoen.sys.domain.enums.ClubStatusEnum;
import com.zoen.sys.domain.enums.JoinRequestStatusEnum;
import com.zoen.sys.domain.enums.MemberRoleEnum;
import com.zoen.sys.domain.enums.MemberStatusEnum;
import com.zoen.sys.infrastructure.mapper.ClubMapper;
import com.zoen.sys.infrastructure.mapper.ClubMemberMapper;
import com.zoen.sys.infrastructure.mapper.JoinRequestMapper;
import com.zoen.sys.service.audit.AuditLogService;
import com.zoen.sys.service.join.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zone
 * @description 入社流程服务实现
 * @since 2026-05-01
 */
@Service
@RequiredArgsConstructor
public class JoinServiceImpl implements JoinService {

    private final JoinRequestMapper joinRequestMapper;
    private final ClubMapper clubMapper;
    private final ClubMemberMapper clubMemberMapper;
    private final AuditLogService auditLogService;

    /**
     * @param userId 用户ID
     * @param clubId 社团ID
     * @param reason 申请理由
     * @description 提交入社申请
     * @return 入社申请记录
     */
    @Override
    public JoinRequest apply(Long userId, Long clubId, String reason) {
        Club club = clubMapper.selectById(clubId);
        if (club == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "社团不存在");
        }
        if (club.getStatus() == null || club.getStatus() != ClubStatusEnum.APPROVED.getCode()) {
            throw new BizException(ErrorCode.BIZ_ERROR, "当前不可申请加入");
        }

        ClubMember member = clubMemberMapper.selectOne(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId)
                .eq(ClubMember::getStatus, MemberStatusEnum.ACTIVE.getCode())
                .last("limit 1"));
        if (member != null) {
            throw new BizException(ErrorCode.BIZ_ERROR, "你已加入该社团");
        }

        JoinRequest pending = joinRequestMapper.selectOne(new LambdaQueryWrapper<JoinRequest>()
                .eq(JoinRequest::getClubId, clubId)
                .eq(JoinRequest::getUserId, userId)
                .eq(JoinRequest::getStatus, JoinRequestStatusEnum.PENDING.getCode())
                .last("limit 1"));
        if (pending != null) {
            throw new BizException(ErrorCode.BIZ_ERROR, "已提交申请，请等待审核");
        }

        JoinRequest req = JoinRequest.builder()
                .clubId(clubId)
                .userId(userId)
                .reason(reason)
                .status(JoinRequestStatusEnum.PENDING.getCode())
                .isDeleted(0)
                .build();
        joinRequestMapper.insert(req);
        auditLogService.write(userId, "JOIN_APPLY_SUBMIT", "join_request", req.getId(), null, null);
        return req;
    }

    /**
     * @param userId 用户ID
     * @description 我的入社申请列表
     * @return 入社申请列表
     */
    @Override
    public List<JoinRequest> myList(Long userId) {
        return joinRequestMapper.selectList(new LambdaQueryWrapper<JoinRequest>()
                .eq(JoinRequest::getUserId, userId)
                .orderByDesc(JoinRequest::getId));
    }

    /**
     * @param operatorUserId 操作人用户ID（社团管理员）
     * @param clubId 社团ID
     * @description 待审核入社申请列表
     * @return 入社申请列表
     */
    @Override
    public List<JoinRequest> pendingList(Long operatorUserId, Long clubId) {
        assertClubAdmin(operatorUserId, clubId);
        return joinRequestMapper.selectList(new LambdaQueryWrapper<JoinRequest>()
                .eq(JoinRequest::getClubId, clubId)
                .eq(JoinRequest::getStatus, JoinRequestStatusEnum.PENDING.getCode())
                .orderByAsc(JoinRequest::getId));
    }

    /**
     * @param operatorUserId 操作人用户ID（社团管理员）
     * @param requestId 入社申请ID
     * @param pass 是否通过
     * @param rejectReason 驳回原因（可选）
     * @description 审核入社申请
     * @return 无
     */
    @Override
    @Transactional
    public void review(Long operatorUserId, Long requestId, boolean pass, String rejectReason) {
        JoinRequest req = joinRequestMapper.selectById(requestId);
        if (req == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "入社申请不存在");
        }
        if (req.getStatus() == null || req.getStatus() != JoinRequestStatusEnum.PENDING.getCode()) {
            throw new BizException(ErrorCode.BIZ_ERROR, "该申请已处理");
        }
        assertClubAdmin(operatorUserId, req.getClubId());

        req.setReviewedBy(operatorUserId);
        req.setReviewedAt(LocalDateTime.now());
        if (pass) {
            req.setStatus(JoinRequestStatusEnum.APPROVED.getCode());
            req.setRejectReason(null);
        } else {
            req.setStatus(JoinRequestStatusEnum.REJECTED.getCode());
            req.setRejectReason(rejectReason == null ? null : rejectReason.trim());
        }
        joinRequestMapper.updateById(req);

        if (pass) {
            ClubMember exist = clubMemberMapper.selectOne(new LambdaQueryWrapper<ClubMember>()
                    .eq(ClubMember::getClubId, req.getClubId())
                    .eq(ClubMember::getUserId, req.getUserId())
                    .last("limit 1"));
            if (exist == null) {
                ClubMember member = ClubMember.builder()
                        .clubId(req.getClubId())
                        .userId(req.getUserId())
                        .role(MemberRoleEnum.MEMBER.getCode())
                        .status(MemberStatusEnum.ACTIVE.getCode())
                        .joinedAt(LocalDateTime.now())
                        .isDeleted(0)
                        .build();
                clubMemberMapper.insert(member);
            }
        }

        auditLogService.write(
                operatorUserId,
                pass ? "JOIN_REVIEW_PASS" : "JOIN_REVIEW_REJECT",
                "join_request",
                requestId,
                pass ? null : rejectReason,
                null
        );
    }

    private void assertClubAdmin(Long operatorUserId, Long clubId) {
        ClubMember admin = clubMemberMapper.selectOne(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, operatorUserId)
                .eq(ClubMember::getRole, MemberRoleEnum.ADMIN.getCode())
                .eq(ClubMember::getStatus, MemberStatusEnum.ACTIVE.getCode())
                .last("limit 1"));
        if (admin == null) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限操作该社团");
        }
    }
}

