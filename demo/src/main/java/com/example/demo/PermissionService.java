package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PermissionService {
    private final Map<Long, Permission> permissionMap = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(100);

    public List<Permission> listPermissions() {
        return new ArrayList<>(permissionMap.values());
    }

    public Permission getPermission(Long id) {
        return permissionMap.get(id);
    }

    public Permission addPermission(Permission permission) {
        long id = idGen.incrementAndGet();
        permission.setId(id);
        permissionMap.put(id, permission);
        return permission;
    }

    public Permission updatePermission(Long id, Permission permission) {
        if (!permissionMap.containsKey(id)) return null;
        permission.setId(id);
        permissionMap.put(id, permission);
        return permission;
    }

    public boolean deletePermission(Long id) {
        return permissionMap.remove(id) != null;
    }
}