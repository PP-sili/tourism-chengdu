package org.example.tourismchengdu.dto.scene;

import org.example.tourismchengdu.entity.Scene;
import org.example.tourismchengdu.util.JsonUtils;

import java.math.BigDecimal;
import java.util.List;

public class SceneDetailVO {

    private Long id;
    private String name;
    private String subtitle;
    private String coverImage;
    private List<String> images;
    private String historyText;
    private String address;
    private String city;
    private BigDecimal ticketPrice;
    private BigDecimal studentPrice;
    private BigDecimal childPrice;
    private String openTime;
    private Integer suggestedDuration;
    private String contactPhone;
    private Integer dailyLimit;
    private Boolean bookable;

    public static SceneDetailVO from(Scene scene) {
        SceneDetailVO vo = new SceneDetailVO();
        vo.setId(scene.getId());
        vo.setName(scene.getName());
        vo.setSubtitle(scene.getSubtitle());
        vo.setCoverImage(scene.getCoverImage());
        vo.setImages(JsonUtils.parseStringList(scene.getImages()));
        vo.setHistoryText(scene.getHistoryText());
        vo.setAddress(scene.getAddress());
        vo.setCity(scene.getCity());
        vo.setTicketPrice(scene.getTicketPrice());
        vo.setStudentPrice(scene.getStudentPrice());
        vo.setChildPrice(scene.getChildPrice());
        vo.setOpenTime(scene.getOpenTime());
        vo.setSuggestedDuration(scene.getSuggestedDuration());
        vo.setContactPhone(scene.getContactPhone());
        vo.setDailyLimit(scene.getDailyLimit());
        vo.setBookable(SceneListVO.isBookable(scene.getTicketPrice()));
        return vo;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getHistoryText() {
        return historyText;
    }

    public void setHistoryText(String historyText) {
        this.historyText = historyText;
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

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Integer getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(Integer dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public Boolean getBookable() {
        return bookable;
    }

    public void setBookable(Boolean bookable) {
        this.bookable = bookable;
    }
}
