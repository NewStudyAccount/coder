package com.admin.mapper;

import com.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> selectList(@Param("username") String username, @Param("status") Integer status,
                          @Param("deptId") Long deptId);

    User selectById(Long id);

    User selectByUsername(String username);

    int insert(User user);

    int update(User user);

    int deleteById(Long id);

    int deleteBatch(List<Long> ids);

    List<Long> selectRoleIdsByUserId(Long userId);

    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    int deleteUserRolesByUserId(Long userId);
}
