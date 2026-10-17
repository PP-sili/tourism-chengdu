package org.example.tourismchengdu.dto.post;

import org.example.tourismchengdu.entity.AppUser;
import org.example.tourismchengdu.entity.Post;
import org.example.tourismchengdu.util.JsonUtils;

import java.time.LocalDateTime;
import java.util.List;

public class PostSummaryVO {

    private Long id;
    private String title;
    private String content;
    private List<String> images;
    private Long sceneId;
    private String sceneName;
    private String location;
    private Integer viewCount;
    private Integer likeCount;
    private Integer hotScore;
    private Boolean liked;
    private LocalDateTime createTime;
    private PostAuthorVO author;

    public static PostSummaryVO from(Post post, AppUser author, boolean liked) {
        PostSummaryVO vo = new PostSummaryVO();
        vo.setId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setImages(JsonUtils.parseStringList(post.getImages()));
        vo.setSceneId(post.getSceneId());
        vo.setSceneName(post.getSceneName());
        vo.setLocation(post.getLocation());
        vo.setViewCount(post.getViewCount());
        vo.setLikeCount(post.getLikeCount());
        vo.setHotScore(calcHotScore(post));
        vo.setLiked(liked);
        vo.setCreateTime(post.getCreateTime());
        if (author != null) {
            vo.setAuthor(PostAuthorVO.from(author));
        }
        return vo;
    }

    public static int calcHotScore(Post post) {
        int likes = post.getLikeCount() == null ? 0 : post.getLikeCount();
        int views = post.getViewCount() == null ? 0 : post.getViewCount();
        return likes * 2 + views;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getHotScore() {
        return hotScore;
    }

    public void setHotScore(Integer hotScore) {
        this.hotScore = hotScore;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public PostAuthorVO getAuthor() {
        return author;
    }

    public void setAuthor(PostAuthorVO author) {
        this.author = author;
    }
}
