package com.zoen.sys.service.me.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zoen.sys.domain.entity.Activity;
import com.zoen.sys.domain.entity.ActivitySignup;
import com.zoen.sys.domain.entity.Club;
import com.zoen.sys.domain.entity.ClubMember;
import com.zoen.sys.domain.entity.JoinRequest;
import com.zoen.sys.domain.enums.ActivitySignupStatusEnum;
import com.zoen.sys.domain.enums.MemberStatusEnum;
import com.zoen.sys.infrastructure.mapper.ActivityMapper;
import com.zoen.sys.infrastructure.mapper.ActivitySignupMapper;
import com.zoen.sys.infrastructure.mapper.ClubMapper;
import com.zoen.sys.infrastructure.mapper.ClubMemberMapper;
import com.zoen.sys.infrastructure.mapper.JoinRequestMapper;
import com.zoen.sys.service.me.MeService;
import com.zoen.sys.service.me.model.MyCenterData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zone
 * @description 我的中心服务实现
 * @since 2026-05-01
 */
@Service
@RequiredArgsConstructor
public class MeServiceImpl implements MeService {

    private final ClubMapper clubMapper;
    private final JoinRequestMapper joinRequestMapper;
    private final ClubMemberMapper clubMemberMapper;
    private final ActivitySignupMapper activitySignupMapper;
    private final ActivityMapper activityMapper;

    /**
     * @param userId 用户ID
     * @description 获取我的中心数据
     * @return 我的中心数据聚合
     */
    @Override
    public MyCenterData getMyCenter(Long userId) {
        List<Club> myCreatedClubs = clubMapper.selectList(new LambdaQueryWrapper<Club>()
                .eq(Club::getOwnerUserId, userId)
                .orderByDesc(Club::getId));

        List<JoinRequest> myJoinRequests = joinRequestMapper.selectList(new LambdaQueryWrapper<JoinRequest>()
                .eq(JoinRequest::getUserId, userId)
                .orderByDesc(JoinRequest::getId));

        List<ClubMember> myClubMembers = clubMemberMapper.selectList(new LambdaQueryWrapper<ClubMember>()
                .eq(ClubMember::getUserId, userId)
                .eq(ClubMember::getStatus, MemberStatusEnum.ACTIVE.getCode())
                .orderByDesc(ClubMember::getId));

        List<Club> myClubs = fetchClubsByMembers(myClubMembers);

        List<ActivitySignup> mySignups = activitySignupMapper.selectList(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getUserId, userId)
                .eq(ActivitySignup::getStatus, ActivitySignupStatusEnum.SIGNED.getCode())
                .orderByDesc(ActivitySignup::getId));

        List<Activity> myActivities = fetchActivitiesBySignups(mySignups);

        return MyCenterData.builder()
                .myCreatedClubs(myCreatedClubs)
                .myJoinRequests(myJoinRequests)
                .myClubMembers(myClubMembers)
                .myClubs(myClubs)
                .mySignups(mySignups)
                .myActivities(myActivities)
                .build();
    }

    private List<Club> fetchClubsByMembers(List<ClubMember> members) {
        if (members == null || members.isEmpty()) return Collections.emptyList();
        Set<Long> clubIds = members.stream().map(ClubMember::getClubId).collect(Collectors.toSet());
        if (clubIds.isEmpty()) return Collections.emptyList();
        return clubMapper.selectList(new LambdaQueryWrapper<Club>().in(Club::getId, clubIds));
    }

    private List<Activity> fetchActivitiesBySignups(List<ActivitySignup> signups) {
        if (signups == null || signups.isEmpty()) return Collections.emptyList();
        Set<Long> ids = signups.stream().map(ActivitySignup::getActivityId).collect(Collectors.toSet());
        if (ids.isEmpty()) return Collections.emptyList();
        return activityMapper.selectList(new LambdaQueryWrapper<Activity>().in(Activity::getId, ids));
    }
}

