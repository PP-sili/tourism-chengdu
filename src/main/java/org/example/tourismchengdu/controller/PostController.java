package org.example.tourismchengdu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.tourismchengdu.common.Result;
import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.post.CreatePostRequest;
import org.example.tourismchengdu.dto.post.PostDetailVO;
import org.example.tourismchengdu.dto.post.PostLikeResultVO;
import org.example.tourismchengdu.dto.post.PostSummaryVO;
import org.example.tourismchengdu.security.UserContext;
import org.example.tourismchengdu.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "社区", description = "发帖、浏览、点赞")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public Result<PostDetailVO> createPost(@Valid @RequestBody CreatePostRequest request) {
        return Result.success(postService.createPost(UserContext.getUserId(), request));
    }

    @GetMapping
    public Result<PageResult<PostSummaryVO>> listPosts(
            @RequestParam(defaultValue = "time") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(postService.listPosts(UserContext.getUserId(), sort, page, size));
    }

    @GetMapping("/mine")
    public Result<PageResult<PostSummaryVO>> listMyPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(postService.listMyPosts(UserContext.getUserId(), page, size));
    }

    @GetMapping("/{id}")
    public Result<PostDetailVO> getPostDetail(@PathVariable Long id) {
        return Result.success(postService.getPostDetail(UserContext.getUserId(), id));
    }

    @PostMapping("/{id}/like")
    public Result<PostLikeResultVO> toggleLike(@PathVariable Long id) {
        return Result.success(postService.toggleLike(UserContext.getUserId(), id));
    }
}
