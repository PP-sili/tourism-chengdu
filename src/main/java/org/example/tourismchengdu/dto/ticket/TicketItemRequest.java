package org.example.tourismchengdu.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TicketItemRequest {

    @NotBlank(message = "票种不能为空")
    @Pattern(regexp = "^(adult|student|child)$", message = "票种无效")
    private String ticketType;

    @NotBlank(message = "游客姓名不能为空")
    @Size(max = 50, message = "游客姓名过长")
    private String visitorName;

    @NotBlank(message = "身份证号不能为空")
    private String idCardNo;

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }
}
