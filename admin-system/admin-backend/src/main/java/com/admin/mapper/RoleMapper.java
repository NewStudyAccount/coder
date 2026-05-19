package com.admin.mapper;

import com.admin.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {

    List<Role> selectList(@Param("roleName") String roleName, @Param("roleKey") String roleKey,
                          @Param("status") Integer status);

    Role selectById(Long id);

    Role selectByRoleKey(String roleKey);

    int insert(Role role);

    int update(Role role);

    int deleteById(Long id);

    int deleteBatch(List<Long> ids);

    List<Long> selectMenuIdsByRoleId(Long roleId);

    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    int deleteRoleMenusByRoleId(Long roleId);
}
