package org.example.tourismchengdu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.tourismchengdu.common.Result;
import org.example.tourismchengdu.dto.itinerary.ItineraryDayVO;
import org.example.tourismchengdu.dto.itinerary.UpdateItineraryRequest;
import org.example.tourismchengdu.security.UserContext;
import org.example.tourismchengdu.service.ItineraryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/itinerary")
@Tag(name = "行程", description = "自动生成行程与手动调整")
public class ItineraryController {

    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @GetMapping
    public Result<List<ItineraryDayVO>> listItinerary() {
        return Result.success(itineraryService.listItinerary(UserContext.getUserId()));
    }

    @PutMapping("/{date}")
    public Result<ItineraryDayVO> updateItinerary(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody UpdateItineraryRequest request) {
        return Result.success(itineraryService.updateItinerary(UserContext.getUserId(), date, request));
    }
}
