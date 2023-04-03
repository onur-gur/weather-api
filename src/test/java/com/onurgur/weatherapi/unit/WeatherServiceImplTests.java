package com.onurgur.weatherapi.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.onurgur.weatherapi.dto.WeatherDto;
import com.onurgur.weatherapi.dto.WeatherResponse;
import com.onurgur.weatherapi.model.WeatherEntity;
import com.onurgur.weatherapi.repository.WeatherRepository;
import com.onurgur.weatherapi.service.WeatherServiceImpl;
import com.onurgur.weatherapi.service.WeatherStackApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WeatherServiceImplTests extends BaseUnit {

    private WeatherServiceImpl weatherServiceImpl;
    private WeatherRepository weatherRepository;
    private Clock clock;
    private ObjectMapper objectMapper;
    private WeatherStackApiClient weatherStackApiClient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule());

        weatherRepository = mock(WeatherRepository.class);
        clock = mock(Clock.class);
        weatherStackApiClient = mock(WeatherStackApiClient.class);

        weatherServiceImpl = new WeatherServiceImpl(weatherRepository, weatherStackApiClient, clock);

        when(clock.instant()).thenReturn(getCurrentInstant());
        when(clock.getZone()).thenReturn(Clock.systemDefaultZone().getZone());
    }

    @Test
    void getWeatherWithCityName_whenFirstRequestForRequestedCityName_shouldRequestWeatherStackApiAndSaveEntityAndReturnWeather() throws JsonProcessingException {
        String response = getAnkaraWeatherJson();
        WeatherResponse weatherResponse = objectMapper.readValue(response, WeatherResponse.class);

        WeatherEntity toSaveEntity = getToSavedWeatherEntity(weatherResponse.location().localTime());
        WeatherEntity savedEntity = getSavedWeatherEntity(weatherResponse.location().localTime());

        WeatherDto expectedWeather = new WeatherDto(savedEntity.getCityName(), savedEntity.getCountry(), savedEntity.getTemperature(), savedEntity.getUpdatedTime());

        when(weatherRepository.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(requestedCity)).thenReturn(Optional.empty());
        when(weatherStackApiClient.getWeatherFromWeatherStack(requestedCity)).thenReturn(weatherResponse);
        when(weatherRepository.save(toSaveEntity)).thenReturn(savedEntity);

        WeatherDto actualWeatherDto = weatherServiceImpl.getWeatherWithCityName(requestedCity);

        assertEquals(expectedWeather, actualWeatherDto);

        verify(weatherRepository).save(toSaveEntity);
        verify(weatherStackApiClient).getWeatherFromWeatherStack(requestedCity);
    }

    @Test
    void getWeatherWithCityName_whenExistsInDbByUpdatedTimeBefore30MinNow_shouldReturnSavedWeatherResponseInDb() throws JsonProcessingException {
        String responseJson = getAnkaraWeatherJson();
        WeatherResponse response = objectMapper.readValue(responseJson, WeatherResponse.class);
        WeatherEntity savedEntity = getSavedWeatherEntity(response.location().localTime());

        WeatherDto expectedWeather = new WeatherDto(savedEntity.getCityName(), savedEntity.getCountry(), savedEntity.getTemperature(), savedEntity.getUpdatedTime());

        when(weatherRepository.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(requestedCity)).thenReturn(Optional.of(savedEntity));

        WeatherDto actualWeatherDto = weatherServiceImpl.getWeatherWithCityName(requestedCity);

        assertEquals(expectedWeather, actualWeatherDto);

        verifyNoInteractions(weatherStackApiClient);
        verify(weatherRepository).findFirstByRequestedCityNameOrderByUpdatedTimeDesc(requestedCity);
        verifyNoMoreInteractions(weatherRepository);
    }

    @Test
    void getWeatherWithCityName_whenExistsInDbByUpdatedTimeAfter30MinNow_shouldRequestWeatherStackApiAndSaveEntityAndReturnWeather() throws JsonProcessingException {
        String response = getAnkaraWeatherJson();
        WeatherResponse weatherResponse = objectMapper.readValue(response, WeatherResponse.class);

        String cityName = "Ankara";
        WeatherEntity oldEntity = new WeatherEntity("id",
                requestedCity,
                "Ankara",
                "Turkey",
                Double.valueOf("10"),
                LocalDateTime.parse("2023-03-05 12:35", formatter),
                LocalDateTime.parse(weatherResponse.location().localTime(), formatter));

        WeatherEntity toSaveEntity = getToSavedWeatherEntity(weatherResponse.location().localTime());
        WeatherEntity savedEntity = getSavedWeatherEntity(weatherResponse.location().localTime());

        WeatherDto expectedWeather = new WeatherDto(savedEntity.getCityName(), savedEntity.getCountry(), savedEntity.getTemperature(), savedEntity.getUpdatedTime());

        when(weatherRepository.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(cityName)).thenReturn(Optional.of(oldEntity));
        when(weatherStackApiClient.getWeatherFromWeatherStack(cityName)).thenReturn(weatherResponse);
        when(weatherRepository.save(toSaveEntity)).thenReturn(savedEntity);

        WeatherDto actualWeatherDto = weatherServiceImpl.getWeatherWithCityName(cityName);

        assertEquals(expectedWeather, actualWeatherDto);

        verify(weatherRepository).save(toSaveEntity);
        verify(weatherStackApiClient).getWeatherFromWeatherStack(cityName);
    }
}
