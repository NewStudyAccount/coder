package com.admin.service;

import com.admin.entity.Dept;
import com.admin.mapper.DeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeptService {

    @Autowired
    private DeptMapper deptMapper;

    public List<Dept> getList(String deptName, Integer status) {
        return deptMapper.selectList(deptName, status);
    }

    public List<Dept> getDeptTree(String deptName, Integer status) {
        List<Dept> allDepts = deptMapper.selectList(deptName, status);
        return buildTree(allDepts, 0L);
    }

    private List<Dept> buildTree(List<Dept> depts, Long parentId) {
        return depts.stream()
                .filter(dept -> parentId.equals(dept.getParentId()))
                .peek(dept -> dept.setChildren(buildTree(depts, dept.getId())))
                .collect(Collectors.toList());
    }

    public Dept getById(Long id) {
        return deptMapper.selectById(id);
    }

    @Transactional
    public void add(Dept dept) {
        dept.setStatus(dept.getStatus() != null ? dept.getStatus() : 1);
        deptMapper.insert(dept);
    }

    @Transactional
    public void update(Dept dept) {
        deptMapper.update(dept);
    }

    @Transactional
    public void deleteById(Long id) {
        List<Dept> children = deptMapper.selectByParentId(id);
        if (children != null && !children.isEmpty()) {
            throw new RuntimeException("存在子部门，不允许删除");
        }
        deptMapper.deleteById(id);
    }
}
