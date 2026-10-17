package org.example.tourismchengdu.util;

import java.util.Map;

public final class WeatherCodeMapper {

    private static final Map<Integer, String> TEXT_MAP = Map.ofEntries(
            Map.entry(0, "晴"),
            Map.entry(1, "晴"),
            Map.entry(2, "多云"),
            Map.entry(3, "阴"),
            Map.entry(45, "雾"),
            Map.entry(48, "雾"),
            Map.entry(51, "毛毛雨"),
            Map.entry(53, "毛毛雨"),
            Map.entry(55, "毛毛雨"),
            Map.entry(61, "小雨"),
            Map.entry(63, "中雨"),
            Map.entry(65, "大雨"),
            Map.entry(71, "小雪"),
            Map.entry(73, "中雪"),
            Map.entry(75, "大雪"),
            Map.entry(80, "阵雨"),
            Map.entry(81, "阵雨"),
            Map.entry(82, "阵雨"),
            Map.entry(95, "雷阵雨"),
            Map.entry(96, "雷阵雨"),
            Map.entry(99, "雷阵雨")
    );

    private WeatherCodeMapper() {
    }

    public static String toText(Integer code) {
        if (code == null) {
            return "未知";
        }
        return TEXT_MAP.getOrDefault(code, "未知");
    }
}
