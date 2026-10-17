package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.common.BusinessException;
import org.example.tourismchengdu.common.ResultCode;
import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.post.CreatePostRequest;
import org.example.tourismchengdu.dto.post.PostDetailVO;
import org.example.tourismchengdu.dto.post.PostLikeResultVO;
import org.example.tourismchengdu.dto.post.PostSummaryVO;
import org.example.tourismchengdu.dto.post.UserLikeStatsVO;
import org.example.tourismchengdu.entity.AppUser;
import org.example.tourismchengdu.entity.Post;
import org.example.tourismchengdu.entity.PostLike;
import org.example.tourismchengdu.entity.Scene;
import org.example.tourismchengdu.mapper.AppUserMapper;
import org.example.tourismchengdu.mapper.PostLikeMapper;
import org.example.tourismchengdu.mapper.PostMapper;
import org.example.tourismchengdu.mapper.SceneMapper;
import org.example.tourismchengdu.service.PostService;
import org.example.tourismchengdu.util.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {

    private static final int MAX_PAGE_SIZE = 50;
    private static final String SORT_HOT = "hot";

    private final PostMapper postMapper;
    private final PostLikeMapper postLikeMapper;
    private final AppUserMapper appUserMapper;
    private final SceneMapper sceneMapper;

    public PostServiceImpl(PostMapper postMapper,
                           PostLikeMapper postLikeMapper,
                           AppUserMapper appUserMapper,
                           SceneMapper sceneMapper) {
        this.postMapper = postMapper;
        this.postLikeMapper = postLikeMapper;
        this.appUserMapper = appUserMapper;
        this.sceneMapper = sceneMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostDetailVO createPost(Long userId, CreatePostRequest request) {
        requireUser(userId);
        validateImages(request.getImages());

        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setLocation(request.getLocation());
        post.setStatus(1);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            post.setImages(JsonUtils.toJson(request.getImages()));
        }

        if (request.getSceneId() != null) {
            Scene scene = sceneMapper.findById(request.getSceneId());
            if (scene == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "关联景点不存在");
            }
            post.setSceneId(scene.getId());
            post.setSceneName(scene.getName());
        }

        postMapper.insert(post);
        return toDetailVO(postMapper.findVisibleById(post.getId()), userId);
    }

    @Override
    public PageResult<PostSummaryVO> listPosts(Long userId, String sort, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int offset = (safePage - 1) * safeSize;

        List<Post> posts;
        long total;
        if (SORT_HOT.equalsIgnoreCase(sort)) {
            posts = postMapper.findByHot(offset, safeSize);
        } else {
            posts = postMapper.findByLatest(offset, safeSize);
        }
        total = postMapper.countVisible();

        return new PageResult<>(toSummaryList(posts, userId), total, safePage, safeSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostDetailVO getPostDetail(Long userId, Long postId) {
        Post post = requireVisiblePost(postId);
        postMapper.incrementViewCount(postId);
        post.setViewCount((post.getViewCount() == null ? 0 : post.getViewCount()) + 1);
        return toDetailVO(post, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostLikeResultVO toggleLike(Long userId, Long postId) {
        requireUser(userId);
        Post post = requireVisiblePost(postId);

        boolean liked;
        if (postLikeMapper.exists(postId, userId) > 0) {
            postLikeMapper.delete(postId, userId);
            postMapper.decrementLikeCount(postId);
            liked = false;
        } else {
            PostLike postLike = new PostLike();
            postLike.setPostId(postId);
            postLike.setUserId(userId);
            postLikeMapper.insert(postLike);
            postMapper.incrementLikeCount(postId);
            liked = true;
        }

        Post updated = postMapper.findVisibleById(postId);
        return new PostLikeResultVO(liked, updated.getLikeCount());
    }

    @Override
    public PageResult<PostSummaryVO> listMyPosts(Long userId, int page, int size) {
        requireUser(userId);
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int offset = (safePage - 1) * safeSize;

        List<Post> posts = postMapper.findByUserId(userId, offset, safeSize);
        long total = postMapper.countByUserId(userId);
        return new PageResult<>(toSummaryList(posts, userId), total, safePage, safeSize);
    }

    @Override
    public UserLikeStatsVO getUserLikeStats(Long userId) {
        requireUser(userId);
        long totalLikes = postMapper.sumLikeCountByUserId(userId);
        return new UserLikeStatsVO(totalLikes);
    }

    private List<PostSummaryVO> toSummaryList(List<Post> posts, Long currentUserId) {
        Map<Long, AppUser> authorCache = new HashMap<>();
        return posts.stream()
                .map(post -> {
                    AppUser author = authorCache.computeIfAbsent(post.getUserId(), appUserMapper::findById);
                    boolean liked = postLikeMapper.exists(post.getId(), currentUserId) > 0;
                    return PostSummaryVO.from(post, author, liked);
                })
                .toList();
    }

    private PostDetailVO toDetailVO(Post post, Long currentUserId) {
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "帖子不存在");
        }
        AppUser author = appUserMapper.findById(post.getUserId());
        boolean liked = postLikeMapper.exists(post.getId(), currentUserId) > 0;
        PostDetailVO vo = new PostDetailVO();
        PostSummaryVO summary = PostSummaryVO.from(post, author, liked);
        vo.setId(summary.getId());
        vo.setTitle(summary.getTitle());
        vo.setContent(summary.getContent());
        vo.setImages(summary.getImages());
        vo.setSceneId(summary.getSceneId());
        vo.setSceneName(summary.getSceneName());
        vo.setLocation(summary.getLocation());
        vo.setViewCount(summary.getViewCount());
        vo.setLikeCount(summary.getLikeCount());
        vo.setHotScore(summary.getHotScore());
        vo.setLiked(summary.getLiked());
        vo.setCreateTime(summary.getCreateTime());
        vo.setAuthor(summary.getAuthor());
        return vo;
    }

    private Post requireVisiblePost(Long postId) {
        Post post = postMapper.findVisibleById(postId);
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "帖子不存在");
        }
        return post;
    }

    private AppUser requireUser(Long userId) {
        AppUser user = appUserMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    private void validateImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        if (images.size() > 9) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "最多上传9张图片");
        }
        for (String image : images) {
            if (!StringUtils.hasText(image) || !image.startsWith("/uploads/")) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "图片URL格式不正确");
            }
        }
    }
}
