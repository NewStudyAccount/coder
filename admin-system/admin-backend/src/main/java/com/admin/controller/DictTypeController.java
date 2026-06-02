package com.admin.controller;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.common.result.Result;
import com.admin.entity.DictType;
import com.admin.service.DictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
public class DictTypeController {

    private final DictTypeService dictTypeService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result<PageResult<DictType>> list(PageParam pageParam,
                                              @RequestParam(required = false) String dictName,
                                              @RequestParam(required = false) String dictType,
                                              @RequestParam(required = false) Integer status) {
        return Result.success(dictTypeService.selectDictTypeList(pageParam, dictName, dictType, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:query')")
    public Result<DictType> getInfo(@PathVariable Long id) {
        return Result.success(dictTypeService.selectDictTypeById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:dict:add')")
    public Result<Void> add(@RequestBody DictType dictType) {
        dictTypeService.createDictType(dictType);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result<Void> edit(@RequestBody DictType dictType) {
        dictTypeService.updateDictType(dictType);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        dictTypeService.deleteDictType(id);
        return Result.success();
    }
}
