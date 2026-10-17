package org.example.tourismchengdu.dto.weather;

import java.time.LocalDate;
import java.util.List;

public class WeatherVO {

    private String city;
    private List<DailyWeatherVO> daily;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<DailyWeatherVO> getDaily() {
        return daily;
    }

    public void setDaily(List<DailyWeatherVO> daily) {
        this.daily = daily;
    }

    public static class DailyWeatherVO {
        private LocalDate date;
        private String weatherText;
        private Double tempMin;
        private Double tempMax;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getWeatherText() {
            return weatherText;
        }

        public void setWeatherText(String weatherText) {
            this.weatherText = weatherText;
        }

        public Double getTempMin() {
            return tempMin;
        }

        public void setTempMin(Double tempMin) {
            this.tempMin = tempMin;
        }

        public Double getTempMax() {
            return tempMax;
        }

        public void setTempMax(Double tempMax) {
            this.tempMax = tempMax;
        }
    }
}
