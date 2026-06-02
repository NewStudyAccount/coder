package com.admin.mapper;

import com.admin.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<String> selectPermsByUserId(@Param("userId") Long userId);

    List<Menu> selectMenuTreeByUserId(@Param("userId") Long userId);

    List<Menu> selectMenuTreeAll();
}
