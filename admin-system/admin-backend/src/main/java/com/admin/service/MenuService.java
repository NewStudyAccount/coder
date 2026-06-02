package com.admin.service;

import com.admin.dto.MenuCreateRequest;
import com.admin.entity.Menu;

import java.util.List;

public interface MenuService {

    List<Menu> selectMenuTree(Long userId);

    List<Menu> selectMenuList(String menuName, Integer status);

    Menu selectMenuById(Long id);

    void createMenu(MenuCreateRequest request);

    void updateMenu(Long id, MenuCreateRequest request);

    void deleteMenu(Long id);
}
