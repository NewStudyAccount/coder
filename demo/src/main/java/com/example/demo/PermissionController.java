package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/permission/list")
    public Map<String, Object> listPermissions() {
        List<Permission> permissions = permissionService.listPermissions();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", permissions);
        return result;
    }

    @GetMapping("/permission/get")
    public Map<String, Object> getPermission(@RequestParam Long id) {
        Permission permission = permissionService.getPermission(id);
        Map<String, Object> result = new HashMap<>();
        if (permission == null) {
            result.put("code", 404);
            result.put("msg", "权限不存在");
        } else {
            result.put("code", 0);
            result.put("msg", "success");
            result.put("data", permission);
        }
        return result;
    }

    @PostMapping("/permission/add")
    public Map<String, Object> addPermission(@RequestBody Permission permission) {
        Permission newPermission = permissionService.addPermission(permission);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", newPermission);
        return result;
    }

    @PostMapping("/permission/update")
    public Map<String, Object> updatePermission(@RequestParam Long id, @RequestBody Permission permission) {
        Permission updated = permissionService.updatePermission(id, permission);
        Map<String, Object> result = new HashMap<>();
        if (updated == null) {
            result.put("code", 404);
            result.put("msg", "权限不存在");
        } else {
            result.put("code", 0);
            result.put("msg", "success");
            result.put("data", updated);
        }
        return result;
    }

    @PostMapping("/permission/delete")
    public Map<String, Object> deletePermission(@RequestParam Long id) {
        boolean ok = permissionService.deletePermission(id);
        Map<String, Object> result = new HashMap<>();
        if (ok) {
            result.put("code", 0);
            result.put("msg", "success");
        } else {
            result.put("code", 404);
            result.put("msg", "权限不存在");
        }
        return result;
    }
}