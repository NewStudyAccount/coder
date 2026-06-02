package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.DictType;

public interface DictTypeService {

    PageResult<DictType> selectDictTypeList(PageParam pageParam, String dictName, String dictType, Integer status);

    DictType selectDictTypeById(Long id);

    void createDictType(DictType dictType);

    void updateDictType(DictType dictType);

    void deleteDictType(Long id);
}
