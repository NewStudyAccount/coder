package com.admin.controller;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.common.result.Result;
import com.admin.dto.RoleCreateRequest;
import com.admin.entity.Role;
import com.admin.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<PageResult<Role>> list(PageParam pageParam,
                                          @RequestParam(required = false) String roleName,
                                          @RequestParam(required = false) String roleKey,
                                          @RequestParam(required = false) Integer status) {
        return Result.success(roleService.selectRoleList(pageParam, roleName, roleKey, status));
    }

    @GetMapping("/all")
    public Result<List<Role>> all() {
        return Result.success(roleService.selectRoleAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<Role> getInfo(@PathVariable Long id) {
        return Result.success(roleService.selectRoleById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Void> add(@Valid @RequestBody RoleCreateRequest request) {
        roleService.createRole(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> edit(@PathVariable Long id, @Valid @RequestBody RoleCreateRequest request) {
        roleService.updateRole(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @GetMapping("/menuIds/{roleId}")
    public Result<List<Long>> getMenuIds(@PathVariable Long roleId) {
        return Result.success(roleService.selectMenuIdsByRoleId(roleId));
    }

    @PutMapping("/assignMenu")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> assignMenu(@RequestBody Map<String, Object> body) {
        Long roleId = Long.valueOf(body.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> menuIds = (List<Long>) body.get("menuIds");
        roleService.assignMenu(roleId, menuIds);
        return Result.success();
    }
}
