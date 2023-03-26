package com.onurgur.weatherapi.repository;

import com.onurgur.weatherapi.model.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherEntity, String> {
    //select * from weather where requestedCityName ='cityName' order by updatedTime desc limit 1
    Optional<WeatherEntity> findFirstByRequestedCityNameOrderByUpdatedTimeDesc(String cityName);
}
