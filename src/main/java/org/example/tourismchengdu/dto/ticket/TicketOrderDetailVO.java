package org.example.tourismchengdu.dto.ticket;

import org.example.tourismchengdu.entity.Order;
import org.example.tourismchengdu.entity.OrderTicket;

import java.util.List;

public class TicketOrderDetailVO extends TicketOrderSummaryVO {

    private String contactName;
    private String contactPhone;
    private List<TicketItemVO> tickets;

    public static TicketOrderDetailVO from(Order order, List<OrderTicket> tickets) {
        TicketOrderDetailVO vo = new TicketOrderDetailVO();
        TicketOrderSummaryVO summary = TicketOrderSummaryVO.from(order);
        vo.setOrderNo(summary.getOrderNo());
        vo.setSceneId(summary.getSceneId());
        vo.setSceneName(summary.getSceneName());
        vo.setVisitDate(summary.getVisitDate());
        vo.setVisitTimeSlot(summary.getVisitTimeSlot());
        vo.setTicketQuantity(summary.getTicketQuantity());
        vo.setTotalAmount(summary.getTotalAmount());
        vo.setActualAmount(summary.getActualAmount());
        vo.setStatus(summary.getStatus());
        vo.setPayTime(summary.getPayTime());
        vo.setRefundDeadline(summary.getRefundDeadline());
        vo.setCreateTime(summary.getCreateTime());
        vo.setContactName(order.getContactName());
        vo.setContactPhone(order.getContactPhone());
        vo.setTickets(tickets.stream().map(TicketItemVO::from).toList());
        return vo;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public List<TicketItemVO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketItemVO> tickets) {
        this.tickets = tickets;
    }
}
