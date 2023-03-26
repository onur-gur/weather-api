package com.onurgur.weatherapi.controller;

import com.onurgur.weatherapi.controller.validation.CityNameConstraint;
import com.onurgur.weatherapi.dto.WeatherDto;
import com.onurgur.weatherapi.service.WeatherService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/weather")
@Validated
@Tag(name = "Weather API V1", description = "Search with city name for temperature")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Operation(
            method = "GET",
            summary = "",
            description = "Search with city name for temperature",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = WeatherDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "City name is wrong",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Rate limit exceeded",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal/External server error.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    @GetMapping("/{cityName}")
    @RateLimiter(name = "basic")
    public ResponseEntity<WeatherDto> getWeatherWithCityName(@PathVariable("cityName") @NotBlank @CityNameConstraint String cityName) {
        return ResponseEntity.ok(weatherService.getWeatherWithCityName(cityName));
    }
}
