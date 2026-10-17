package org.example.tourismchengdu.dto.ticket;

import org.example.tourismchengdu.entity.TicketStock;
import org.example.tourismchengdu.entity.TicketStockSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AvailableDatesVO {

    private Long sceneId;
    private List<DateStockVO> dates;

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public List<DateStockVO> getDates() {
        return dates;
    }

    public void setDates(List<DateStockVO> dates) {
        this.dates = dates;
    }

    public static class DateStockVO {
        private LocalDate date;
        private Integer totalLimit;
        private List<SlotStockVO> slots;

        public static DateStockVO from(TicketStock stock, List<TicketStockSlot> slotRows) {
            DateStockVO vo = new DateStockVO();
            vo.setDate(stock.getStockDate());
            vo.setTotalLimit(stock.getTotalLimit());
            vo.setSlots(slotRows.stream().map(SlotStockVO::from).toList());
            return vo;
        }

        public static List<DateStockVO> buildList(List<TicketStock> stocks, List<TicketStockSlot> allSlots) {
            Map<LocalDate, List<TicketStockSlot>> grouped = allSlots.stream()
                    .collect(Collectors.groupingBy(TicketStockSlot::getStockDate));
            return stocks.stream()
                    .map(stock -> DateStockVO.from(stock, grouped.getOrDefault(stock.getStockDate(), List.of())))
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
}
