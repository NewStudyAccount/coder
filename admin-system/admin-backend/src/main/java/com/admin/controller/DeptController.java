package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.Dept;
import com.admin.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @GetMapping("/list")
    public Result<List<Dept>> list(@RequestParam(required = false) String deptName,
                                   @RequestParam(required = false) Integer status) {
        List<Dept> list = deptService.getList(deptName, status);
        return Result.success(list);
    }

    @GetMapping("/tree")
    public Result<List<Dept>> tree(@RequestParam(required = false) String deptName,
                                   @RequestParam(required = false) Integer status) {
        List<Dept> tree = deptService.getDeptTree(deptName, status);
        return Result.success(tree);
    }

    @GetMapping("/{id}")
    public Result<Dept> getById(@PathVariable Long id) {
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Dept dept) {
        deptService.add(dept);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Dept dept) {
        deptService.update(dept);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        try {
            deptService.deleteById(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
