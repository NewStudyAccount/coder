package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_job_log")
public class JobLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long jobId;
    private String jobName;
    private String jobGroup;
    private String invokeTarget;
    private String jobMessage;
    private Integer status;
    private String exceptionInfo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
