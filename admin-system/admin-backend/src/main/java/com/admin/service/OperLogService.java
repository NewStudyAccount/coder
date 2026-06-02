package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.OperLog;

public interface OperLogService {

    PageResult<OperLog> selectOperLogList(PageParam pageParam, String title, Integer businessType, Integer status);

    void createOperLog(OperLog operLog);

    void cleanOperLog();
}
