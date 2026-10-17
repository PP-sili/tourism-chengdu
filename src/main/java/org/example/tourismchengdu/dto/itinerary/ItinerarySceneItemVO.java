package org.example.tourismchengdu.dto.itinerary;

public class ItinerarySceneItemVO {

    private Long sceneId;
    private String sceneName;
    private String orderNo;
    private String visitTimeSlot;
    private String visitTimeSlotText;
    private Integer suggestedDuration;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getVisitTimeSlot() {
        return visitTimeSlot;
    }

    public void setVisitTimeSlot(String visitTimeSlot) {
        this.visitTimeSlot = visitTimeSlot;
    }

    public String getVisitTimeSlotText() {
        return visitTimeSlotText;
    }

    public void setVisitTimeSlotText(String visitTimeSlotText) {
        this.visitTimeSlotText = visitTimeSlotText;
    }

    public Integer getSuggestedDuration() {
        return suggestedDuration;
    }

    public void setSuggestedDuration(Integer suggestedDuration) {
        this.suggestedDuration = suggestedDuration;
    }
}
