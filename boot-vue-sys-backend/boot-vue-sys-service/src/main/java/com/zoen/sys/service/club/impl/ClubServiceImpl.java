package com.zoen.sys.service.club.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zoen.sys.common.exception.BizException;
import com.zoen.sys.common.exception.ErrorCode;
import com.zoen.sys.domain.entity.Club;
import com.zoen.sys.domain.enums.ClubStatusEnum;
import com.zoen.sys.infrastructure.mapper.ClubMapper;
import com.zoen.sys.service.audit.AuditLogService;
import com.zoen.sys.service.club.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zone
 * @description 社团服务实现
 * @since 2026-05-01
 */
@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubMapper clubMapper;
    private final AuditLogService auditLogService;

    /**
     * @param keyword 关键词（可选）
     * @param type 社团类型（可选）
     * @description 社团列表（仅上架）
     * @return 社团列表
     */
    @Override
    public List<Club> listApproved(String keyword, Integer type) {
        LambdaQueryWrapper<Club> qw = new LambdaQueryWrapper<Club>()
                .eq(Club::getStatus, ClubStatusEnum.APPROVED.getCode())
                .orderByDesc(Club::getId);
        if (keyword != null && !keyword.isBlank()) {
            qw.like(Club::getName, keyword.trim());
        }
        if (type != null) {
            qw.eq(Club::getType, type);
        }
        return clubMapper.selectList(qw);
    }

    /**
     * @param clubId 社团ID
     * @description 社团详情
     * @return 社团信息
     */
    @Override
    public Club detail(Long clubId) {
        Club club = clubMapper.selectById(clubId);
        if (club == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "社团不存在");
        }
        return club;
    }

    /**
     * @param ownerUserId 申请人用户ID
     * @param name 社团名称
     * @param type 社团类型
     * @param intro 简介
     * @param advisorName 指导老师（可选）
     * @param contact 联系方式
     * @description 提交创建社团申请
     * @return 创建的社团记录
     */
    @Override
    public Club applyCreate(Long ownerUserId, String name, Integer type, String intro, String advisorName, String contact) {
        try {
            Club club = Club.builder()
                    .name(name)
                    .type(type)
                    .intro(intro)
                    .advisorName(advisorName)
                    .contact(contact)
                    .status(ClubStatusEnum.PENDING.getCode())
                    .ownerUserId(ownerUserId)
                    .isDeleted(0)
                    .build();
            clubMapper.insert(club);
            auditLogService.write(ownerUserId, "CLUB_APPLY_CREATE", "club", club.getId(), null, null);
            return club;
        } catch (DuplicateKeyException e) {
            throw new BizException(ErrorCode.BIZ_ERROR, "社团名称已存在");
        }
    }

    /**
     * @param ownerUserId 用户ID
     * @description 获取我创建的社团（含待审/驳回/上架）
     * @return 社团列表
     */
    @Override
    public List<Club> listMyCreated(Long ownerUserId) {
        return clubMapper.selectList(new LambdaQueryWrapper<Club>()
                .eq(Club::getOwnerUserId, ownerUserId)
                .orderByDesc(Club::getId));
    }
}

