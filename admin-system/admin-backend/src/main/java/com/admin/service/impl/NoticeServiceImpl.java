package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.Notice;
import com.admin.mapper.NoticeMapper;
import com.admin.service.NoticeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    public PageResult<Notice> selectNoticeList(PageParam pageParam, String noticeTitle, Integer noticeType) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(noticeTitle), Notice::getNoticeTitle, noticeTitle)
               .eq(noticeType != null, Notice::getNoticeType, noticeType)
               .orderByDesc(Notice::getCreateTime);

        Page<Notice> page = noticeMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public Notice selectNoticeById(Long id) {
        return noticeMapper.selectById(id);
    }

    @Override
    public void createNotice(Notice notice) {
        notice.setCreateBy("system");
        noticeMapper.insert(notice);
    }

    @Override
    public void updateNotice(Notice notice) {
        notice.setUpdateBy("system");
        noticeMapper.updateById(notice);
    }

    @Override
    public void deleteNotice(Long id) {
        noticeMapper.deleteById(id);
    }
}
