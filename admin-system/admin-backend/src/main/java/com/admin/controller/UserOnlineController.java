package com.admin.controller;

import com.admin.common.result.Result;
import com.admin.security.LoginUser;
import com.admin.service.UserOnlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitor/online")
@RequiredArgsConstructor
public class UserOnlineController {

    private final UserOnlineService userOnlineService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('monitor:online:list')")
    public Result<List<LoginUser>> list(@RequestParam(required = false) String username,
                                         @RequestParam(required = false) String ipaddr) {
        return Result.success(userOnlineService.selectOnlineList(username, ipaddr));
    }

    @DeleteMapping("/forceLogout/{userId}")
    @PreAuthorize("hasAuthority('monitor:online:forceLogout')")
    public Result<Void> forceLogout(@PathVariable Long userId) {
        userOnlineService.forceLogout(userId);
        return Result.success();
    }
}
