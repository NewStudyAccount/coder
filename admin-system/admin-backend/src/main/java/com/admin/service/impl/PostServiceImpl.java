package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.Post;
import com.admin.mapper.PostMapper;
import com.admin.service.PostService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    @Override
    public PageResult<Post> selectPostList(PageParam pageParam, String postCode, String postName, Integer status) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(postCode), Post::getPostCode, postCode)
               .like(StrUtil.isNotBlank(postName), Post::getPostName, postName)
               .eq(status != null, Post::getStatus, status)
               .orderByAsc(Post::getSort);

        Page<Post> page = postMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public List<Post> selectPostAll() {
        return postMapper.selectList(new LambdaQueryWrapper<Post>().eq(Post::getStatus, 1).orderByAsc(Post::getSort));
    }

    @Override
    public Post selectPostById(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public void createPost(Post post) {
        post.setCreateBy("system");
        postMapper.insert(post);
    }

    @Override
    public void updatePost(Post post) {
        post.setUpdateBy("system");
        postMapper.updateById(post);
    }

    @Override
    public void deletePost(Long id) {
        postMapper.deleteById(id);
    }
}
