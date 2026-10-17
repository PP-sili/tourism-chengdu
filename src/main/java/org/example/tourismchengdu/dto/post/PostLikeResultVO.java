package org.example.tourismchengdu.dto.post;

public class PostLikeResultVO {

    private Boolean liked;
    private Integer likeCount;

    public PostLikeResultVO() {
    }

    public PostLikeResultVO(Boolean liked, Integer likeCount) {
        this.liked = liked;
        this.likeCount = likeCount;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
}
