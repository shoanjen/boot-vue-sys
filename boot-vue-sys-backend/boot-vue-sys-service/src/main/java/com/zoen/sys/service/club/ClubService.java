package com.zoen.sys.service.club;

import com.zoen.sys.domain.entity.Club;

import java.util.List;

/**
 * @author zone
 * @description 社团服务
 * @since 2026-05-01
 */
public interface ClubService {

    /**
     * @param keyword 关键词（可选）
     * @param type 社团类型（可选）
     * @description 社团列表（仅上架）
     * @return 社团列表
     */
    List<Club> listApproved(String keyword, Integer type);

    /**
     * @param clubId 社团ID
     * @description 社团详情
     * @return 社团信息
     */
    Club detail(Long clubId);

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
    Club applyCreate(Long ownerUserId, String name, Integer type, String intro, String advisorName, String contact);

    /**
     * @param ownerUserId 用户ID
     * @description 获取我创建的社团（含待审/驳回/上架）
     * @return 社团列表
     */
    List<Club> listMyCreated(Long ownerUserId);
}

