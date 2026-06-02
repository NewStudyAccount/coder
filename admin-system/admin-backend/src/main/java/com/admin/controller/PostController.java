package com.admin.controller;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.common.result.Result;
import com.admin.entity.Post;
import com.admin.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:post:list')")
    public Result<PageResult<Post>> list(PageParam pageParam,
                                          @RequestParam(required = false) String postCode,
                                          @RequestParam(required = false) String postName,
                                          @RequestParam(required = false) Integer status) {
        return Result.success(postService.selectPostList(pageParam, postCode, postName, status));
    }

    @GetMapping("/all")
    public Result<List<Post>> all() {
        return Result.success(postService.selectPostAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:post:query')")
    public Result<Post> getInfo(@PathVariable Long id) {
        return Result.success(postService.selectPostById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:post:add')")
    public Result<Void> add(@RequestBody Post post) {
        postService.createPost(post);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:post:edit')")
    public Result<Void> edit(@RequestBody Post post) {
        postService.updatePost(post);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:post:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.success();
    }
}
