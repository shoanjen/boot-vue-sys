package com.zoen.sys.service.activity;

import com.zoen.sys.domain.entity.Activity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zone
 * @description 活动管理服务
 * @since 2026-05-01
 */
public interface ActivityService {

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
    Activity createAndPublish(Long operatorUserId, Long clubId, String title, LocalDateTime startTime, LocalDateTime endTime,
                              String place, LocalDateTime signupDeadline, Integer signupLimit);

    /**
     * @param clubId 社团ID
     * @description 活动列表（已发布）
     * @return 活动列表
     */
    List<Activity> listPublished(Long clubId);

    /**
     * @param userId 用户ID
     * @param activityId 活动ID
     * @description 报名活动
     * @return 无
     */
    void signup(Long userId, Long activityId);

    /**
     * @param userId 用户ID
     * @param activityId 活动ID
     * @description 取消报名（截止前可取消）
     * @return 无
     */
    void cancelSignup(Long userId, Long activityId);
}

