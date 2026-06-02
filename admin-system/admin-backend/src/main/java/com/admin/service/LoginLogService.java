package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.LoginLog;

public interface LoginLogService {

    PageResult<LoginLog> selectLoginLogList(PageParam pageParam, String username, Integer status);

    void createLoginLog(LoginLog loginLog);

    void cleanLoginLog();
}
