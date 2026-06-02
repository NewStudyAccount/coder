package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.DictType;
import com.admin.mapper.DictTypeMapper;
import com.admin.service.DictTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DictTypeServiceImpl implements DictTypeService {

    private final DictTypeMapper dictTypeMapper;

    @Override
    public PageResult<DictType> selectDictTypeList(PageParam pageParam, String dictName, String dictType, Integer status) {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(dictName), DictType::getDictName, dictName)
               .like(StrUtil.isNotBlank(dictType), DictType::getDictType, dictType)
               .eq(status != null, DictType::getStatus, status);

        Page<DictType> page = dictTypeMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public DictType selectDictTypeById(Long id) {
        return dictTypeMapper.selectById(id);
    }

    @Override
    public void createDictType(DictType dictType) {
        dictType.setCreateBy("system");
        dictTypeMapper.insert(dictType);
    }

    @Override
    public void updateDictType(DictType dictType) {
        dictType.setUpdateBy("system");
        dictTypeMapper.updateById(dictType);
    }

    @Override
    public void deleteDictType(Long id) {
        dictTypeMapper.deleteById(id);
    }
}
