package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BusinessException;
import com.admin.dto.MenuCreateRequest;
import com.admin.entity.Menu;
import com.admin.mapper.MenuMapper;
import com.admin.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public List<Menu> selectMenuTree(Long userId) {
        List<Menu> menus;
        if (userId == 1L) {
            menus = menuMapper.selectMenuTreeAll();
        } else {
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return buildMenuTree(menus, 0L);
    }

    @Override
    public List<Menu> selectMenuList(String menuName, Integer status) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(menuName), Menu::getMenuName, menuName)
               .eq(status != null, Menu::getStatus, status)
               .orderByAsc(Menu::getSort);
        return menuMapper.selectList(wrapper);
    }

    @Override
    public Menu selectMenuById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public void createMenu(MenuCreateRequest request) {
        Menu menu = new Menu();
        menu.setMenuName(request.getMenuName());
        menu.setParentId(request.getParentId());
        menu.setSort(request.getSort() != null ? request.getSort() : 0);
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setQueryParam(request.getQueryParam());
        menu.setIsFrame(request.getIsFrame() != null ? request.getIsFrame() : 0);
        menu.setIsCache(request.getIsCache() != null ? request.getIsCache() : 0);
        menu.setMenuType(request.getMenuType());
        menu.setVisible(request.getVisible() != null ? request.getVisible() : 1);
        menu.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        menu.setPerms(request.getPerms());
        menu.setIcon(request.getIcon());
        menu.setRemark(request.getRemark());
        menu.setCreateBy("system");
        menuMapper.insert(menu);
    }

    @Override
    public void updateMenu(Long id, MenuCreateRequest request) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        if (request.getParentId().equals(id)) {
            throw new BusinessException("上级菜单不能选择自己");
        }

        menu.setMenuName(request.getMenuName());
        menu.setParentId(request.getParentId());
        menu.setSort(request.getSort());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setQueryParam(request.getQueryParam());
        menu.setIsFrame(request.getIsFrame());
        menu.setIsCache(request.getIsCache());
        menu.setMenuType(request.getMenuType());
        menu.setVisible(request.getVisible());
        menu.setStatus(request.getStatus());
        menu.setPerms(request.getPerms());
        menu.setIcon(request.getIcon());
        menu.setRemark(request.getRemark());
        menu.setUpdateBy("system");
        menuMapper.updateById(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        Long count = menuMapper.selectCount(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id));
        if (count > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }
        menuMapper.deleteById(id);
    }

    private List<Menu> buildMenuTree(List<Menu> menus, Long parentId) {
        return menus.stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .peek(m -> m.setChildren(buildMenuTree(menus, m.getId())))
                .collect(Collectors.toList());
    }
}
