package com.asset.management.mapper;

import com.asset.management.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User findById(Long id);

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM sys_user ORDER BY create_time DESC")
    List<User> findAll();

    /**
     * 新增用户
     */
    @Insert("INSERT INTO sys_user (username, password, real_name, email, phone, department, role, status) " +
            "VALUES (#{username}, #{password}, #{realName}, #{email}, #{phone}, #{department}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新用户
     */
    @Update("UPDATE sys_user SET real_name = #{realName}, email = #{email}, phone = #{phone}, " +
            "department = #{department}, role = #{role}, status = #{status} WHERE id = #{id}")
    int update(User user);

    /**
     * 删除用户
     */
    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int delete(Long id);

    /**
     * 修改密码
     */
    @Update("UPDATE sys_user SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
