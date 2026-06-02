package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BusinessException;
import com.admin.entity.Dept;
import com.admin.mapper.DeptMapper;
import com.admin.service.DeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;

    @Override
    public List<Dept> selectDeptList(String deptName, Integer status) {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(deptName), Dept::getDeptName, deptName)
               .eq(status != null, Dept::getStatus, status)
               .orderByAsc(Dept::getSort);
        return deptMapper.selectList(wrapper);
    }

    @Override
    public Dept selectDeptById(Long id) {
        return deptMapper.selectById(id);
    }

    @Override
    public void createDept(Dept dept) {
        dept.setCreateBy("system");
        deptMapper.insert(dept);
    }

    @Override
    public void updateDept(Dept dept) {
        dept.setUpdateBy("system");
        deptMapper.updateById(dept);
    }

    @Override
    public void deleteDept(Long id) {
        Long count = deptMapper.selectCount(new LambdaQueryWrapper<Dept>().eq(Dept::getParentId, id));
        if (count > 0) {
            throw new BusinessException("存在子部门，不允许删除");
        }
        deptMapper.deleteById(id);
    }

    @Override
    public List<Dept> selectDeptTree() {
        List<Dept> depts = deptMapper.selectList(new LambdaQueryWrapper<Dept>().eq(Dept::getStatus, 1).orderByAsc(Dept::getSort));
        return buildDeptTree(depts, 0L);
    }

    private List<Dept> buildDeptTree(List<Dept> depts, Long parentId) {
        return depts.stream()
                .filter(d -> parentId.equals(d.getParentId()))
                .peek(d -> d.setChildren(buildDeptTree(depts, d.getId())))
                .collect(Collectors.toList());
    }
}
