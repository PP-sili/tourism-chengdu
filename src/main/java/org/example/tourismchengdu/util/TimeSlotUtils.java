package org.example.tourismchengdu.util;

import org.example.tourismchengdu.constant.VisitSlotConstants;
import org.example.tourismchengdu.dto.ticket.VisitSlotVO;

import java.util.List;

public final class TimeSlotUtils {

    private TimeSlotUtils() {
    }

    public static List<VisitSlotVO> listAllSlots() {
        return VisitSlotConstants.allSlots().stream()
                .map(code -> new VisitSlotVO(code, toText(code), VisitSlotConstants.sortOrder(code)))
                .toList();
    }

    public static int sortWeight(String timeSlot) {
        return VisitSlotConstants.sortOrder(timeSlot);
    }

    public static String toText(String timeSlot) {
        if (timeSlot == null) {
            return "未知时段";
        }
        if (VisitSlotConstants.isValid(timeSlot)) {
            return timeSlot;
        }
        return switch (timeSlot) {
            case "morning" -> "08:00-10:00";
            case "afternoon" -> "12:00-14:00";
            case "full_day" -> "16:00-18:00";
            default -> timeSlot;
        };
    }

    public static void requireValid(String timeSlot) {
        if (!VisitSlotConstants.isValid(timeSlot)) {
            throw new IllegalArgumentException("游玩时段无效");
        }
    }
}
