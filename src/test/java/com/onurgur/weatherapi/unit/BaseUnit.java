package com.onurgur.weatherapi.unit;

import com.onurgur.weatherapi.model.WeatherEntity;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseUnit {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Instant getCurrentInstant() {
        String instantExpected = "2023-03-08T10:15:30Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), Clock.systemDefaultZone().getZone());

        return Instant.now(clock);
    }

    public LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.ofInstant(getCurrentInstant(), Clock.systemDefaultZone().getZone());
    }

    public static String requestedCity = "ankara";

    public WeatherEntity getSavedWeatherEntity(String responseLocalTime) {
        return new WeatherEntity("id",
                requestedCity,
                "Ankara",
                "Turkey",
                Double.valueOf("10"),
                getCurrentLocalDateTime(),
                LocalDateTime.parse(responseLocalTime, formatter));
    }

    public WeatherEntity getToSavedWeatherEntity(String responseLocalTime) {
        return new WeatherEntity(requestedCity,
                "Ankara",
                "Turkey",
                Double.valueOf("10"),
                getCurrentLocalDateTime(),
                LocalDateTime.parse(responseLocalTime, formatter));
    }

    public String getAnkaraWeatherJson() {
        return """
                {
                    "request": {
                        "type": "City",
                        "query": "Ankara, Turkey",
                        "language": "en",
                        "unit": "m"
                    },
                    "location": {
                        "name": "Ankara",
                        "country": "Turkey",
                        "region": "Ankara",
                        "lat": "39.927",
                        "lon": "32.864",
                        "timezone_id": "Europe/Istanbul",
                        "localtime": "2023-03-08 21:45",
                        "localtime_epoch": 1680566340,
                        "utc_offset": "3.0"
                    },
                    "current": {
                        "observation_time": "08:59 PM",
                        "temperature": 10,
                        "weather_code": 113,
                        "weather_icons": [
                            "https://cdn.worldweatheronline.com/images/wsymbols01_png_64/wsymbol_0008_clear_sky_night.png"
                        ],
                        "weather_descriptions": [
                            "Clear"
                        ],
                        "wind_speed": 11,
                        "wind_degree": 110,
                        "wind_dir": "ESE",
                        "pressure": 1012,
                        "precip": 0,
                        "humidity": 48,
                        "cloudcover": 0,
                        "feelslike": 15,
                        "uv_index": 1,
                        "visibility": 10,
                        "is_day": "no"
                    }
                }
                """;
    }
}
