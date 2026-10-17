package org.example.tourismchengdu.dto.scene;

import org.example.tourismchengdu.dto.ticket.SlotStockVO;
import org.example.tourismchengdu.entity.TicketStock;
import org.example.tourismchengdu.entity.TicketStockSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketStatItemVO {

    private LocalDate date;
    private Integer totalLimit;
    private List<SlotStockVO> slots;

    public static TicketStatItemVO from(TicketStock stock, List<TicketStockSlot> slotRows) {
        TicketStatItemVO vo = new TicketStatItemVO();
        vo.setDate(stock.getStockDate());
        vo.setTotalLimit(stock.getTotalLimit());
        vo.setSlots(slotRows.stream().map(SlotStockVO::from).toList());
        return vo;
    }

    public static List<TicketStatItemVO> buildList(List<TicketStock> stocks, List<TicketStockSlot> allSlots) {
        Map<LocalDate, List<TicketStockSlot>> grouped = allSlots.stream()
                .collect(Collectors.groupingBy(TicketStockSlot::getStockDate));
        return stocks.stream()
                .map(stock -> TicketStatItemVO.from(stock, grouped.getOrDefault(stock.getStockDate(), List.of())))
                .toList();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(Integer totalLimit) {
        this.totalLimit = totalLimit;
    }

    public List<SlotStockVO> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotStockVO> slots) {
        this.slots = slots;
    }
}
