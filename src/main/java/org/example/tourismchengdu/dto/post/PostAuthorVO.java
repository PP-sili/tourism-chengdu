package org.example.tourismchengdu.dto.post;

import org.example.tourismchengdu.entity.AppUser;

public class PostAuthorVO {

    private Long userId;
    private String nickname;
    private String avatar;

    public static PostAuthorVO from(AppUser user) {
        PostAuthorVO vo = new PostAuthorVO();
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        return vo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
