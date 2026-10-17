package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.dto.weather.WeatherVO;
import org.example.tourismchengdu.service.WeatherService;
import org.example.tourismchengdu.util.JsonUtils;
import org.example.tourismchengdu.util.WeatherCodeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WeatherServiceImpl implements WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherServiceImpl.class);
    private static final String CACHE_KEY = "weather:chengdu";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    // open-meteo 7 天预报 API（成都 30.57°N, 104.07°E）
    private static final String OPEN_METEO_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=30.57&longitude=104.07" +
            "&daily=weather_code,temperature_2m_max,temperature_2m_min" +
            "&timezone=Asia/Shanghai&forecast_days=7";

    private final RestClient restClient;
    private final StringRedisTemplate stringRedisTemplate;

    public WeatherServiceImpl(RestClient restClient,
                              StringRedisTemplate stringRedisTemplate) {
        this.restClient = restClient;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public WeatherVO getChengduWeather() {
        // 尝试读 Redis 缓存，连不上则降级为直接调外部 API
        try {
            String cached = stringRedisTemplate.opsForValue().get(CACHE_KEY);
            if (cached != null && !cached.isBlank()) {
                return JsonUtils.fromJson(cached, WeatherVO.class);
            }
        } catch (Exception e) {
            log.warn("Redis 不可用，降级为直接调用 open-meteo API: {}", e.getMessage());
        }

        WeatherVO weather = fetchFromOpenMeteo();

        // 尝试写缓存，失败不影响返回结果
        try {
            stringRedisTemplate.opsForValue().set(CACHE_KEY, JsonUtils.toJson(weather), CACHE_TTL);
        } catch (Exception e) {
            log.warn("Redis 写缓存失败，不影响返回: {}", e.getMessage());
        }
        return weather;
    }

    @SuppressWarnings("unchecked")
    private WeatherVO fetchFromOpenMeteo() {
        Map<String, Object> response = restClient.get()
                .uri(OPEN_METEO_URL)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException("open-meteo API 返回为空");
        }

        Map<String, Object> daily = (Map<String, Object>) response.get("daily");
        if (daily == null) {
            throw new IllegalStateException("open-meteo API 返回数据格式异常");
        }

        List<String> dates = (List<String>) daily.get("time");
        List<Integer> weatherCodes = (List<Integer>) daily.get("weather_code");
        List<Double> tempMaxList = (List<Double>) daily.get("temperature_2m_max");
        List<Double> tempMinList = (List<Double>) daily.get("temperature_2m_min");

        if (dates == null || dates.isEmpty()) {
            throw new IllegalStateException("open-meteo API 返回数据为空");
        }

        WeatherVO weatherVO = new WeatherVO();
        weatherVO.setCity("成都");
        List<WeatherVO.DailyWeatherVO> dailyList = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            WeatherVO.DailyWeatherVO day = new WeatherVO.DailyWeatherVO();
            day.setDate(LocalDate.parse(dates.get(i)));
            day.setWeatherText(WeatherCodeMapper.toText(weatherCodes.get(i)));
            day.setTempMax(tempMaxList.get(i));
            day.setTempMin(tempMinList.get(i));
            dailyList.add(day);
        }
        weatherVO.setDaily(dailyList);
        return weatherVO;
    }
}
