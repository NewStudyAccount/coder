package com.admin.controller;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.common.result.Result;
import com.admin.entity.OperLog;
import com.admin.service.OperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/monitor/operlog")
@RequiredArgsConstructor
public class OperLogController {

    private final OperLogService operLogService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('monitor:operlog:list')")
    public Result<PageResult<OperLog>> list(PageParam pageParam,
                                             @RequestParam(required = false) String title,
                                             @RequestParam(required = false) Integer businessType,
                                             @RequestParam(required = false) Integer status) {
        return Result.success(operLogService.selectOperLogList(pageParam, title, businessType, status));
    }

    @DeleteMapping("/clean")
    @PreAuthorize("hasAuthority('monitor:operlog:remove')")
    public Result<Void> clean() {
        operLogService.cleanOperLog();
        return Result.success();
    }
}
