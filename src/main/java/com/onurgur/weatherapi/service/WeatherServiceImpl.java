package com.onurgur.weatherapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onurgur.weatherapi.contants.Constants;
import com.onurgur.weatherapi.dto.WeatherDto;
import com.onurgur.weatherapi.dto.WeatherResponse;
import com.onurgur.weatherapi.model.WeatherEntity;
import com.onurgur.weatherapi.repository.WeatherRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {
    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherServiceImpl(WeatherRepository weatherRepository, RestTemplate restTemplate) {
        this.weatherRepository = weatherRepository;
        this.restTemplate = restTemplate;
    }

    public WeatherDto getWeatherWithCityName(String cityName) {
        Optional<WeatherEntity> weatherEntityOptional = weatherRepository.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(cityName);

        return weatherEntityOptional.map(weather -> {
            if (weather.getUpdatedTime().isBefore(LocalDateTime.now().minusMinutes(30))) {
                return WeatherDto.from(getWeatherFromWeatherStack(cityName));
            }
            return WeatherDto.from(weather);
        }).orElseGet(() -> WeatherDto.from(getWeatherFromWeatherStack(cityName)));
    }

    private WeatherEntity getWeatherFromWeatherStack(String cityName) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(getWeatherStackApiUrl(cityName), String.class);
        try {
            WeatherResponse weatherResponse = objectMapper.readValue(responseEntity.getBody(), WeatherResponse.class);
            return saveWeatherEntity(cityName, weatherResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getWeatherStackApiUrl(String cityName) {
        return Constants.API_URL + Constants.API_KEY_PARAM + Constants.API_KEY + Constants.API_QUERY_PARAM + cityName;
    }

    private WeatherEntity saveWeatherEntity(String city, WeatherResponse weatherResponse) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        WeatherEntity weatherEntity = new WeatherEntity(
                city,
                weatherResponse.location().name(),
                weatherResponse.location().country(),
                weatherResponse.current().temperature(),
                LocalDateTime.now(),
                LocalDateTime.parse(weatherResponse.location().localTime(), dateTimeFormatter)
        );
        return weatherRepository.save(weatherEntity);
    }
}
