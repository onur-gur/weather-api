package com.onurgur.weatherapi.contract;

import com.onurgur.weatherapi.WeatherApiApplication;
import com.onurgur.weatherapi.controller.WeatherController;
import com.onurgur.weatherapi.dto.WeatherDto;
import com.onurgur.weatherapi.service.WeatherService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest(classes = WeatherApiApplication.class)
@AutoConfigureMockMvc
public class BaseContractTest {
    @Autowired
    private WeatherController weatherController;
    @MockBean
    private WeatherService weatherService;
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    public void before() {

        Mockito.when(this.weatherService.getWeatherWithCityName("ankara"))
                .thenReturn(new WeatherDto("Ankara",
                        "Turkey",
                        Double.valueOf("10"),
                        LocalDateTime.parse("2023-03-05 12:35", formatter)));

        RestAssuredMockMvc.standaloneSetup(this.weatherController);
    }

}
