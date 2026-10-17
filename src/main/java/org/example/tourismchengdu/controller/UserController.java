package org.example.tourismchengdu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.tourismchengdu.common.Result;
import org.example.tourismchengdu.dto.user.UpdateAvatarRequest;
import org.example.tourismchengdu.dto.user.UpdateProfileRequest;
import org.example.tourismchengdu.dto.user.UserOverviewVO;
import org.example.tourismchengdu.dto.user.UserProfileVO;
import org.example.tourismchengdu.dto.post.UserLikeStatsVO;
import org.example.tourismchengdu.security.UserContext;
import org.example.tourismchengdu.service.PostService;
import org.example.tourismchengdu.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户", description = "个人资料与我的页汇总")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/profile")
    public Result<UserProfileVO> getProfile() {
        return Result.success(userService.getProfile(UserContext.getUserId()));
    }

    @PutMapping("/profile")
    public Result<UserProfileVO> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return Result.success(userService.updateProfile(UserContext.getUserId(), request));
    }

    @PutMapping("/avatar")
    public Result<UserProfileVO> updateAvatar(@Valid @RequestBody UpdateAvatarRequest request) {
        return Result.success(userService.updateAvatar(UserContext.getUserId(), request));
    }

    @GetMapping("/like-stats")
    public Result<UserLikeStatsVO> getLikeStats() {
        return Result.success(postService.getUserLikeStats(UserContext.getUserId()));
    }

    @GetMapping("/overview")
    public Result<UserOverviewVO> getOverview() {
        return Result.success(userService.getOverview(UserContext.getUserId()));
    }
}
