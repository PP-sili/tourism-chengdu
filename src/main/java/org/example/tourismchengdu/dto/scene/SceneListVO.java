package org.example.tourismchengdu.dto.scene;

import org.example.tourismchengdu.entity.Scene;

import java.math.BigDecimal;

public class SceneListVO {

    private Long id;
    private String name;
    private String subtitle;
    private String coverImage;
    private BigDecimal ticketPrice;
    private BigDecimal studentPrice;
    private BigDecimal childPrice;
    private String address;
    private String city;
    private String openTime;
    private Integer suggestedDuration;
    private Boolean bookable;

    public static SceneListVO from(Scene scene) {
        SceneListVO vo = new SceneListVO();
        vo.setId(scene.getId());
        vo.setName(scene.getName());
        vo.setSubtitle(scene.getSubtitle());
        vo.setCoverImage(scene.getCoverImage());
        vo.setTicketPrice(scene.getTicketPrice());
        vo.setStudentPrice(scene.getStudentPrice());
        vo.setChildPrice(scene.getChildPrice());
        vo.setAddress(scene.getAddress());
        vo.setCity(scene.getCity());
        vo.setOpenTime(scene.getOpenTime());
        vo.setSuggestedDuration(scene.getSuggestedDuration());
        vo.setBookable(isBookable(scene.getTicketPrice()));
        return vo;
    }

    public static boolean isBookable(BigDecimal ticketPrice) {
        return ticketPrice != null && ticketPrice.compareTo(BigDecimal.ZERO) > 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public BigDecimal getStudentPrice() {
        return studentPrice;
    }

    public void setStudentPrice(BigDecimal studentPrice) {
        this.studentPrice = studentPrice;
    }

    public BigDecimal getChildPrice() {
        return childPrice;
    }

    public void setChildPrice(BigDecimal childPrice) {
        this.childPrice = childPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public Integer getSuggestedDuration() {
        return suggestedDuration;
    }

    public void setSuggestedDuration(Integer suggestedDuration) {
        this.suggestedDuration = suggestedDuration;
    }

    public Boolean getBookable() {
        return bookable;
    }

    public void setBookable(Boolean bookable) {
        this.bookable = bookable;
    }
}
