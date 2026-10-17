package org.example.tourismchengdu.dto.user;

import java.util.HashMap;
import java.util.Map;

public class OrderStatusCountVO {

    private Map<String, Long> statusCounts = new HashMap<>();
    private long total;

    public Map<String, Long> getStatusCounts() {
        return statusCounts;
    }

    public void setStatusCounts(Map<String, Long> statusCounts) {
        this.statusCounts = statusCounts;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
