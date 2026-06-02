package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.DictData;
import com.admin.mapper.DictDataMapper;
import com.admin.service.DictDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictDataServiceImpl implements DictDataService {

    private final DictDataMapper dictDataMapper;

    @Override
    public PageResult<DictData> selectDictDataList(PageParam pageParam, String dictType, String dictLabel, Integer status) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(dictType), DictData::getDictType, dictType)
               .like(StrUtil.isNotBlank(dictLabel), DictData::getDictLabel, dictLabel)
               .eq(status != null, DictData::getStatus, status)
               .orderByAsc(DictData::getDictSort);

        Page<DictData> page = dictDataMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public List<DictData> selectDictDataByType(String dictType) {
        return dictDataMapper.selectList(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDictType, dictType)
                .eq(DictData::getStatus, 1)
                .orderByAsc(DictData::getDictSort));
    }

    @Override
    public void createDictData(DictData dictData) {
        dictData.setCreateBy("system");
        dictDataMapper.insert(dictData);
    }

    @Override
    public void updateDictData(DictData dictData) {
        dictData.setUpdateBy("system");
        dictDataMapper.updateById(dictData);
    }

    @Override
    public void deleteDictData(Long id) {
        dictDataMapper.deleteById(id);
    }
}
