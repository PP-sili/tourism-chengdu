package org.example.tourismchengdu.dto.ticket;

import jakarta.validation.constraints.Size;

public class RefundTicketOrderRequest {

    @Size(max = 255, message = "退票原因过长")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
