package org.example.tourismchengdu.dto.evaluation;

import org.example.tourismchengdu.entity.AppUser;
import org.example.tourismchengdu.entity.SceneEvaluation;

import java.time.LocalDateTime;

public class EvaluationVO {

    private Long id;
    private Long sceneId;
    private String sceneName;
    private String type;
    private String content;
    private Integer rating;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private LocalDateTime createTime;

    public static EvaluationVO from(SceneEvaluation evaluation, AppUser user) {
        EvaluationVO vo = new EvaluationVO();
        vo.setId(evaluation.getId());
        vo.setSceneId(evaluation.getSceneId());
        vo.setType(evaluation.getEvalType());
        vo.setContent(evaluation.getContent());
        vo.setRating(evaluation.getRating());
        vo.setUserId(evaluation.getUserId());
        vo.setCreateTime(evaluation.getCreateTime());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }
        return vo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
