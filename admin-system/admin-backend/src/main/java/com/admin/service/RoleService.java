package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.dto.RoleCreateRequest;
import com.admin.entity.Role;

import java.util.List;

public interface RoleService {

    PageResult<Role> selectRoleList(PageParam pageParam, String roleName, String roleKey, Integer status);

    List<Role> selectRoleAll();

    Role selectRoleById(Long id);

    void createRole(RoleCreateRequest request);

    void updateRole(Long id, RoleCreateRequest request);

    void deleteRole(Long id);

    List<Long> selectMenuIdsByRoleId(Long roleId);

    void assignMenu(Long roleId, List<Long> menuIds);
}
