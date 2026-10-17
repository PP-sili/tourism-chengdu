package org.example.tourismchengdu.service;

import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.post.CreatePostRequest;
import org.example.tourismchengdu.dto.post.PostDetailVO;
import org.example.tourismchengdu.dto.post.PostLikeResultVO;
import org.example.tourismchengdu.dto.post.PostSummaryVO;
import org.example.tourismchengdu.dto.post.UserLikeStatsVO;

public interface PostService {

    PostDetailVO createPost(Long userId, CreatePostRequest request);

    PageResult<PostSummaryVO> listPosts(Long userId, String sort, int page, int size);

    PostDetailVO getPostDetail(Long userId, Long postId);

    PostLikeResultVO toggleLike(Long userId, Long postId);

    PageResult<PostSummaryVO> listMyPosts(Long userId, int page, int size);

    UserLikeStatsVO getUserLikeStats(Long userId);
}
