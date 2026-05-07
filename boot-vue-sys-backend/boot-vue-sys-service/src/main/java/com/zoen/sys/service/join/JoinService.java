package com.zoen.sys.service.join;

import com.zoen.sys.domain.entity.JoinRequest;

import java.util.List;

/**
 * @author zone
 * @description 入社流程服务
 * @since 2026-05-01
 */
public interface JoinService {

    /**
     * @param userId 用户ID
     * @param clubId 社团ID
     * @param reason 申请理由
     * @description 提交入社申请
     * @return 入社申请记录
     */
    JoinRequest apply(Long userId, Long clubId, String reason);

    /**
     * @param userId 用户ID
     * @description 我的入社申请列表
     * @return 入社申请列表
     */
    List<JoinRequest> myList(Long userId);

    /**
     * @param operatorUserId 操作人用户ID（社团管理员）
     * @param clubId 社团ID
     * @description 待审核入社申请列表
     * @return 入社申请列表
     */
    List<JoinRequest> pendingList(Long operatorUserId, Long clubId);

    /**
     * @param operatorUserId 操作人用户ID（社团管理员）
     * @param requestId 入社申请ID
     * @param pass 是否通过
     * @param rejectReason 驳回原因（可选）
     * @description 审核入社申请
     * @return 无
     */
    void review(Long operatorUserId, Long requestId, boolean pass, String rejectReason);
}

