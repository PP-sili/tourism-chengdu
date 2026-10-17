package org.example.tourismchengdu.constant;

import java.util.List;

/**
 * 全景点统一的预约时段（2 小时一段）。
 */
public final class VisitSlotConstants {

    public static final String SLOT_08_10 = "08:00-10:00";
    public static final String SLOT_10_12 = "10:00-12:00";
    public static final String SLOT_12_14 = "12:00-14:00";
    public static final String SLOT_14_16 = "14:00-16:00";
    public static final String SLOT_16_18 = "16:00-18:00";

    private static final List<String> ALL_SLOTS = List.of(
            SLOT_08_10,
            SLOT_10_12,
            SLOT_12_14,
            SLOT_14_16,
            SLOT_16_18
    );

    private VisitSlotConstants() {
    }

    public static List<String> allSlots() {
        return ALL_SLOTS;
    }

    public static boolean isValid(String slotCode) {
        return slotCode != null && ALL_SLOTS.contains(slotCode);
    }

    public static int sortOrder(String slotCode) {
        int index = ALL_SLOTS.indexOf(slotCode);
        return index >= 0 ? index + 1 : 99;
    }
}
