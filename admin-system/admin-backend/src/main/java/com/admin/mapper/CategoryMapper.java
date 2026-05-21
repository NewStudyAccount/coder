package com.admin.mapper;

import com.admin.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> selectList(@Param("name") String name, @Param("status") Integer status);

    Category selectById(Long id);

    int insert(Category category);

    int update(Category category);

    int deleteById(Long id);

    int deleteBatch(List<Long> ids);
}
