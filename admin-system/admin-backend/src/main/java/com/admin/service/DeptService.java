package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.Dept;

import java.util.List;

public interface DeptService {

    List<Dept> selectDeptList(String deptName, Integer status);

    Dept selectDeptById(Long id);

    void createDept(Dept dept);

    void updateDept(Dept dept);

    void deleteDept(Long id);

    List<Dept> selectDeptTree();
}
