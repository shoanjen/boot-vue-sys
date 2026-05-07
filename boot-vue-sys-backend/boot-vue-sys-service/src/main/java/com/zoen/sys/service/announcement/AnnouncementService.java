package com.zoen.sys.service.announcement;

import com.zoen.sys.domain.entity.Announcement;

import java.util.List;

/**
 * @author zone
 * @description 公告服务
 * @since 2026-05-01
 */
public interface AnnouncementService {

    /**
     * @param operatorUserId 发布人用户ID（社团管理员）
     * @param clubId 社团ID
     * @param title 标题
     * @param content 内容
     * @description 发布公告
     * @return 公告记录
     */
    Announcement publish(Long operatorUserId, Long clubId, String title, String content);

    /**
     * @param clubId 社团ID
     * @description 公告列表
     * @return 公告列表
     */
    List<Announcement> list(Long clubId);

    /**
     * @param announcementId 公告ID
     * @description 公告详情
     * @return 公告记录
     */
    Announcement detail(Long announcementId);
}

