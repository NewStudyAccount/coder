package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.OperLog;
import com.admin.mapper.OperLogMapper;
import com.admin.service.OperLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperLogServiceImpl implements OperLogService {

    private final OperLogMapper operLogMapper;

    @Override
    public PageResult<OperLog> selectOperLogList(PageParam pageParam, String title, Integer businessType, Integer status) {
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(title), OperLog::getTitle, title)
               .eq(businessType != null, OperLog::getBusinessType, businessType)
               .eq(status != null, OperLog::getStatus, status)
               .orderByDesc(OperLog::getOperTime);

        Page<OperLog> page = operLogMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public void createOperLog(OperLog operLog) {
        operLogMapper.insert(operLog);
    }

    @Override
    public void cleanOperLog() {
        operLogMapper.delete(new LambdaQueryWrapper<>());
    }
}
