package org.example.tourismchengdu.dto.banner;

import org.example.tourismchengdu.entity.Banner;

public class BannerVO {

    private Long id;
    private Long sceneId;
    private String imageUrl;
    private String title;
    private String subTitle;

    public static BannerVO from(Banner banner) {
        BannerVO vo = new BannerVO();
        vo.setId(banner.getId());
        vo.setSceneId(banner.getSceneId());
        vo.setImageUrl(banner.getImageUrl());
        vo.setTitle(banner.getTitle());
        vo.setSubTitle(banner.getSubTitle());
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
