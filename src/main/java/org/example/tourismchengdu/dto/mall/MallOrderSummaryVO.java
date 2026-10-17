package org.example.tourismchengdu.dto.mall;

import org.example.tourismchengdu.entity.Order;
import org.example.tourismchengdu.util.MallStatusUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MallOrderSummaryVO {

    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private String status;
    private String statusText;
    private LocalDateTime payTime;
    private LocalDateTime createTime;

    public static MallOrderSummaryVO from(Order order) {
        MallOrderSummaryVO vo = new MallOrderSummaryVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setActualAmount(order.getActualAmount());
        vo.setStatus(order.getStatus());
        vo.setStatusText(MallStatusUtils.toStatusText(order.getStatus()));
        vo.setPayTime(order.getPayTime());
        vo.setCreateTime(order.getCreateTime());
        return vo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
