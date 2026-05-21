package com.admin.service;

import com.admin.entity.Category;
import com.admin.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> getList(String name, Integer status) {
        return categoryMapper.selectList(name, status);
    }

    public List<Category> getTree(String name, Integer status) {
        List<Category> list = categoryMapper.selectList(name, status);
        return buildTree(list);
    }

    private List<Category> buildTree(List<Category> list) {
        Map<Long, List<Category>> groupMap = list.stream()
                .collect(Collectors.groupingBy(Category::getParentId));
        list.forEach(item -> item.setChildren(groupMap.get(item.getId())));
        return list.stream()
                .filter(item -> item.getParentId() == 0)
                .collect(Collectors.toList());
    }

    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Transactional
    public void add(Category category) {
        category.setStatus(category.getStatus() != null ? category.getStatus() : 1);
        category.setSort(category.getSort() != null ? category.getSort() : 0);
        categoryMapper.insert(category);
    }

    @Transactional
    public void update(Category category) {
        categoryMapper.update(category);
    }

    @Transactional
    public void deleteById(Long id) {
        categoryMapper.deleteById(id);
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        categoryMapper.deleteBatch(ids);
    }
}
