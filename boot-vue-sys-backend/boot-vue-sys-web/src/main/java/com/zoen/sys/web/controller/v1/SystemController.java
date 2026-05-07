package com.zoen.sys.web.controller.v1;

import com.zoen.sys.common.model.ApiResponse;
import com.zoen.sys.service.system.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zone
 * @description 系统基础接口
 * @since 2026-05-01
 */
@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    /**
     * @description 健康检查接口
     * @return 健康状态
     */
    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success(systemService.ping());
    }
}
