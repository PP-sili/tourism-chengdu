package org.example.tourismchengdu.dto.ticket;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class CreateTicketOrderRequest {

    @NotNull(message = "景点ID不能为空")
    private Long sceneId;

    @NotNull(message = "游玩日期不能为空")
    @FutureOrPresent(message = "游玩日期不能早于今天")
    private LocalDate visitDate;

    @NotBlank(message = "游玩时段不能为空")
    @Pattern(regexp = "^(08:00-10:00|10:00-12:00|12:00-14:00|14:00-16:00|16:00-18:00)$", message = "游玩时段无效")
    private String timeSlot;

    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名过长")
    private String contactName;

    @NotBlank(message = "联系人手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "联系人手机号格式不正确")
    private String contactPhone;

    @NotEmpty(message = "购票信息不能为空")
    @Valid
    private List<TicketItemRequest> tickets;

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public List<TicketItemRequest> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketItemRequest> tickets) {
        this.tickets = tickets;
    }
}
