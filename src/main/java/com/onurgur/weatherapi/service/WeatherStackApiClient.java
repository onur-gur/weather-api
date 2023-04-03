package com.onurgur.weatherapi.service;

import com.onurgur.weatherapi.dto.WeatherResponse;

public interface WeatherStackApiClient {
    WeatherResponse getWeatherFromWeatherStack(String cityName);
}
