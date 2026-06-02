package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BusinessException;
import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.dto.RoleCreateRequest;
import com.admin.entity.Menu;
import com.admin.entity.Role;
import com.admin.entity.RoleMenu;
import com.admin.mapper.MenuMapper;
import com.admin.mapper.RoleMapper;
import com.admin.mapper.RoleMenuMapper;
import com.admin.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final MenuMapper menuMapper;

    @Override
    public PageResult<Role> selectRoleList(PageParam pageParam, String roleName, String roleKey, Integer status) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(roleName), Role::getRoleName, roleName)
               .like(StrUtil.isNotBlank(roleKey), Role::getRoleKey, roleKey)
               .eq(status != null, Role::getStatus, status)
               .orderByAsc(Role::getRoleSort);

        Page<Role> page = roleMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public List<Role> selectRoleAll() {
        return roleMapper.selectList(new LambdaQueryWrapper<Role>().eq(Role::getStatus, 1).orderByAsc(Role::getRoleSort));
    }

    @Override
    public Role selectRoleById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    @Transactional
    public void createRole(RoleCreateRequest request) {
        Role exist = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, request.getRoleKey()));
        if (exist != null) {
            throw new BusinessException("角色标识已存在");
        }

        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRoleSort(request.getRoleSort());
        role.setDataScope(request.getDataScope() != null ? request.getDataScope() : 1);
        role.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        role.setRemark(request.getRemark());
        role.setCreateBy("system");
        roleMapper.insert(role);

        saveRoleMenus(role.getId(), request.getMenuIds());
    }

    @Override
    @Transactional
    public void updateRole(Long id, RoleCreateRequest request) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRoleSort(request.getRoleSort());
        if (request.getDataScope() != null) role.setDataScope(request.getDataScope());
        if (request.getStatus() != null) role.setStatus(request.getStatus());
        if (request.getRemark() != null) role.setRemark(request.getRemark());
        role.setUpdateBy("system");
        roleMapper.updateById(role);

        if (request.getMenuIds() != null) {
            roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, id));
            saveRoleMenus(id, request.getMenuIds());
        }
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (id == 1L) {
            throw new BusinessException("不允许删除超级管理员角色");
        }
        roleMapper.deleteById(id);
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, id));
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        List<RoleMenu> list = roleMenuMapper.selectList(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        return list.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignMenu(Long roleId, List<Long> menuIds) {
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        saveRoleMenus(roleId, menuIds);
    }

    private void saveRoleMenus(Long roleId, List<Long> menuIds) {
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                roleMenuMapper.insert(rm);
            }
        }
    }
}
