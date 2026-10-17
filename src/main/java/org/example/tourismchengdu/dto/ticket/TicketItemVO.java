package org.example.tourismchengdu.dto.ticket;

import org.example.tourismchengdu.entity.OrderTicket;
import org.example.tourismchengdu.util.IdCardUtils;

import java.math.BigDecimal;

public class TicketItemVO {

    private Long ticketId;
    private String ticketType;
    private String visitorName;
    private String idCardNo;
    private BigDecimal unitPrice;
    private String qrCode;
    private String status;

    public static TicketItemVO from(OrderTicket ticket) {
        TicketItemVO vo = new TicketItemVO();
        vo.setTicketId(ticket.getId());
        vo.setTicketType(ticket.getTicketType());
        vo.setVisitorName(ticket.getVisitorName());
        vo.setIdCardNo(ticket.getIdCardNo());
        vo.setUnitPrice(ticket.getUnitPrice());
        vo.setQrCode(ticket.getQrCode());
        vo.setStatus(ticket.getStatus());
        return vo;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdCardNoMasked() {
        return IdCardUtils.mask(idCardNo);
    }
}
