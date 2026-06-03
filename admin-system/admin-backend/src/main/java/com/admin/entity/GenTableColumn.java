package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gen_table_column")
public class GenTableColumn {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tableId;
    private String columnName;
    private String columnComment;
    private String columnType;
    private String javaType;
    private String javaField;
    private Integer isPk;
    private Integer isIncrement;
    private Integer isRequired;
    private Integer isInsert;
    private Integer isEdit;
    private Integer isList;
    private Integer isQuery;
    private String queryType;
    private String htmlType;
    private String dictType;
    private Integer sort;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
}
