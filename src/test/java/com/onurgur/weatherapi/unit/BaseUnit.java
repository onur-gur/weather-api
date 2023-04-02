package com.onurgur.weatherapi.unit;

import com.onurgur.weatherapi.model.WeatherEntity;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseUnit {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public Instant getCurrentInstant() {
        String instantExpected = "2023-03-29T23:53:00Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), Clock.systemDefaultZone().getZone());

        return Instant.now(clock);
    }

    public LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.ofInstant(getCurrentInstant(), Clock.systemDefaultZone().getZone());
    }

    public static String requestedCity = "istanbul";

    public WeatherEntity getSavedWeatherEntity(String responseLocalTime) {
        return new WeatherEntity("id",
                requestedCity,
                "istanbul",
                "Turkey",
                2d,
                getCurrentLocalDateTime(),
                LocalDateTime.parse(responseLocalTime, formatter));
    }

    public WeatherEntity getToSavedWeatherEntity(String responseLocalTime) {
        return new WeatherEntity(requestedCity,
                "Amsterdam",
                "Netherlands",
                2d,
                getCurrentLocalDateTime(),
                LocalDateTime.parse(responseLocalTime, formatter));
    }

    public String getAmsterdamWeatherJson() {
        return """
                {
                    "request": {
                        "type": "City",
                        "query": "Amsterdam, Netherlands",
                        "language": "en",
                        "unit": "m"
                    },
                    "location": {
                        "name": "Amsterdam",
                        "country": "Netherlands",
                        "region": "North Holland",
                        "lat": "52.374",
                        "lon": "4.890",
                        "timezone_id": "Europe/Amsterdam",
                        "localtime": "2023-03-08 21:45",
                        "localtime_epoch": 1678311900,
                        "utc_offset": "1.0"
                    },
                    "current": {
                        "observation_time": "08:45 PM",
                        "temperature": 2,
                        "weather_code": 113,
                        "weather_icons": [
                            "https://cdn.worldweatheronline.com/images/wsymbols01_png_64/wsymbol_0008_clear_sky_night.png"
                        ],
                        "weather_descriptions": [
                            "Clear"
                        ],
                        "wind_speed": 11,
                        "wind_degree": 70,
                        "wind_dir": "ENE",
                        "pressure": 992,
                        "precip": 0,
                        "humidity": 87,
                        "cloudcover": 0,
                        "feelslike": -2,
                        "uv_index": 1,
                        "visibility": 10,
                        "is_day": "no"
                    }
                }
                """;
    }
}
