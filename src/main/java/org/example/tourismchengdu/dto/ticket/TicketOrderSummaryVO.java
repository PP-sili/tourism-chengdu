package org.example.tourismchengdu.dto.ticket;

import org.example.tourismchengdu.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TicketOrderSummaryVO {

    private String orderNo;
    private Long sceneId;
    private String sceneName;
    private LocalDate visitDate;
    private String visitTimeSlot;
    private Integer ticketQuantity;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private String status;
    private LocalDateTime payTime;
    private LocalDateTime refundDeadline;
    private LocalDateTime createTime;

    public static TicketOrderSummaryVO from(Order order) {
        TicketOrderSummaryVO vo = new TicketOrderSummaryVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setSceneId(order.getSceneId());
        vo.setSceneName(order.getSceneName());
        vo.setVisitDate(order.getVisitDate());
        vo.setVisitTimeSlot(order.getVisitTimeSlot());
        vo.setTicketQuantity(order.getTicketQuantity());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setActualAmount(order.getActualAmount());
        vo.setStatus(order.getStatus());
        vo.setPayTime(order.getPayTime());
        vo.setRefundDeadline(order.getRefundDeadline());
        vo.setCreateTime(order.getCreateTime());
        return vo;
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

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public String getVisitTimeSlot() {
        return visitTimeSlot;
    }

    public void setVisitTimeSlot(String visitTimeSlot) {
        this.visitTimeSlot = visitTimeSlot;
    }

    public Integer getTicketQuantity() {
        return ticketQuantity;
    }

    public void setTicketQuantity(Integer ticketQuantity) {
        this.ticketQuantity = ticketQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public LocalDateTime getRefundDeadline() {
        return refundDeadline;
    }

    public void setRefundDeadline(LocalDateTime refundDeadline) {
        this.refundDeadline = refundDeadline;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
