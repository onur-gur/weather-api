package com.onurgur.weatherapi.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.onurgur.weatherapi.contants.Constants;
import com.onurgur.weatherapi.dto.WeatherDto;
import com.onurgur.weatherapi.model.WeatherEntity;
import com.onurgur.weatherapi.repository.WeatherRepository;
import com.onurgur.weatherapi.service.WeatherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WeatherServiceImplTests {

    private WeatherServiceImpl weatherServiceImpl;
    private WeatherRepository weatherRepository;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule());

        weatherRepository = Mockito.mock(WeatherRepository.class);
        restTemplate = Mockito.mock(RestTemplate.class);

        weatherServiceImpl = new WeatherServiceImpl(weatherRepository, restTemplate);

        Constants constants = new Constants();
        constants.setApiUrl("weather-stack-api-url");
        constants.setApiKey("api-key");
    }

    @Test
    void getWeather_whenExistsInDbByUpdatedTimeBefore30MinNow_shouldReturnWeather() {
        String city = "istanbul";

        when(weatherRepository.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(city)).thenReturn(Optional.of(getWeatherEntity()));

        WeatherDto actualWeatherDto = weatherServiceImpl.getWeatherWithCityName(city);

        WeatherDto expectedWeather = WeatherDto.from(getWeatherEntity());
        assertEquals(expectedWeather, actualWeatherDto);

        verifyNoInteractions(restTemplate);
        verify(weatherRepository).findFirstByRequestedCityNameOrderByUpdatedTimeDesc(city);
        verifyNoMoreInteractions(weatherRepository);
    }

    private WeatherEntity getWeatherEntity() {
        return new WeatherEntity(
                UUID.randomUUID().toString(),
                "istanbul",
                "Turkey",
                Double.valueOf("10"),
                LocalDateTime.of(2023, 3, 27, 0, 0),
                LocalDateTime.of(2023, 3, 27, 0, 0));
    }

    private WeatherDto getWeatherDto() {
        return new WeatherDto("istanbul", "Turkey", Double.valueOf("10"), LocalDateTime.of(2023, 3, 27, 0, 0));
    }
}
