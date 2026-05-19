package com.admin.mapper;

import com.admin.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeptMapper {

    List<Dept> selectList(@Param("deptName") String deptName, @Param("status") Integer status);

    Dept selectById(Long id);

    int insert(Dept dept);

    int update(Dept dept);

    int deleteById(Long id);

    List<Dept> selectByParentId(Long parentId);
}
