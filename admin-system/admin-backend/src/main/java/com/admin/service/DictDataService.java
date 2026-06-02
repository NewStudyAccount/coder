package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.DictData;

import java.util.List;

public interface DictDataService {

    PageResult<DictData> selectDictDataList(PageParam pageParam, String dictType, String dictLabel, Integer status);

    List<DictData> selectDictDataByType(String dictType);

    void createDictData(DictData dictData);

    void updateDictData(DictData dictData);

    void deleteDictData(Long id);
}
