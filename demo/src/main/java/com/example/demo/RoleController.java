package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/role/list")
    public Map<String, Object> listRoles() {
        List<Role> roles = roleService.listRoles();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", roles);
        return result;
    }

    @GetMapping("/role/get")
    public Map<String, Object> getRole(@RequestParam Long id) {
        Role role = roleService.getRole(id);
        Map<String, Object> result = new HashMap<>();
        if (role == null) {
            result.put("code", 404);
            result.put("msg", "角色不存在");
        } else {
            result.put("code", 0);
            result.put("msg", "success");
            result.put("data", role);
        }
        return result;
    }

    @PostMapping("/role/add")
    public Map<String, Object> addRole(@RequestBody Role role) {
        Role newRole = roleService.addRole(role);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", newRole);
        return result;
    }

    @PostMapping("/role/update")
    public Map<String, Object> updateRole(@RequestParam Long id, @RequestBody Role role) {
        Role updated = roleService.updateRole(id, role);
        Map<String, Object> result = new HashMap<>();
        if (updated == null) {
            result.put("code", 404);
            result.put("msg", "角色不存在");
        } else {
            result.put("code", 0);
            result.put("msg", "success");
            result.put("data", updated);
        }
        return result;
    }

    @PostMapping("/role/delete")
    public Map<String, Object> deleteRole(@RequestParam Long id) {
        boolean ok = roleService.deleteRole(id);
        Map<String, Object> result = new HashMap<>();
        if (ok) {
            result.put("code", 0);
            result.put("msg", "success");
        } else {
            result.put("code", 404);
            result.put("msg", "角色不存在");
        }
        return result;
    }
}