package com.admin.controller;

import com.admin.common.result.Result;
import com.admin.entity.Dept;
import com.admin.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result<List<Dept>> list(@RequestParam(required = false) String deptName,
                                    @RequestParam(required = false) Integer status) {
        return Result.success(deptService.selectDeptList(deptName, status));
    }

    @GetMapping("/tree")
    public Result<List<Dept>> tree() {
        return Result.success(deptService.selectDeptTree());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Result<Dept> getInfo(@PathVariable Long id) {
        return Result.success(deptService.selectDeptById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:add')")
    public Result<Void> add(@RequestBody Dept dept) {
        deptService.createDept(dept);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:dept:edit')")
    public Result<Void> edit(@RequestBody Dept dept) {
        deptService.updateDept(dept);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        deptService.deleteDept(id);
        return Result.success();
    }
}
