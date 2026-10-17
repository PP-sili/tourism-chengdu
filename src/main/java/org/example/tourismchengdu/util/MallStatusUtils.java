package org.example.tourismchengdu.util;

import org.example.tourismchengdu.constant.OrderConstants;

public final class MallStatusUtils {

    private MallStatusUtils() {
    }

    public static String toStatusText(String status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case OrderConstants.STATUS_PENDING_PAY -> "待付款";
            case OrderConstants.STATUS_PENDING_SHIP -> "待发货";
            case OrderConstants.STATUS_SHIPPED -> "已发货";
            case OrderConstants.STATUS_RECEIVED -> "已签收";
            case OrderConstants.STATUS_CANCELLED -> "已取消";
            default -> status;
        };
    }
}
