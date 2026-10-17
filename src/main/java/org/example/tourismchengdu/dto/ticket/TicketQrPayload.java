package org.example.tourismchengdu.dto.ticket;

import java.util.LinkedHashMap;
import java.util.Map;

public class TicketQrPayload {

    private Long ticketId;
    private String orderNo;
    private Long sceneId;
    private String sceneName;
    private String visitDate;
    private String timeSlot;
    private String ticketType;
    private String visitorName;
    private String idCardNo;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ticketId", ticketId);
        map.put("orderNo", orderNo);
        map.put("sceneId", sceneId);
        map.put("sceneName", sceneName);
        map.put("visitDate", visitDate);
        map.put("timeSlot", timeSlot);
        map.put("ticketType", ticketType);
        map.put("visitorName", visitorName);
        map.put("idCardNo", idCardNo);
        return map;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }
}
