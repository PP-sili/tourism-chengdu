package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.common.BusinessException;
import org.example.tourismchengdu.common.ResultCode;
import org.example.tourismchengdu.constant.OrderConstants;
import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.mall.CreateMallOrderRequest;
import org.example.tourismchengdu.dto.mall.MallOrderDetailVO;
import org.example.tourismchengdu.dto.mall.MallOrderItemRequest;
import org.example.tourismchengdu.dto.mall.MallOrderSummaryVO;
import org.example.tourismchengdu.entity.Cart;
import org.example.tourismchengdu.entity.Order;
import org.example.tourismchengdu.entity.OrderItem;
import org.example.tourismchengdu.entity.Product;
import org.example.tourismchengdu.mapper.CartMapper;
import org.example.tourismchengdu.mapper.OrderItemMapper;
import org.example.tourismchengdu.mapper.OrderMapper;
import org.example.tourismchengdu.mapper.ProductMapper;
import org.example.tourismchengdu.service.MallOrderService;
import org.example.tourismchengdu.util.OrderNoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MallOrderServiceImpl implements MallOrderService {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProductMapper productMapper;
    private final CartMapper cartMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public MallOrderServiceImpl(ProductMapper productMapper,
                                CartMapper cartMapper,
                                OrderMapper orderMapper,
                                OrderItemMapper orderItemMapper) {
        this.productMapper = productMapper;
        this.cartMapper = cartMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallOrderDetailVO createOrder(Long userId, CreateMallOrderRequest request) {
        CheckoutLineResult checkout = resolveCheckoutLines(userId, request);
        validateStock(checkout.lines());

        BigDecimal totalAmount = checkout.lines().stream()
                .map(line -> line.product().getPrice().multiply(BigDecimal.valueOf(line.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setOrderNo(OrderNoUtils.generateMallOrderNo());
        order.setUserId(userId);
        order.setOrderType(OrderConstants.ORDER_TYPE_MALL);
        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount);
        order.setStatus(OrderConstants.STATUS_PENDING_PAY);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setRemark(request.getRemark());
        orderMapper.insertMallOrder(order);

        for (CheckoutLine line : checkout.lines()) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(line.product().getId());
            item.setProductName(line.product().getName());
            item.setProductPrice(line.product().getPrice());
            item.setQuantity(line.quantity());
            orderItemMapper.insert(item);
        }

        if (checkout.fromCart() && !checkout.cartIds().isEmpty()) {
            cartMapper.deleteByIds(userId, checkout.cartIds());
        }

        return buildDetail(orderMapper.findByOrderNo(order.getOrderNo()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallOrderDetailVO payOrder(Long userId, String orderNo) {
        Order order = requireUserMallOrder(userId, orderNo);
        if (!OrderConstants.STATUS_PENDING_PAY.equals(order.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "订单状态不允许支付");
        }

        List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
        for (OrderItem item : items) {
            int affected = productMapper.deductStock(item.getProductId(), item.getQuantity());
            if (affected <= 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "商品库存不足：" + item.getProductName());
            }
            productMapper.incrementSalesCount(item.getProductId(), item.getQuantity());
        }

        int updated = orderMapper.markMallPaid(order.getId());
        if (updated <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "支付失败，请重试");
        }

        return buildDetail(orderMapper.findByOrderNo(orderNo));
    }

    @Override
    public PageResult<MallOrderSummaryVO> listOrders(Long userId, String status, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int offset = (safePage - 1) * safeSize;

        List<Order> orders;
        long total;
        if (StringUtils.hasText(status)) {
            orders = orderMapper.findMallOrdersByUserIdAndStatus(userId, status, offset, safeSize);
            total = orderMapper.countMallOrdersByUserIdAndStatus(userId, status);
        } else {
            orders = orderMapper.findMallOrdersByUserId(userId, offset, safeSize);
            total = orderMapper.countMallOrdersByUserId(userId);
        }

        List<MallOrderSummaryVO> list = orders.stream().map(MallOrderSummaryVO::from).toList();
        return new PageResult<>(list, total, safePage, safeSize);
    }

    @Override
    public MallOrderDetailVO getOrderDetail(Long userId, String orderNo) {
        Order order = requireUserMallOrder(userId, orderNo);
        return buildDetail(order);
    }

    private MallOrderDetailVO buildDetail(Order order) {
        List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
        return MallOrderDetailVO.from(order, items);
    }

    private Order requireUserMallOrder(Long userId, String orderNo) {
        Order order = orderMapper.findMallOrderByOrderNoAndUserId(orderNo, userId);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        return order;
    }

    private CheckoutLineResult resolveCheckoutLines(Long userId, CreateMallOrderRequest request) {
        if (Boolean.TRUE.equals(request.getFromCart())) {
            List<Cart> selected = cartMapper.findSelectedByUserId(userId);
            if (selected.isEmpty()) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "请先选择购物车商品");
            }

            List<CheckoutLine> lines = new ArrayList<>();
            List<Long> cartIds = new ArrayList<>();
            for (Cart cart : selected) {
                Product product = requireAvailableProduct(cart.getProductId());
                lines.add(new CheckoutLine(product, cart.getQuantity()));
                cartIds.add(cart.getId());
            }
            return new CheckoutLineResult(true, cartIds, lines);
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "下单商品不能为空");
        }

        Map<Long, Integer> quantityMap = new HashMap<>();
        for (MallOrderItemRequest item : request.getItems()) {
            quantityMap.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        List<CheckoutLine> lines = quantityMap.entrySet().stream()
                .map(entry -> new CheckoutLine(requireAvailableProduct(entry.getKey()), entry.getValue()))
                .toList();
        return new CheckoutLineResult(false, List.of(), lines);
    }

    private Product requireAvailableProduct(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在或已下架");
        }
        return product;
    }

    private void validateStock(List<CheckoutLine> lines) {
        for (CheckoutLine line : lines) {
            if (line.product().getStock() == null || line.product().getStock() < line.quantity()) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "商品库存不足：" + line.product().getName());
            }
        }
    }

    private record CheckoutLine(Product product, int quantity) {
    }

    private record CheckoutLineResult(boolean fromCart, List<Long> cartIds, List<CheckoutLine> lines) {
    }
}
