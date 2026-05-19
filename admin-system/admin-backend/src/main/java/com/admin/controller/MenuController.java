package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.Menu;
import com.admin.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public Result<List<Menu>> list(@RequestParam(required = false) String menuName,
                                   @RequestParam(required = false) Integer status) {
        List<Menu> list = menuService.getList(menuName, status);
        return Result.success(list);
    }

    @GetMapping("/tree")
    public Result<List<Menu>> tree(@RequestParam(required = false) String menuName,
                                   @RequestParam(required = false) Integer status) {
        List<Menu> tree = menuService.getMenuTree(menuName, status);
        return Result.success(tree);
    }

    @GetMapping("/{id}")
    public Result<Menu> getById(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        return Result.success(menu);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Menu menu) {
        menuService.add(menu);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Menu menu) {
        menuService.update(menu);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        try {
            menuService.deleteById(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
