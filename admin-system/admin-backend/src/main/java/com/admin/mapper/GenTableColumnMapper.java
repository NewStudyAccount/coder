package com.admin.mapper;

import com.admin.entity.GenTableColumn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GenTableColumnMapper extends BaseMapper<GenTableColumn> {

    List<GenTableColumn> selectByTableId(@Param("tableId") Long tableId);
}
