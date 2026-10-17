package org.example.tourismchengdu.dto.mall;

import org.example.tourismchengdu.entity.Order;
import org.example.tourismchengdu.entity.OrderItem;
import org.example.tourismchengdu.util.MallStatusUtils;

import java.util.List;

public class MallOrderDetailVO extends MallOrderSummaryVO {

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String logisticsNo;
    private String remark;
    private List<MallOrderItemVO> items;

    public static String statusText(String status) {
        return MallStatusUtils.toStatusText(status);
    }

    public static MallOrderDetailVO from(Order order, List<OrderItem> items) {
        MallOrderDetailVO vo = new MallOrderDetailVO();
        MallOrderSummaryVO summary = MallOrderSummaryVO.from(order);
        vo.setOrderNo(summary.getOrderNo());
        vo.setTotalAmount(summary.getTotalAmount());
        vo.setActualAmount(summary.getActualAmount());
        vo.setStatus(summary.getStatus());
        vo.setStatusText(summary.getStatusText());
        vo.setPayTime(summary.getPayTime());
        vo.setCreateTime(summary.getCreateTime());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setLogisticsNo(order.getLogisticsNo());
        vo.setRemark(order.getRemark());
        vo.setItems(items.stream().map(MallOrderItemVO::from).toList());
        return vo;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<MallOrderItemVO> getItems() {
        return items;
    }

    public void setItems(List<MallOrderItemVO> items) {
        this.items = items;
    }
}
