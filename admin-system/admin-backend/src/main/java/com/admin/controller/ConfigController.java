package com.admin.controller;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.common.result.Result;
import com.admin.entity.Config;
import com.admin.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:config:list')")
    public Result<PageResult<Config>> list(PageParam pageParam,
                                            @RequestParam(required = false) String configName,
                                            @RequestParam(required = false) String configKey,
                                            @RequestParam(required = false) Integer configType) {
        return Result.success(configService.selectConfigList(pageParam, configName, configKey, configType));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:config:query')")
    public Result<Config> getInfo(@PathVariable Long id) {
        return Result.success(configService.selectConfigById(id));
    }

    @GetMapping("/key/{configKey}")
    public Result<String> getByKey(@PathVariable String configKey) {
        return Result.success(configService.selectConfigByKey(configKey));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:config:add')")
    public Result<Void> add(@RequestBody Config config) {
        configService.createConfig(config);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:config:edit')")
    public Result<Void> edit(@RequestBody Config config) {
        configService.updateConfig(config);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:config:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        configService.deleteConfig(id);
        return Result.success();
    }
}
