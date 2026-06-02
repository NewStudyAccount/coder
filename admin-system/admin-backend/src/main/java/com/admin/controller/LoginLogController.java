package com.admin.controller;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.common.result.Result;
import com.admin.entity.LoginLog;
import com.admin.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/monitor/loginlog")
@RequiredArgsConstructor
public class LoginLogController {

    private final LoginLogService loginLogService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('monitor:loginlog:list')")
    public Result<PageResult<LoginLog>> list(PageParam pageParam,
                                              @RequestParam(required = false) String username,
                                              @RequestParam(required = false) Integer status) {
        return Result.success(loginLogService.selectLoginLogList(pageParam, username, status));
    }

    @DeleteMapping("/clean")
    @PreAuthorize("hasAuthority('monitor:loginlog:remove')")
    public Result<Void> clean() {
        loginLogService.cleanLoginLog();
        return Result.success();
    }
}
