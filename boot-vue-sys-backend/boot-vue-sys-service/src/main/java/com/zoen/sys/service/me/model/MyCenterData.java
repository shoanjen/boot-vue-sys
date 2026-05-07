package com.zoen.sys.service.me.model;

import com.zoen.sys.domain.entity.Activity;
import com.zoen.sys.domain.entity.ActivitySignup;
import com.zoen.sys.domain.entity.Club;
import com.zoen.sys.domain.entity.ClubMember;
import com.zoen.sys.domain.entity.JoinRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zone
 * @description 我的中心数据聚合
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCenterData {

    private List<Club> myCreatedClubs;
    private List<JoinRequest> myJoinRequests;
    private List<ClubMember> myClubMembers;
    private List<Club> myClubs;
    private List<ActivitySignup> mySignups;
    private List<Activity> myActivities;
}

