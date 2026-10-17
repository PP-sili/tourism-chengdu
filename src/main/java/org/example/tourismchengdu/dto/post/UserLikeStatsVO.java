package org.example.tourismchengdu.dto.post;

public class UserLikeStatsVO {

    private Long totalLikes;

    public UserLikeStatsVO() {
    }

    public UserLikeStatsVO(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }
}
