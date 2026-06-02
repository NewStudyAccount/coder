package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.LoginLog;
import com.admin.mapper.LoginLogMapper;
import com.admin.service.LoginLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    @Override
    public PageResult<LoginLog> selectLoginLogList(PageParam pageParam, String username, Integer status) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(username), LoginLog::getUsername, username)
               .eq(status != null, LoginLog::getStatus, status)
               .orderByDesc(LoginLog::getLoginTime);

        Page<LoginLog> page = loginLogMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public void createLoginLog(LoginLog loginLog) {
        loginLogMapper.insert(loginLog);
    }

    @Override
    public void cleanLoginLog() {
        loginLogMapper.delete(new LambdaQueryWrapper<>());
    }
}
