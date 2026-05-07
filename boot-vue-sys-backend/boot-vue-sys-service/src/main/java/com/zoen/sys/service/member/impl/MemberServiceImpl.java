package com.zoen.sys.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zoen.sys.common.exception.BizException;
import com.zoen.sys.common.exception.ErrorCode;
import com.zoen.sys.domain.entity.ClubMember;
import com.zoen.sys.domain.enums.MemberRoleEnum;
import com.zoen.sys.domain.enums.MemberStatusEnum;
import com.zoen.sys.infrastructure.mapper.ClubMemberMapper;
import com.zoen.sys.service.audit.AuditLogService;
import com.zoen.sys.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zone
 * @description 成员管理服务实现
 * @since 2026-05-01
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final ClubMemberMapper clubMemberMapper;
    private final AuditLogService auditLogService;

    /**
     * @param operatorUserId 操作人用户ID（社团管理员）
     * @param clubId 社团ID
     * @description 成员列表
     * @return 成员列表
     */
    @Override
    public List<ClubMember> listMembers(Long operatorUserId, Long clubId) {
        assertClubAdmin(operatorUserId, clubId);
        return clubMemberMapper.selectList(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getStatus, MemberStatusEnum.ACTIVE.getCode())
                .orderByDesc(ClubMember::getId));
    }

    /**
     * @param operatorUserId 操作人用户ID（社团管理员）
     * @param memberId 成员记录ID
     * @description 移除成员
     * @return 无
     */
    @Override
    public void removeMember(Long operatorUserId, Long memberId) {
        ClubMember member = clubMemberMapper.selectById(memberId);
        if (member == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "成员不存在");
        }
        assertClubAdmin(operatorUserId, member.getClubId());
        if (member.getRole() != null && member.getRole() == MemberRoleEnum.ADMIN.getCode()) {
            throw new BizException(ErrorCode.BIZ_ERROR, "不能移除管理员");
        }
        member.setStatus(MemberStatusEnum.REMOVED.getCode());
        clubMemberMapper.updateById(member);
        auditLogService.write(operatorUserId, "MEMBER_REMOVE", "club_member", memberId, null, null);
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

