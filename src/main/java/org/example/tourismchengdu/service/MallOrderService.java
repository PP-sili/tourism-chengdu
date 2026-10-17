package org.example.tourismchengdu.service;

import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.mall.CreateMallOrderRequest;
import org.example.tourismchengdu.dto.mall.MallOrderDetailVO;
import org.example.tourismchengdu.dto.mall.MallOrderSummaryVO;

public interface MallOrderService {

    MallOrderDetailVO createOrder(Long userId, CreateMallOrderRequest request);

    MallOrderDetailVO payOrder(Long userId, String orderNo);

    PageResult<MallOrderSummaryVO> listOrders(Long userId, String status, int page, int size);

    MallOrderDetailVO getOrderDetail(Long userId, String orderNo);
}
