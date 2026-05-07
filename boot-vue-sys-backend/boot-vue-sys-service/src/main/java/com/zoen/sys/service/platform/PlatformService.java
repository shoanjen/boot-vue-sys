package com.zoen.sys.service.platform;

import com.zoen.sys.domain.entity.Club;

import java.util.List;

/**
 * @author zone
 * @description 平台管理服务
 * @since 2026-05-01
 */
public interface PlatformService {

    /**
     * @description 待审核社团列表
     * @return 社团列表
     */
    List<Club> listPendingClubs();

    /**
     * @param operatorUserId 操作人用户ID
     * @param clubId 社团ID
     * @param pass 是否通过
     * @param rejectReason 驳回原因（驳回时必填）
     * @description 审核社团创建申请
     * @return 无
     */
    void reviewClub(Long operatorUserId, Long clubId, boolean pass, String rejectReason);
}

