package org.example.tourismchengdu.dto.itinerary;

import java.time.LocalDate;
import java.util.List;

public class ItineraryDayVO {

    private LocalDate visitDate;
    private String note;
    private List<ItinerarySceneItemVO> scenes;

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<ItinerarySceneItemVO> getScenes() {
        return scenes;
    }

    public void setScenes(List<ItinerarySceneItemVO> scenes) {
        this.scenes = scenes;
    }
}
