package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/menu/list")
    public Map<String, Object> listMenus() {
        List<Menu> menus = menuService.listMenus();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", menus);
        return result;
    }

    @GetMapping("/menu/get")
    public Map<String, Object> getMenu(@RequestParam Long id) {
        Menu menu = menuService.getMenu(id);
        Map<String, Object> result = new HashMap<>();
        if (menu == null) {
            result.put("code", 404);
            result.put("msg", "菜单不存在");
        } else {
            result.put("code", 0);
            result.put("msg", "success");
            result.put("data", menu);
        }
        return result;
    }

    @PostMapping("/menu/add")
    public Map<String, Object> addMenu(@RequestBody Menu menu) {
        Menu newMenu = menuService.addMenu(menu);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", newMenu);
        return result;
    }

    @PostMapping("/menu/update")
    public Map<String, Object> updateMenu(@RequestParam Long id, @RequestBody Menu menu) {
        Menu updated = menuService.updateMenu(id, menu);
        Map<String, Object> result = new HashMap<>();
        if (updated == null) {
            result.put("code", 404);
            result.put("msg", "菜单不存在");
        } else {
            result.put("code", 0);
            result.put("msg", "success");
            result.put("data", updated);
        }
        return result;
    }

    @PostMapping("/menu/delete")
    public Map<String, Object> deleteMenu(@RequestParam Long id) {
        boolean ok = menuService.deleteMenu(id);
        Map<String, Object> result = new HashMap<>();
        if (ok) {
            result.put("code", 0);
            result.put("msg", "success");
        } else {
            result.put("code", 404);
            result.put("msg", "菜单不存在");
        }
        return result;
    }
}