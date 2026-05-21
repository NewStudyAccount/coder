package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.Category;
import com.admin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> list(@RequestParam(required = false) String name,
                                       @RequestParam(required = false) Integer status) {
        List<Category> list = categoryService.getList(name, status);
        return Result.success(list);
    }

    @GetMapping("/tree")
    public Result<List<Category>> tree(@RequestParam(required = false) String name,
                                       @RequestParam(required = false) Integer status) {
        List<Category> tree = categoryService.getTree(name, status);
        return Result.success(tree);
    }

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Category category) {
        categoryService.add(category);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Category category) {
        categoryService.update(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        categoryService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        categoryService.deleteBatch(ids);
        return Result.success();
    }
}
