package org.example.tourismchengdu.dto.itinerary;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateItineraryRequest {

    @NotEmpty(message = "景点排序不能为空")
    private List<Long> sceneOrder;

    @Size(max = 500, message = "备注不能超过500字")
    private String note;

    public List<Long> getSceneOrder() {
        return sceneOrder;
    }

    public void setSceneOrder(List<Long> sceneOrder) {
        this.sceneOrder = sceneOrder;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
