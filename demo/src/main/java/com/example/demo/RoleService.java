package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RoleService {
    private final Map<Long, Role> roleMap = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(100);

    public List<Role> listRoles() {
        return new ArrayList<>(roleMap.values());
    }

    public Role getRole(Long id) {
        return roleMap.get(id);
    }

    public Role addRole(Role role) {
        long id = idGen.incrementAndGet();
        role.setId(id);
        if (role.getPermissions() == null) {
            role.setPermissions(new ArrayList<>());
        }
        roleMap.put(id, role);
        return role;
    }

    public Role updateRole(Long id, Role role) {
        if (!roleMap.containsKey(id)) return null;
        role.setId(id);
        if (role.getPermissions() == null) {
            role.setPermissions(new ArrayList<>());
        }
        roleMap.put(id, role);
        return role;
    }

    public boolean deleteRole(Long id) {
        return roleMap.remove(id) != null;
    }

    // 新增：获取角色权限
    public List<String> getRolePermissions(Long roleId) {
        Role role = roleMap.get(roleId);
        if (role == null) return new ArrayList<>();
        return role.getPermissions() == null ? new ArrayList<>() : role.getPermissions();
    }

    // 新增：设置角色权限
    public boolean setRolePermissions(Long roleId, List<String> permissions) {
        Role role = roleMap.get(roleId);
        if (role == null) return false;
        role.setPermissions(permissions);
        return true;
    }
}
