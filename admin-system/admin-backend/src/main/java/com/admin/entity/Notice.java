package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_notice")
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String noticeTitle;
    private Integer noticeType;
    private String noticeContent;
    private Integer status;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
}
