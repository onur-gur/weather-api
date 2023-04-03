package com.onurgur.weatherapi.service;

import com.onurgur.weatherapi.dto.WeatherDto;
import com.onurgur.weatherapi.dto.WeatherResponse;
import com.onurgur.weatherapi.model.WeatherEntity;
import com.onurgur.weatherapi.repository.WeatherRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {
    private final WeatherRepository weatherRepository;
    private final WeatherStackApiClient weatherStackApiClient;
    private final Clock clock;

    public WeatherServiceImpl(WeatherRepository weatherRepository, WeatherStackApiClient weatherStackApiClient, Clock clock) {
        this.weatherRepository = weatherRepository;
        this.weatherStackApiClient = weatherStackApiClient;
        this.clock = clock;
    }

    public WeatherDto getWeatherWithCityName(String cityName) {
        Optional<WeatherEntity> weatherEntityOptional = weatherRepository.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(cityName);

        return weatherEntityOptional.map(weather -> {
            if (weather.getUpdatedTime().isBefore(getLocalDateTimeNow().minusMinutes(30))) {
                return WeatherDto.from(getWeatherFromWeatherStack(cityName));
            }
            return WeatherDto.from(weather);
        }).orElseGet(() -> WeatherDto.from(getWeatherFromWeatherStack(cityName)));
    }

    private WeatherEntity getWeatherFromWeatherStack(String cityName) {
        WeatherResponse weatherFromWeatherStack = weatherStackApiClient.getWeatherFromWeatherStack(cityName);
        return saveWeatherEntity(cityName, weatherFromWeatherStack);
    }

    private WeatherEntity saveWeatherEntity(String city, WeatherResponse weatherResponse) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        WeatherEntity weatherEntity = new WeatherEntity(
                city,
                weatherResponse.location().name(),
                weatherResponse.location().country(),
                weatherResponse.current().temperature(),
                getLocalDateTimeNow(),
                LocalDateTime.parse(weatherResponse.location().localTime(), dateTimeFormatter)
        );
        return weatherRepository.save(weatherEntity);
    }

    private LocalDateTime getLocalDateTimeNow() {
        Instant instant = clock.instant();
        return LocalDateTime.ofInstant(
                instant,
                Clock.systemDefaultZone().getZone());
    }
}
