package org.example.tourismchengdu.dto.ticket;

import org.example.tourismchengdu.entity.TicketStockSlot;
import org.example.tourismchengdu.util.TimeSlotUtils;

public class SlotStockVO {

    private String slotCode;
    private String label;
    private Integer remaining;

    public static SlotStockVO from(TicketStockSlot slot) {
        SlotStockVO vo = new SlotStockVO();
        vo.setSlotCode(slot.getSlotCode());
        vo.setLabel(TimeSlotUtils.toText(slot.getSlotCode()));
        vo.setRemaining(slot.getRemaining());
        return vo;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }
}
