package com.zoen.sys.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zoen.sys.domain.entity.Activity;
import com.zoen.sys.domain.entity.Announcement;
import com.zoen.sys.domain.entity.Club;
import com.zoen.sys.domain.entity.ClubMember;
import com.zoen.sys.domain.entity.SysUser;
import com.zoen.sys.domain.enums.ActivityStatusEnum;
import com.zoen.sys.domain.enums.AnnouncementStatusEnum;
import com.zoen.sys.domain.enums.ClubStatusEnum;
import com.zoen.sys.domain.enums.ClubTypeEnum;
import com.zoen.sys.domain.enums.MemberRoleEnum;
import com.zoen.sys.domain.enums.MemberStatusEnum;
import com.zoen.sys.domain.enums.UserRoleEnum;
import com.zoen.sys.infrastructure.mapper.ActivityMapper;
import com.zoen.sys.infrastructure.mapper.AnnouncementMapper;
import com.zoen.sys.infrastructure.mapper.ClubMapper;
import com.zoen.sys.infrastructure.mapper.ClubMemberMapper;
import com.zoen.sys.infrastructure.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author zone
 * @description 启动时初始化演示数据（仅当数据库为空时插入）
 * @since 2026-05-01
 */
@Component
@RequiredArgsConstructor
public class BootstrapDataInitializer implements ApplicationRunner {

    private final SysUserMapper sysUserMapper;
    private final ClubMapper clubMapper;
    private final ClubMemberMapper clubMemberMapper;
    private final ActivityMapper activityMapper;
    private final AnnouncementMapper announcementMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * @param args 启动参数
     * @description 初始化演示用户/社团/活动/公告数据
     * @return 无
     */
    @Override
    public void run(ApplicationArguments args) {
        Long userCount = sysUserMapper.selectCount(new LambdaQueryWrapper<>());
        if (userCount != null && userCount > 0) {
            return;
        }

        String rawPwd = "12345678";
        SysUser student = SysUser.builder()
                .account("20210001")
                .passwordHash(passwordEncoder.encode(rawPwd))
                .name("张同学")
                .studentNo("20210001")
                .role(UserRoleEnum.STUDENT.getCode())
                .status(1)
                .isDeleted(0)
                .build();
        sysUserMapper.insert(student);

        SysUser clubAdmin = SysUser.builder()
                .account("A10001")
                .passwordHash(passwordEncoder.encode(rawPwd))
                .name("李社长")
                .role(UserRoleEnum.CLUB_ADMIN.getCode())
                .status(1)
                .isDeleted(0)
                .build();
        sysUserMapper.insert(clubAdmin);

        SysUser platformAdmin = SysUser.builder()
                .account("P10001")
                .passwordHash(passwordEncoder.encode(rawPwd))
                .name("王老师")
                .role(UserRoleEnum.PLATFORM_ADMIN.getCode())
                .status(1)
                .isDeleted(0)
                .build();
        sysUserMapper.insert(platformAdmin);

        Club club = Club.builder()
                .name("AI 学术社")
                .type(ClubTypeEnum.ACADEMIC.getCode())
                .intro("面向全校的 AI 学术交流与实践社团，定期组织分享会、工作坊与项目协作。")
                .advisorName("王老师")
                .contact("ai-club@school.edu")
                .status(ClubStatusEnum.APPROVED.getCode())
                .ownerUserId(clubAdmin.getId())
                .reviewedBy(platformAdmin.getId())
                .reviewedAt(LocalDateTime.now())
                .isDeleted(0)
                .build();
        clubMapper.insert(club);

        ClubMember adminMember = ClubMember.builder()
                .clubId(club.getId())
                .userId(clubAdmin.getId())
                .role(MemberRoleEnum.ADMIN.getCode())
                .status(MemberStatusEnum.ACTIVE.getCode())
                .joinedAt(LocalDateTime.now())
                .isDeleted(0)
                .build();
        clubMemberMapper.insert(adminMember);

        Activity activity = Activity.builder()
                .clubId(club.getId())
                .title("从零训练一个小模型 · 工作坊")
                .startTime(LocalDateTime.now().plusDays(3).withSecond(0).withNano(0))
                .endTime(LocalDateTime.now().plusDays(3).plusHours(2).withSecond(0).withNano(0))
                .place("教学楼 A-301")
                .signupDeadline(LocalDateTime.now().plusDays(2).withSecond(0).withNano(0))
                .signupLimit(30)
                .signupCount(0)
                .status(ActivityStatusEnum.PUBLISHED.getCode())
                .createdBy(clubAdmin.getId())
                .isDeleted(0)
                .build();
        activityMapper.insert(activity);

        Announcement ann = Announcement.builder()
                .clubId(club.getId())
                .title("欢迎加入 AI 学术社")
                .content("本周将组织首次线下交流会，欢迎同学们积极报名参与。")
                .status(AnnouncementStatusEnum.PUBLISHED.getCode())
                .publishedAt(LocalDateTime.now())
                .publishedBy(clubAdmin.getId())
                .isDeleted(0)
                .build();
        announcementMapper.insert(ann);
    }
}

