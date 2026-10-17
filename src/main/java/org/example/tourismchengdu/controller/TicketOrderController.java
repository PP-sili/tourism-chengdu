package org.example.tourismchengdu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.tourismchengdu.common.Result;
import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.ticket.AvailableDatesVO;
import org.example.tourismchengdu.dto.ticket.CreateTicketOrderRequest;
import org.example.tourismchengdu.dto.ticket.RefundTicketOrderRequest;
import org.example.tourismchengdu.dto.ticket.TicketOrderDetailVO;
import org.example.tourismchengdu.dto.ticket.TicketOrderSummaryVO;
import org.example.tourismchengdu.dto.ticket.VisitSlotVO;
import org.example.tourismchengdu.security.UserContext;
import org.example.tourismchengdu.service.TicketOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "门票订单", description = "预约购票、支付、退票")
public class TicketOrderController {

    private final TicketOrderService ticketOrderService;

    public TicketOrderController(TicketOrderService ticketOrderService) {
        this.ticketOrderService = ticketOrderService;
    }

    @GetMapping("/ticket/visit-slots")
    public Result<List<VisitSlotVO>> listVisitSlots() {
        return Result.success(ticketOrderService.listVisitSlots());
    }

    @GetMapping("/scenes/{id}/available-dates")
    public Result<AvailableDatesVO> getAvailableDates(@PathVariable Long id) {
        return Result.success(ticketOrderService.getAvailableDates(id));
    }

    @PostMapping("/ticket/orders")
    public Result<TicketOrderDetailVO> createOrder(@Valid @RequestBody CreateTicketOrderRequest request) {
        return Result.success(ticketOrderService.createOrder(UserContext.getUserId(), request));
    }

    @PostMapping("/ticket/orders/{orderNo}/pay")
    public Result<TicketOrderDetailVO> payOrder(@PathVariable String orderNo) {
        return Result.success(ticketOrderService.payOrder(UserContext.getUserId(), orderNo));
    }

    @GetMapping("/ticket/orders")
    public Result<PageResult<TicketOrderSummaryVO>> listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(ticketOrderService.listOrders(UserContext.getUserId(), status, page, size));
    }

    @GetMapping("/ticket/orders/{orderNo}")
    public Result<TicketOrderDetailVO> getOrderDetail(@PathVariable String orderNo) {
        return Result.success(ticketOrderService.getOrderDetail(UserContext.getUserId(), orderNo));
    }

    @PostMapping("/ticket/orders/{orderNo}/refund")
    public Result<TicketOrderDetailVO> refundOrder(@PathVariable String orderNo,
                                                   @RequestBody(required = false) RefundTicketOrderRequest request) {
        String reason = request == null ? null : request.getReason();
        return Result.success(ticketOrderService.refundOrder(UserContext.getUserId(), orderNo, reason));
    }
}
