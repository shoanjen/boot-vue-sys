package com.zoen.sys.service.activity.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zoen.sys.common.exception.BizException;
import com.zoen.sys.common.exception.ErrorCode;
import com.zoen.sys.domain.entity.Activity;
import com.zoen.sys.domain.entity.ActivitySignup;
import com.zoen.sys.domain.entity.ClubMember;
import com.zoen.sys.domain.enums.ActivitySignupStatusEnum;
import com.zoen.sys.domain.enums.ActivityStatusEnum;
import com.zoen.sys.domain.enums.MemberStatusEnum;
import com.zoen.sys.infrastructure.mapper.ActivityMapper;
import com.zoen.sys.infrastructure.mapper.ActivitySignupMapper;
import com.zoen.sys.infrastructure.mapper.ClubMemberMapper;
import com.zoen.sys.service.activity.ActivityService;
import com.zoen.sys.service.audit.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zone
 * @description 活动管理服务实现
 * @since 2026-05-01
 */
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final ActivitySignupMapper activitySignupMapper;
    private final ClubMemberMapper clubMemberMapper;
    private final AuditLogService auditLogService;

    /**
     * @param operatorUserId 操作人用户ID（社团管理员）
     * @param clubId 社团ID
     * @param title 标题
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param place 地点
     * @param signupDeadline 报名截止
     * @param signupLimit 人数上限（0不限）
     * @description 创建并发布活动
     * @return 活动记录
     */
    @Override
    public Activity createAndPublish(Long operatorUserId, Long clubId, String title, LocalDateTime startTime, LocalDateTime endTime,
                                     String place, LocalDateTime signupDeadline, Integer signupLimit) {
        if (endTime.isBefore(startTime)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "结束时间必须大于开始时间");
        }
        if (signupDeadline.isAfter(startTime)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "报名截止必须小于等于开始时间");
        }
        Activity a = Activity.builder()
                .clubId(clubId)
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .place(place)
                .signupDeadline(signupDeadline)
                .signupLimit(signupLimit == null ? 0 : signupLimit)
                .signupCount(0)
                .status(ActivityStatusEnum.PUBLISHED.getCode())
                .createdBy(operatorUserId)
                .isDeleted(0)
                .build();
        activityMapper.insert(a);
        auditLogService.write(operatorUserId, "ACTIVITY_PUBLISH", "activity", a.getId(), null, null);
        return a;
    }

    /**
     * @param clubId 社团ID
     * @description 活动列表（已发布）
     * @return 活动列表
     */
    @Override
    public List<Activity> listPublished(Long clubId) {
        return activityMapper.selectList(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getClubId, clubId)
                .eq(Activity::getStatus, ActivityStatusEnum.PUBLISHED.getCode())
                .orderByDesc(Activity::getId));
    }

    /**
     * @param userId 用户ID
     * @param activityId 活动ID
     * @description 报名活动
     * @return 无
     */
    @Override
    @Transactional
    public void signup(Long userId, Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "活动不存在");
        }
        if (activity.getStatus() == null || activity.getStatus() != ActivityStatusEnum.PUBLISHED.getCode()) {
            throw new BizException(ErrorCode.BIZ_ERROR, "活动不可报名");
        }
        if (activity.getSignupDeadline() != null && LocalDateTime.now().isAfter(activity.getSignupDeadline())) {
            throw new BizException(ErrorCode.BIZ_ERROR, "报名已截止");
        }
        ClubMember member = clubMemberMapper.selectOne(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getClubId, activity.getClubId())
                .eq(ClubMember::getUserId, userId)
                .eq(ClubMember::getStatus, MemberStatusEnum.ACTIVE.getCode())
                .last("limit 1"));
        if (member == null) {
            throw new BizException(ErrorCode.BIZ_ERROR, "加入社团后可报名");
        }

        try {
            ActivitySignup signup = ActivitySignup.builder()
                    .activityId(activityId)
                    .userId(userId)
                    .status(ActivitySignupStatusEnum.SIGNED.getCode())
                    .signupAt(LocalDateTime.now())
                    .isDeleted(0)
                    .build();
            activitySignupMapper.insert(signup);
        } catch (DuplicateKeyException e) {
            throw new BizException(ErrorCode.BIZ_ERROR, "你已报名该活动");
        }

        if (activity.getSignupLimit() != null && activity.getSignupLimit() > 0) {
            UpdateWrapper<Activity> uw = new UpdateWrapper<>();
            uw.eq("id", activityId)
                    .lt("signup_count", activity.getSignupLimit())
                    .setSql("signup_count = signup_count + 1");
            int updated = activityMapper.update(null, uw);
            if (updated <= 0) {
                throw new BizException(ErrorCode.BIZ_ERROR, "报名人数已满");
            }
        } else {
            UpdateWrapper<Activity> uw = new UpdateWrapper<>();
            uw.eq("id", activityId).setSql("signup_count = signup_count + 1");
            activityMapper.update(null, uw);
        }

        auditLogService.write(userId, "ACTIVITY_SIGNUP", "activity", activityId, null, null);
    }

    /**
     * @param userId 用户ID
     * @param activityId 活动ID
     * @description 取消报名（截止前可取消）
     * @return 无
     */
    @Override
    @Transactional
    public void cancelSignup(Long userId, Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "活动不存在");
        }
        if (activity.getSignupDeadline() != null && LocalDateTime.now().isAfter(activity.getSignupDeadline())) {
            throw new BizException(ErrorCode.BIZ_ERROR, "报名截止后不可取消");
        }
        ActivitySignup signup = activitySignupMapper.selectOne(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getUserId, userId)
                .eq(ActivitySignup::getStatus, ActivitySignupStatusEnum.SIGNED.getCode())
                .last("limit 1"));
        if (signup == null) {
            throw new BizException(ErrorCode.BIZ_ERROR, "未找到可取消的报名记录");
        }
        signup.setStatus(ActivitySignupStatusEnum.CANCELED.getCode());
        signup.setCancelAt(LocalDateTime.now());
        activitySignupMapper.updateById(signup);

        UpdateWrapper<Activity> uw = new UpdateWrapper<>();
        uw.eq("id", activityId).gt("signup_count", 0).setSql("signup_count = signup_count - 1");
        activityMapper.update(null, uw);

        auditLogService.write(userId, "ACTIVITY_CANCEL_SIGNUP", "activity", activityId, null, null);
    }
}
