package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gen_table")
public class GenTable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String tableName;
    private String tableComment;
    private String className;
    private String packageName;
    private String moduleName;
    private String businessName;
    private String functionName;
    private String functionNameQuery;
    private String genType;
    private String genPath;
    private String tplCategory;
    private String tplWebType;
    private String parentMenuId;
    private String parentMenuName;
    private Long menuId;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;

    @TableField(exist = false)
    private java.util.List<GenTableColumn> columns;
}
