package com.zoen.sys.service.member;

import com.zoen.sys.domain.entity.ClubMember;

import java.util.List;

/**
 * @author zone
 * @description 成员管理服务
 * @since 2026-05-01
 */
public interface MemberService {

    /**
     * @param operatorUserId 操作人用户ID（社团管理员）
     * @param clubId 社团ID
     * @description 成员列表
     * @return 成员列表
     */
    List<ClubMember> listMembers(Long operatorUserId, Long clubId);

    /**
     * @param operatorUserId 操作人用户ID（社团管理员）
     * @param memberId 成员记录ID
     * @description 移除成员
     * @return 无
     */
    void removeMember(Long operatorUserId, Long memberId);
}

