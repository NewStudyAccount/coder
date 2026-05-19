package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.Role;
import com.admin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public Result<List<Role>> list(@RequestParam(required = false) String roleName,
                                   @RequestParam(required = false) String roleKey,
                                   @RequestParam(required = false) Integer status) {
        List<Role> list = roleService.getList(roleName, roleKey, status);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Role role) {
        try {
            roleService.add(role);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    public Result<Void> update(@RequestBody Role role) {
        try {
            roleService.update(role);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        roleService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        roleService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{roleId}/menus")
    public Result<List<Long>> getMenuIds(@PathVariable Long roleId) {
        List<Long> menuIds = roleService.getMenuIds(roleId);
        return Result.success(menuIds);
    }

    @PutMapping("/{roleId}/menus")
    public Result<Void> assignMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.success();
    }
}
