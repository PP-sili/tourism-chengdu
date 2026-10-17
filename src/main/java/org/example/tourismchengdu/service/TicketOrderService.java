package org.example.tourismchengdu.service;

import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.ticket.AvailableDatesVO;
import org.example.tourismchengdu.dto.ticket.CreateTicketOrderRequest;
import org.example.tourismchengdu.dto.ticket.TicketOrderDetailVO;
import org.example.tourismchengdu.dto.ticket.TicketOrderSummaryVO;

import org.example.tourismchengdu.dto.ticket.VisitSlotVO;

import java.util.List;

public interface TicketOrderService {

    List<VisitSlotVO> listVisitSlots();

    AvailableDatesVO getAvailableDates(Long sceneId);

    TicketOrderDetailVO createOrder(Long userId, CreateTicketOrderRequest request);

    TicketOrderDetailVO payOrder(Long userId, String orderNo);

    PageResult<TicketOrderSummaryVO> listOrders(Long userId, String status, int page, int size);

    TicketOrderDetailVO getOrderDetail(Long userId, String orderNo);

    TicketOrderDetailVO refundOrder(Long userId, String orderNo, String reason);
}
