package com.admin.service;

import com.admin.entity.Menu;
import com.admin.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;

    public List<Menu> getList(String menuName, Integer status) {
        return menuMapper.selectList(menuName, status);
    }

    public List<Menu> getMenuTree(String menuName, Integer status) {
        List<Menu> allMenus = menuMapper.selectList(menuName, status);
        return buildTree(allMenus, 0L);
    }

    private List<Menu> buildTree(List<Menu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .peek(menu -> menu.setChildren(buildTree(menus, menu.getId())))
                .collect(Collectors.toList());
    }

    public Menu getById(Long id) {
        return menuMapper.selectById(id);
    }

    @Transactional
    public void add(Menu menu) {
        menu.setStatus(menu.getStatus() != null ? menu.getStatus() : 1);
        menuMapper.insert(menu);
    }

    @Transactional
    public void update(Menu menu) {
        menuMapper.update(menu);
    }

    @Transactional
    public void deleteById(Long id) {
        List<Menu> children = menuMapper.selectByParentId(id);
        if (children != null && !children.isEmpty()) {
            throw new RuntimeException("存在子菜单，不允许删除");
        }
        menuMapper.deleteById(id);
    }

    public List<Menu> getMenusByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Menu> menus = menuMapper.selectByRoleIds(roleIds);
        return buildTree(menus, 0L);
    }
}
