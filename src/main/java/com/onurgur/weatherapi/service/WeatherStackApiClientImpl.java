package com.onurgur.weatherapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onurgur.weatherapi.contants.Constants;
import com.onurgur.weatherapi.dto.WeatherResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherStackApiClientImpl implements WeatherStackApiClient {
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherStackApiClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public WeatherResponse getWeatherFromWeatherStack(String cityName) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(getWeatherStackApiUrl(cityName), String.class);
        try {
            return objectMapper.readValue(responseEntity.getBody(), WeatherResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getWeatherStackApiUrl(String cityName) {
        return Constants.API_URL + Constants.API_KEY_PARAM + Constants.API_KEY + Constants.API_QUERY_PARAM + cityName;
    }
}
