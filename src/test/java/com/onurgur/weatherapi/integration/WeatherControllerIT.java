package com.onurgur.weatherapi.integration;

import com.onurgur.weatherapi.dto.WeatherDto;
import com.onurgur.weatherapi.repository.WeatherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


public class WeatherControllerIT extends AbstractTestIT {

    @Autowired
    private WeatherRepository weatherRepository;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        overrideProps(registry);
    }

    @Test
    public void should_get_weather() {
        //given
        mockGetWeatherForIstanbul();

        //when
        ResponseEntity<WeatherDto> response =
                testRestTemplate.exchange("/api/v1/weather/istanbul", HttpMethod.GET, new HttpEntity<>(null), WeatherDto.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull()
                .extracting("cityName", "country", "temperature", "updatedTime")
                .containsExactly("Istanbul", "Turkey", Double.valueOf("13"), LocalDateTime.now());

        //with restAssured
        /* given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/v1/weather/istanbul")
                .then()
                .statusCode(200);*/
    }


}
