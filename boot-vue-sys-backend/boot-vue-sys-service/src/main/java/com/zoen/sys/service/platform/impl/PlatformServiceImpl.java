package com.zoen.sys.service.platform.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zoen.sys.common.exception.BizException;
import com.zoen.sys.common.exception.ErrorCode;
import com.zoen.sys.domain.entity.Club;
import com.zoen.sys.domain.entity.ClubMember;
import com.zoen.sys.domain.entity.SysUser;
import com.zoen.sys.domain.enums.ClubStatusEnum;
import com.zoen.sys.domain.enums.MemberRoleEnum;
import com.zoen.sys.domain.enums.MemberStatusEnum;
import com.zoen.sys.domain.enums.UserRoleEnum;
import com.zoen.sys.infrastructure.mapper.ClubMapper;
import com.zoen.sys.infrastructure.mapper.ClubMemberMapper;
import com.zoen.sys.infrastructure.mapper.SysUserMapper;
import com.zoen.sys.service.audit.AuditLogService;
import com.zoen.sys.service.platform.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zone
 * @description 平台管理服务实现
 * @since 2026-05-01
 */
@Service
@RequiredArgsConstructor
public class PlatformServiceImpl implements PlatformService {

    private final ClubMapper clubMapper;
    private final SysUserMapper sysUserMapper;
    private final ClubMemberMapper clubMemberMapper;
    private final AuditLogService auditLogService;

    /**
     * @description 待审核社团列表
     * @return 社团列表
     */
    @Override
    public List<Club> listPendingClubs() {
        return clubMapper.selectList(new LambdaQueryWrapper<Club>()
                .eq(Club::getStatus, ClubStatusEnum.PENDING.getCode())
                .orderByAsc(Club::getId));
    }

    /**
     * @param operatorUserId 操作人用户ID
     * @param clubId 社团ID
     * @param pass 是否通过
     * @param rejectReason 驳回原因（驳回时必填）
     * @description 审核社团创建申请
     * @return 无
     */
    @Override
    @Transactional
    public void reviewClub(Long operatorUserId, Long clubId, boolean pass, String rejectReason) {
        Club club = clubMapper.selectById(clubId);
        if (club == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "社团不存在");
        }
        if (club.getStatus() == null || club.getStatus() != ClubStatusEnum.PENDING.getCode()) {
            throw new BizException(ErrorCode.BIZ_ERROR, "当前社团不处于待审核状态");
        }
        if (!pass && (rejectReason == null || rejectReason.isBlank())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "驳回原因不能为空");
        }

        club.setReviewedBy(operatorUserId);
        club.setReviewedAt(LocalDateTime.now());
        if (pass) {
            club.setStatus(ClubStatusEnum.APPROVED.getCode());
            club.setRejectReason(null);
        } else {
            club.setStatus(ClubStatusEnum.REJECTED.getCode());
            club.setRejectReason(rejectReason.trim());
        }
        clubMapper.updateById(club);

        if (pass) {
            SysUser owner = sysUserMapper.selectById(club.getOwnerUserId());
            if (owner != null && owner.getRole() != null && owner.getRole() == UserRoleEnum.STUDENT.getCode()) {
                owner.setRole(UserRoleEnum.CLUB_ADMIN.getCode());
                sysUserMapper.updateById(owner);
            }
            ClubMember exist = clubMemberMapper.selectOne(new LambdaQueryWrapper<ClubMember>()
                    .eq(ClubMember::getClubId, club.getId())
                    .eq(ClubMember::getUserId, club.getOwnerUserId())
                    .last("limit 1"));
            if (exist == null) {
                ClubMember member = ClubMember.builder()
                        .clubId(club.getId())
                        .userId(club.getOwnerUserId())
                        .role(MemberRoleEnum.ADMIN.getCode())
                        .status(MemberStatusEnum.ACTIVE.getCode())
                        .joinedAt(LocalDateTime.now())
                        .isDeleted(0)
                        .build();
                clubMemberMapper.insert(member);
            }
        }

        auditLogService.write(
                operatorUserId,
                pass ? "CLUB_REVIEW_PASS" : "CLUB_REVIEW_REJECT",
                "club",
                clubId,
                pass ? null : rejectReason,
                null
        );
    }
}

