package com.admin.controller;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.common.result.Result;
import com.admin.entity.Job;
import com.admin.entity.JobLog;
import com.admin.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/devtools/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('devtools:job:list')")
    public Result<PageResult<Job>> list(PageParam pageParam,
                                         @RequestParam(required = false) String jobName,
                                         @RequestParam(required = false) String jobGroup,
                                         @RequestParam(required = false) Integer status) {
        return Result.success(jobService.selectJobList(pageParam, jobName, jobGroup, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('devtools:job:query')")
    public Result<Job> getInfo(@PathVariable Long id) {
        return Result.success(jobService.selectJobById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('devtools:job:add')")
    public Result<Void> add(@RequestBody Job job) {
        jobService.createJob(job);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('devtools:job:edit')")
    public Result<Void> edit(@RequestBody Job job) {
        jobService.updateJob(job);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('devtools:job:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        jobService.deleteJob(id);
        return Result.success();
    }

    @PutMapping("/changeStatus")
    @PreAuthorize("hasAuthority('devtools:job:edit')")
    public Result<Void> changeStatus(@RequestBody Job job) {
        jobService.changeStatus(job.getId(), job.getStatus());
        return Result.success();
    }

    @PutMapping("/run/{id}")
    @PreAuthorize("hasAuthority('devtools:job:edit')")
    public Result<Void> run(@PathVariable Long id) {
        jobService.runOnce(id);
        return Result.success();
    }

    @GetMapping("/log/list")
    @PreAuthorize("hasAuthority('devtools:job:list')")
    public Result<PageResult<JobLog>> logList(PageParam pageParam,
                                                @RequestParam(required = false) String jobName,
                                                @RequestParam(required = false) String jobGroup,
                                                @RequestParam(required = false) Integer status) {
        return Result.success(jobService.selectJobLogList(pageParam, jobName, jobGroup, status));
    }

    @DeleteMapping("/log/clean")
    @PreAuthorize("hasAuthority('devtools:job:remove')")
    public Result<Void> cleanLog() {
        jobService.cleanJobLog();
        return Result.success();
    }
}
