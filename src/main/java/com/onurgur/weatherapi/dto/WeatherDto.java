package com.onurgur.weatherapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onurgur.weatherapi.model.WeatherEntity;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherDto(
        String cityName,
        String country,
        Double temperature,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime updatedTime
) {
    public static WeatherDto from(WeatherEntity weatherEntity) {
        return new WeatherDto(
                weatherEntity.getCityName(),
                weatherEntity.getCountry(),
                weatherEntity.getTemperature(),
                weatherEntity.getUpdatedTime());
    }
}
