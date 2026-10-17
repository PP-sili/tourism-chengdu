package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.common.BusinessException;
import org.example.tourismchengdu.common.ResultCode;
import org.example.tourismchengdu.constant.OrderConstants;
import org.example.tourismchengdu.constant.VisitSlotConstants;
import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.scene.SceneListVO;
import org.example.tourismchengdu.dto.ticket.AvailableDatesVO;
import org.example.tourismchengdu.dto.ticket.CreateTicketOrderRequest;
import org.example.tourismchengdu.dto.ticket.TicketItemRequest;
import org.example.tourismchengdu.dto.ticket.TicketOrderDetailVO;
import org.example.tourismchengdu.dto.ticket.TicketOrderSummaryVO;
import org.example.tourismchengdu.dto.ticket.TicketQrPayload;
import org.example.tourismchengdu.dto.ticket.VisitSlotVO;
import org.example.tourismchengdu.entity.Order;
import org.example.tourismchengdu.entity.OrderTicket;
import org.example.tourismchengdu.entity.RefundRecord;
import org.example.tourismchengdu.entity.Scene;
import org.example.tourismchengdu.entity.TicketStock;
import org.example.tourismchengdu.entity.TicketStockSlot;
import org.example.tourismchengdu.mapper.OrderMapper;
import org.example.tourismchengdu.mapper.OrderTicketMapper;
import org.example.tourismchengdu.mapper.RefundRecordMapper;
import org.example.tourismchengdu.mapper.SceneMapper;
import org.example.tourismchengdu.mapper.TicketStockMapper;
import org.example.tourismchengdu.mapper.TicketStockSlotMapper;
import org.example.tourismchengdu.service.TicketOrderService;
import org.example.tourismchengdu.util.IdCardUtils;
import org.example.tourismchengdu.util.JsonUtils;
import org.example.tourismchengdu.util.OrderNoUtils;
import org.example.tourismchengdu.util.TimeSlotUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TicketOrderServiceImpl implements TicketOrderService {

    private static final int AVAILABLE_DAYS = 7;
    private static final int MAX_PAGE_SIZE = 50;

    private final SceneMapper sceneMapper;
    private final TicketStockMapper ticketStockMapper;
    private final TicketStockSlotMapper ticketStockSlotMapper;
    private final OrderMapper orderMapper;
    private final OrderTicketMapper orderTicketMapper;
    private final RefundRecordMapper refundRecordMapper;

    public TicketOrderServiceImpl(SceneMapper sceneMapper,
                                  TicketStockMapper ticketStockMapper,
                                  TicketStockSlotMapper ticketStockSlotMapper,
                                  OrderMapper orderMapper,
                                  OrderTicketMapper orderTicketMapper,
                                  RefundRecordMapper refundRecordMapper) {
        this.sceneMapper = sceneMapper;
        this.ticketStockMapper = ticketStockMapper;
        this.ticketStockSlotMapper = ticketStockSlotMapper;
        this.orderMapper = orderMapper;
        this.orderTicketMapper = orderTicketMapper;
        this.refundRecordMapper = refundRecordMapper;
    }

    @Override
    public List<VisitSlotVO> listVisitSlots() {
        return TimeSlotUtils.listAllSlots();
    }

    @Override
    public AvailableDatesVO getAvailableDates(Long sceneId) {
        Scene scene = requireBookableScene(sceneId);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(AVAILABLE_DAYS - 1L);

        List<TicketStock> stocks = ticketStockMapper.findBySceneIdAndDateRange(sceneId, startDate, endDate);
        List<TicketStockSlot> slots = ticketStockSlotMapper.findBySceneIdAndDateRange(sceneId, startDate, endDate);

        AvailableDatesVO vo = new AvailableDatesVO();
        vo.setSceneId(scene.getId());
        vo.setDates(AvailableDatesVO.DateStockVO.buildList(stocks, slots));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketOrderDetailVO createOrder(Long userId, CreateTicketOrderRequest request) {
        Scene scene = requireBookableScene(request.getSceneId());
        validateTimeSlot(request.getTimeSlot());
        validateTickets(request.getTickets());

        int quantity = request.getTickets().size();
        requireStockDate(scene.getId(), request.getVisitDate());
        ensureStockEnough(scene.getId(), request.getVisitDate(), request.getTimeSlot(), quantity);

        BigDecimal totalAmount = calculateTotalAmount(scene, request.getTickets());

        Order order = new Order();
        order.setOrderNo(OrderNoUtils.generateTicketOrderNo());
        order.setUserId(userId);
        order.setOrderType(OrderConstants.ORDER_TYPE_TICKET);
        order.setSceneId(scene.getId());
        order.setSceneName(scene.getName());
        order.setVisitDate(request.getVisitDate());
        order.setVisitTimeSlot(request.getTimeSlot());
        order.setTicketQuantity(quantity);
        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount);
        order.setStatus(OrderConstants.STATUS_PENDING_PAY);
        order.setRefundDeadline(OrderNoUtils.calcRefundDeadline(request.getVisitDate()));
        order.setContactName(request.getContactName());
        order.setContactPhone(request.getContactPhone());
        orderMapper.insert(order);

        for (TicketItemRequest item : request.getTickets()) {
            OrderTicket ticket = new OrderTicket();
            ticket.setOrderId(order.getId());
            ticket.setTicketType(item.getTicketType());
            ticket.setVisitorName(item.getVisitorName());
            ticket.setIdCardNo(item.getIdCardNo());
            ticket.setUnitPrice(resolveUnitPrice(scene, item.getTicketType()));
            ticket.setStatus(OrderConstants.STATUS_PENDING_PAY);
            orderTicketMapper.insert(ticket);
        }

        return buildDetail(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketOrderDetailVO payOrder(Long userId, String orderNo) {
        Order order = requireUserTicketOrder(userId, orderNo);
        if (!OrderConstants.STATUS_PENDING_PAY.equals(order.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "订单状态不允许支付");
        }

        int affected = ticketStockSlotMapper.deductStock(
                order.getSceneId(), order.getVisitDate(), order.getVisitTimeSlot(), order.getTicketQuantity());
        if (affected <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "库存不足，支付失败");
        }

        int updated = orderMapper.markPaid(order.getId(), OrderConstants.STATUS_PENDING_USE);
        if (updated <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "支付失败，请重试");
        }

        List<OrderTicket> tickets = orderTicketMapper.findByOrderId(order.getId());
        for (OrderTicket ticket : tickets) {
            String qrCode = buildQrCodeJson(order, ticket);
            orderTicketMapper.updateQrCodeAndStatus(ticket.getId(), qrCode, OrderConstants.STATUS_PENDING_USE);
        }

        Order paidOrder = orderMapper.findByOrderNo(orderNo);
        return buildDetail(paidOrder);
    }

    @Override
    public PageResult<TicketOrderSummaryVO> listOrders(Long userId, String status, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int offset = (safePage - 1) * safeSize;

        List<Order> orders;
        long total;
        if (StringUtils.hasText(status)) {
            orders = orderMapper.findTicketOrdersByUserIdAndStatus(userId, status, offset, safeSize);
            total = orderMapper.countTicketOrdersByUserIdAndStatus(userId, status);
        } else {
            orders = orderMapper.findTicketOrdersByUserId(userId, offset, safeSize);
            total = orderMapper.countTicketOrdersByUserId(userId);
        }

        List<TicketOrderSummaryVO> list = orders.stream().map(TicketOrderSummaryVO::from).toList();
        return new PageResult<>(list, total, safePage, safeSize);
    }

    @Override
    public TicketOrderDetailVO getOrderDetail(Long userId, String orderNo) {
        Order order = requireUserTicketOrder(userId, orderNo);
        return buildDetail(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TicketOrderDetailVO refundOrder(Long userId, String orderNo, String reason) {
        Order order = requireUserTicketOrder(userId, orderNo);
        if (!OrderConstants.STATUS_PENDING_USE.equals(order.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "当前订单状态不可退票");
        }

        LocalDateTime now = LocalDateTime.now();
        if (order.getRefundDeadline() != null && now.isAfter(order.getRefundDeadline())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已超过退票截止时间");
        }

        ticketStockSlotMapper.restoreStock(
                order.getSceneId(), order.getVisitDate(), order.getVisitTimeSlot(), order.getTicketQuantity());

        int updated = orderMapper.markRefunded(order.getId());
        if (updated <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "退票失败，请重试");
        }
        orderTicketMapper.markRefundedByOrderId(order.getId());

        RefundRecord record = new RefundRecord();
        record.setOrderId(order.getId());
        record.setUserId(userId);
        record.setSceneId(order.getSceneId());
        record.setSceneName(order.getSceneName());
        record.setVisitDate(order.getVisitDate());
        record.setTicketQuantity(order.getTicketQuantity());
        record.setRefundAmount(order.getActualAmount());
        record.setReason(reason);
        refundRecordMapper.insert(record);

        Order refundedOrder = orderMapper.findByOrderNo(orderNo);
        return buildDetail(refundedOrder);
    }

    private TicketOrderDetailVO buildDetail(Order order) {
        List<OrderTicket> tickets = orderTicketMapper.findByOrderId(order.getId());
        return TicketOrderDetailVO.from(order, tickets);
    }

    private Scene requireBookableScene(Long sceneId) {
        Scene scene = sceneMapper.findById(sceneId);
        if (scene == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "景点不存在");
        }
        if (!SceneListVO.isBookable(scene.getTicketPrice())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该景点免费开放，无需预约");
        }
        return scene;
    }

    private void requireStockDate(Long sceneId, LocalDate visitDate) {
        if (ticketStockMapper.findBySceneIdAndDate(sceneId, visitDate) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "所选日期不可预约");
        }
        if (ticketStockSlotMapper.countBySceneIdAndDate(sceneId, visitDate) <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "所选日期不可预约");
        }
    }

    private void validateTimeSlot(String timeSlot) {
        if (!VisitSlotConstants.isValid(timeSlot)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "游玩时段无效");
        }
    }

    private void validateTickets(List<TicketItemRequest> tickets) {
        Set<String> idCards = new HashSet<>();
        for (TicketItemRequest ticket : tickets) {
            if (!IdCardUtils.isValid(ticket.getIdCardNo())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "身份证号格式不正确：" + ticket.getVisitorName());
            }
            if (!idCards.add(ticket.getIdCardNo())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "同一订单内身份证号不能重复");
            }
        }
    }

    private void ensureStockEnough(Long sceneId, LocalDate visitDate, String timeSlot, int quantity) {
        TicketStockSlot slot = ticketStockSlotMapper.findBySceneIdAndDateAndSlot(sceneId, visitDate, timeSlot);
        if (slot == null || slot.getRemaining() == null || slot.getRemaining() < quantity) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "所选时段余票不足");
        }
    }

    private BigDecimal calculateTotalAmount(Scene scene, List<TicketItemRequest> tickets) {
        BigDecimal total = BigDecimal.ZERO;
        for (TicketItemRequest item : tickets) {
            total = total.add(resolveUnitPrice(scene, item.getTicketType()));
        }
        return total;
    }

    private BigDecimal resolveUnitPrice(Scene scene, String ticketType) {
        return switch (ticketType) {
            case OrderConstants.TICKET_ADULT -> scene.getTicketPrice();
            case OrderConstants.TICKET_STUDENT -> defaultPrice(scene.getStudentPrice(), scene.getTicketPrice());
            case OrderConstants.TICKET_CHILD -> defaultPrice(scene.getChildPrice(), scene.getTicketPrice());
            default -> throw new BusinessException(ResultCode.BAD_REQUEST, "票种无效");
        };
    }

    private BigDecimal defaultPrice(BigDecimal price, BigDecimal fallback) {
        return price != null ? price : fallback;
    }

    private Order requireUserTicketOrder(Long userId, String orderNo) {
        Order order = orderMapper.findTicketOrderByOrderNoAndUserId(orderNo, userId);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        return order;
    }

    private String buildQrCodeJson(Order order, OrderTicket ticket) {
        TicketQrPayload payload = new TicketQrPayload();
        payload.setTicketId(ticket.getId());
        payload.setOrderNo(order.getOrderNo());
        payload.setSceneId(order.getSceneId());
        payload.setSceneName(order.getSceneName());
        payload.setVisitDate(order.getVisitDate().toString());
        payload.setTimeSlot(order.getVisitTimeSlot());
        payload.setTicketType(ticket.getTicketType());
        payload.setVisitorName(ticket.getVisitorName());
        payload.setIdCardNo(ticket.getIdCardNo());
        return JsonUtils.toJson(payload.toMap());
    }
}
