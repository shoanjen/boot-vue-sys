package com.zoen.sys.service.announcement.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zoen.sys.common.exception.BizException;
import com.zoen.sys.common.exception.ErrorCode;
import com.zoen.sys.domain.entity.Announcement;
import com.zoen.sys.domain.enums.AnnouncementStatusEnum;
import com.zoen.sys.infrastructure.mapper.AnnouncementMapper;
import com.zoen.sys.service.announcement.AnnouncementService;
import com.zoen.sys.service.audit.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zone
 * @description 公告服务实现
 * @since 2026-05-01
 */
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final AuditLogService auditLogService;

    /**
     * @param operatorUserId 发布人用户ID（社团管理员）
     * @param clubId 社团ID
     * @param title 标题
     * @param content 内容
     * @description 发布公告
     * @return 公告记录
     */
    @Override
    public Announcement publish(Long operatorUserId, Long clubId, String title, String content) {
        Announcement a = Announcement.builder()
                .clubId(clubId)
                .title(title)
                .content(content)
                .status(AnnouncementStatusEnum.PUBLISHED.getCode())
                .publishedAt(LocalDateTime.now())
                .publishedBy(operatorUserId)
                .isDeleted(0)
                .build();
        announcementMapper.insert(a);
        auditLogService.write(operatorUserId, "ANNOUNCEMENT_PUBLISH", "announcement", a.getId(), null, null);
        return a;
    }

    /**
     * @param clubId 社团ID
     * @description 公告列表
     * @return 公告列表
     */
    @Override
    public List<Announcement> list(Long clubId) {
        return announcementMapper.selectList(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getClubId, clubId)
                .eq(Announcement::getStatus, AnnouncementStatusEnum.PUBLISHED.getCode())
                .orderByDesc(Announcement::getPublishedAt));
    }

    /**
     * @param announcementId 公告ID
     * @description 公告详情
     * @return 公告记录
     */
    @Override
    public Announcement detail(Long announcementId) {
        Announcement a = announcementMapper.selectById(announcementId);
        if (a == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "公告不存在");
        }
        return a;
    }
}

