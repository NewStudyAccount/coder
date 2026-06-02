package com.admin.controller;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.common.result.Result;
import com.admin.entity.Notice;
import com.admin.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:notice:list')")
    public Result<PageResult<Notice>> list(PageParam pageParam,
                                            @RequestParam(required = false) String noticeTitle,
                                            @RequestParam(required = false) Integer noticeType) {
        return Result.success(noticeService.selectNoticeList(pageParam, noticeTitle, noticeType));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:notice:query')")
    public Result<Notice> getInfo(@PathVariable Long id) {
        return Result.success(noticeService.selectNoticeById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:notice:add')")
    public Result<Void> add(@RequestBody Notice notice) {
        noticeService.createNotice(notice);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:notice:edit')")
    public Result<Void> edit(@RequestBody Notice notice) {
        noticeService.updateNotice(notice);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:notice:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success();
    }
}
