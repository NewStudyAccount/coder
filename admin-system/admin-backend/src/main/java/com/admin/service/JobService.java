package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.Job;
import com.admin.entity.JobLog;

public interface JobService {

    PageResult<Job> selectJobList(PageParam pageParam, String jobName, String jobGroup, Integer status);

    Job selectJobById(Long id);

    void createJob(Job job);

    void updateJob(Job job);

    void deleteJob(Long id);

    void changeStatus(Long id, Integer status);

    void runOnce(Long id);

    PageResult<JobLog> selectJobLogList(PageParam pageParam, String jobName, String jobGroup, Integer status);

    void cleanJobLog();
}
