package com.gntech.challenge.weatherapi.controller;

import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse;
import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.service.WeatherClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/weather")
public class WeatherController {

    private final WeatherClient weatherClient;

    public WeatherController(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    @GetMapping("/fetch")
    public WeatherDTO fetch(@RequestParam String city) {
        return weatherClient.getWeather(city);
    }
}
