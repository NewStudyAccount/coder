package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.Notice;

public interface NoticeService {

    PageResult<Notice> selectNoticeList(PageParam pageParam, String noticeTitle, Integer noticeType);

    Notice selectNoticeById(Long id);

    void createNotice(Notice notice);

    void updateNotice(Notice notice);

    void deleteNotice(Long id);
}
