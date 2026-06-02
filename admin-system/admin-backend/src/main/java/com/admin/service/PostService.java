package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.Post;

import java.util.List;

public interface PostService {

    PageResult<Post> selectPostList(PageParam pageParam, String postCode, String postName, Integer status);

    List<Post> selectPostAll();

    Post selectPostById(Long id);

    void createPost(Post post);

    void updatePost(Post post);

    void deletePost(Long id);
}
