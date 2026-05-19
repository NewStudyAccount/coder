package com.admin.mapper;

import com.admin.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {

    List<Menu> selectList(@Param("menuName") String menuName, @Param("status") Integer status);

    Menu selectById(Long id);

    int insert(Menu menu);

    int update(Menu menu);

    int deleteById(Long id);

    List<Menu> selectByParentId(Long parentId);

    List<Menu> selectByRoleIds(@Param("roleIds") List<Long> roleIds);
}
