package org.example.tourismchengdu.service;

import org.example.tourismchengdu.dto.itinerary.ItineraryDayVO;
import org.example.tourismchengdu.dto.itinerary.UpdateItineraryRequest;

import java.time.LocalDate;
import java.util.List;

public interface ItineraryService {

    List<ItineraryDayVO> listItinerary(Long userId);

    ItineraryDayVO updateItinerary(Long userId, LocalDate visitDate, UpdateItineraryRequest request);
}
