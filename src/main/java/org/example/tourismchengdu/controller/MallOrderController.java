package org.example.tourismchengdu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.tourismchengdu.common.Result;
import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.mall.CreateMallOrderRequest;
import org.example.tourismchengdu.dto.mall.MallOrderDetailVO;
import org.example.tourismchengdu.dto.mall.MallOrderSummaryVO;
import org.example.tourismchengdu.security.UserContext;
import org.example.tourismchengdu.service.MallOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mall/orders")
@Tag(name = "商城-订单", description = "合并下单、支付、物流")
public class MallOrderController {

    private final MallOrderService mallOrderService;

    public MallOrderController(MallOrderService mallOrderService) {
        this.mallOrderService = mallOrderService;
    }

    @PostMapping
    public Result<MallOrderDetailVO> createOrder(@Valid @RequestBody CreateMallOrderRequest request) {
        return Result.success(mallOrderService.createOrder(UserContext.getUserId(), request));
    }

    @PostMapping("/{orderNo}/pay")
    public Result<MallOrderDetailVO> payOrder(@PathVariable String orderNo) {
        return Result.success(mallOrderService.payOrder(UserContext.getUserId(), orderNo));
    }

    @GetMapping
    public Result<PageResult<MallOrderSummaryVO>> listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(mallOrderService.listOrders(UserContext.getUserId(), status, page, size));
    }

    @GetMapping("/{orderNo}")
    public Result<MallOrderDetailVO> getOrderDetail(@PathVariable String orderNo) {
        return Result.success(mallOrderService.getOrderDetail(UserContext.getUserId(), orderNo));
    }
}
