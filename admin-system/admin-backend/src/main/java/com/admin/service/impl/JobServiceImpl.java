package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BusinessException;
import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.Job;
import com.admin.entity.JobLog;
import com.admin.mapper.JobLogMapper;
import com.admin.mapper.JobMapper;
import com.admin.service.JobService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobMapper jobMapper;
    private final JobLogMapper jobLogMapper;

    @Override
    public PageResult<Job> selectJobList(PageParam pageParam, String jobName, String jobGroup, Integer status) {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(jobName), Job::getJobName, jobName)
               .eq(StrUtil.isNotBlank(jobGroup), Job::getJobGroup, jobGroup)
               .eq(status != null, Job::getStatus, status)
               .orderByAsc(Job::getId);

        Page<Job> page = jobMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public Job selectJobById(Long id) {
        return jobMapper.selectById(id);
    }

    @Override
    @Transactional
    public void createJob(Job job) {
        job.setCreateBy("system");
        jobMapper.insert(job);
    }

    @Override
    @Transactional
    public void updateJob(Job job) {
        job.setUpdateBy("system");
        jobMapper.updateById(job);
    }

    @Override
    @Transactional
    public void deleteJob(Long id) {
        jobMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void changeStatus(Long id, Integer status) {
        Job job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }
        job.setStatus(status);
        job.setUpdateBy("system");
        jobMapper.updateById(job);
    }

    @Override
    public void runOnce(Long id) {
        Job job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }
        log.info("手动执行任务: {} - {}", job.getJobName(), job.getInvokeTarget());
        saveJobLog(job, 1, "手动执行成功");
    }

    @Override
    public PageResult<JobLog> selectJobLogList(PageParam pageParam, String jobName, String jobGroup, Integer status) {
        LambdaQueryWrapper<JobLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(jobName), JobLog::getJobName, jobName)
               .eq(StrUtil.isNotBlank(jobGroup), JobLog::getJobGroup, jobGroup)
               .eq(status != null, JobLog::getStatus, status)
               .orderByDesc(JobLog::getStartTime);

        Page<JobLog> page = jobLogMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public void cleanJobLog() {
        jobLogMapper.delete(new LambdaQueryWrapper<>());
    }

    private void saveJobLog(Job job, int status, String message) {
        JobLog jobLog = new JobLog();
        jobLog.setJobId(job.getId());
        jobLog.setJobName(job.getJobName());
        jobLog.setJobGroup(job.getJobGroup());
        jobLog.setInvokeTarget(job.getInvokeTarget());
        jobLog.setStatus(status);
        jobLog.setJobMessage(message);
        jobLog.setStartTime(LocalDateTime.now());
        jobLog.setEndTime(LocalDateTime.now());
        jobLogMapper.insert(jobLog);
    }
}
