package org.example.tourismchengdu.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class OrderNoUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private OrderNoUtils() {
    }

    public static String generateTicketOrderNo() {
        return generateOrderNo("CD");
    }

    public static String generateMallOrderNo() {
        return generateOrderNo("SC");
    }

    private static String generateOrderNo(String prefix) {
        int suffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        return prefix + LocalDateTime.now().format(FORMATTER) + suffix;
    }

    public static LocalDateTime calcRefundDeadline(LocalDate visitDate) {
        return visitDate.minusDays(1).atTime(20, 0);
    }
}
