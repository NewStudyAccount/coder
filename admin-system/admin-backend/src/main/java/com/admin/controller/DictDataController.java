package com.admin.controller;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.common.result.Result;
import com.admin.entity.DictData;
import com.admin.service.DictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/dict/data")
@RequiredArgsConstructor
public class DictDataController {

    private final DictDataService dictDataService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result<PageResult<DictData>> list(PageParam pageParam,
                                              @RequestParam(required = false) String dictType,
                                              @RequestParam(required = false) String dictLabel,
                                              @RequestParam(required = false) Integer status) {
        return Result.success(dictDataService.selectDictDataList(pageParam, dictType, dictLabel, status));
    }

    @GetMapping("/type/{dictType}")
    public Result<List<DictData>> getByType(@PathVariable String dictType) {
        return Result.success(dictDataService.selectDictDataByType(dictType));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:dict:add')")
    public Result<Void> add(@RequestBody DictData dictData) {
        dictDataService.createDictData(dictData);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result<Void> edit(@RequestBody DictData dictData) {
        dictDataService.updateDictData(dictData);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        dictDataService.deleteDictData(id);
        return Result.success();
    }
}
