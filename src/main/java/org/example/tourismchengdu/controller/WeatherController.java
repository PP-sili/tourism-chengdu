package org.example.tourismchengdu.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.tourismchengdu.common.Result;
import org.example.tourismchengdu.dto.weather.WeatherVO;
import org.example.tourismchengdu.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@Tag(name = "天气", description = "成都天气（Redis 缓存）")
@SecurityRequirements
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/chengdu")
    public Result<WeatherVO> getChengduWeather() {
        return Result.success(weatherService.getChengduWeather());
    }
}
