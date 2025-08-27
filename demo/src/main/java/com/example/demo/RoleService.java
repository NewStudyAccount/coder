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
        roleMap.put(id, role);
        return role;
    }

    public Role updateRole(Long id, Role role) {
        if (!roleMap.containsKey(id)) return null;
        role.setId(id);
        roleMap.put(id, role);
        return role;
    }

    public boolean deleteRole(Long id) {
        return roleMap.remove(id) != null;
    }
}