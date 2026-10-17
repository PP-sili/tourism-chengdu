package org.example.tourismchengdu.dto.scene;

import org.example.tourismchengdu.entity.StatDaily;

import java.time.LocalDate;

public class VisitorStatItemVO {

    private LocalDate date;
    private Integer visitorCount;
    private Integer ticketSales;

    public static VisitorStatItemVO from(StatDaily stat) {
        VisitorStatItemVO vo = new VisitorStatItemVO();
        vo.setDate(stat.getStatDate());
        vo.setVisitorCount(stat.getVisitorCount());
        vo.setTicketSales(stat.getTicketSales());
        return vo;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getVisitorCount() {
        return visitorCount;
    }

    public void setVisitorCount(Integer visitorCount) {
        this.visitorCount = visitorCount;
    }

    public Integer getTicketSales() {
        return ticketSales;
    }

    public void setTicketSales(Integer ticketSales) {
        this.ticketSales = ticketSales;
    }
}
