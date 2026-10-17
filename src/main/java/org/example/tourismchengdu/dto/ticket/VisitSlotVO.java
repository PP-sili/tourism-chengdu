package org.example.tourismchengdu.dto.ticket;

public class VisitSlotVO {

    private String slotCode;
    private String label;
    private int sortOrder;

    public VisitSlotVO() {
    }

    public VisitSlotVO(String slotCode, String label, int sortOrder) {
        this.slotCode = slotCode;
        this.label = label;
        this.sortOrder = sortOrder;
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

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
