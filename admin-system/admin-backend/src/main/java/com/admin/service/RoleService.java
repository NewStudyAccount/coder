package com.admin.service;

import com.admin.entity.Role;
import com.admin.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    public List<Role> getList(String roleName, String roleKey, Integer status) {
        return roleMapper.selectList(roleName, roleKey, status);
    }

    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Transactional
    public void add(Role role) {
        Role existRole = roleMapper.selectByRoleKey(role.getRoleKey());
        if (existRole != null) {
            throw new RuntimeException("角色标识已存在");
        }
        role.setStatus(role.getStatus() != null ? role.getStatus() : 1);
        roleMapper.insert(role);

        if (role.getMenuIds() != null && !role.getMenuIds().isEmpty()) {
            for (Long menuId : role.getMenuIds()) {
                roleMapper.insertRoleMenu(role.getId(), menuId);
            }
        }
    }

    @Transactional
    public void update(Role role) {
        roleMapper.update(role);

        if (role.getMenuIds() != null) {
            roleMapper.deleteRoleMenusByRoleId(role.getId());
            for (Long menuId : role.getMenuIds()) {
                roleMapper.insertRoleMenu(role.getId(), menuId);
            }
        }
    }

    @Transactional
    public void deleteById(Long id) {
        roleMapper.deleteRoleMenusByRoleId(id);
        roleMapper.deleteById(id);
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            roleMapper.deleteRoleMenusByRoleId(id);
        }
        roleMapper.deleteBatch(ids);
    }

    public List<Long> getMenuIds(Long roleId) {
        return roleMapper.selectMenuIdsByRoleId(roleId);
    }

    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMapper.deleteRoleMenusByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                roleMapper.insertRoleMenu(roleId, menuId);
            }
        }
    }
}
