package org.example.tourismchengdu.constant;

public final class OrderConstants {

    public static final int ORDER_TYPE_TICKET = 1;
    public static final int ORDER_TYPE_MALL = 2;

    public static final String STATUS_PENDING_PAY = "pending_pay";
    public static final String STATUS_PENDING_USE = "pending_use";
    public static final String STATUS_USED = "used";
    public static final String STATUS_REFUNDED = "refunded";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_PENDING_SHIP = "pending_ship";
    public static final String STATUS_SHIPPED = "shipped";
    public static final String STATUS_RECEIVED = "received";

    public static final String TICKET_ADULT = "adult";
    public static final String TICKET_STUDENT = "student";
    public static final String TICKET_CHILD = "child";

    private OrderConstants() {
    }
}
