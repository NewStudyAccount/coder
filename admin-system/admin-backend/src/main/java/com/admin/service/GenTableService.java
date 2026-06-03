package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.GenTable;
import com.admin.entity.GenTableColumn;

import java.util.List;
import java.util.Map;

public interface GenTableService {

    PageResult<GenTable> selectGenTableList(PageParam pageParam, String tableName, String tableComment);

    GenTable selectGenTableById(Long id);

    void importTable(List<String> tableNames);

    void updateGenTable(GenTable genTable);

    void deleteGenTable(Long id);

    Map<String, String> previewCode(Long id);

    byte[] generateCode(Long id);

    byte[] batchGenerateCode(Long[] tableIds);
}
