package com.onurgur.weatherapi.service;

import com.onurgur.weatherapi.dto.WeatherDto;

public interface WeatherService {
    WeatherDto getWeatherWithCityName(String cityName);
}
