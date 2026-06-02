package com.admin.controller;

import com.admin.common.result.Result;
import com.admin.dto.MenuCreateRequest;
import com.admin.entity.Menu;
import com.admin.security.LoginUser;
import com.admin.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/tree")
    public Result<List<Menu>> tree() {
        LoginUser loginUser = (LoginUser) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return Result.success(menuService.selectMenuTree(loginUser.getUserId()));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<Menu>> list(@RequestParam(required = false) String menuName,
                                    @RequestParam(required = false) Integer status) {
        return Result.success(menuService.selectMenuList(menuName, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Result<Menu> getInfo(@PathVariable Long id) {
        return Result.success(menuService.selectMenuById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    public Result<Void> add(@Valid @RequestBody MenuCreateRequest request) {
        menuService.createMenu(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<Void> edit(@PathVariable Long id, @Valid @RequestBody MenuCreateRequest request) {
        menuService.updateMenu(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }
}
