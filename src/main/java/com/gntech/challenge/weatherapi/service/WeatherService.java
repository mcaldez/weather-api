package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final WeatherClient weatherClient;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    public WeatherDTO getWeather(String city) {

        String formattedCity = formatCityName(city);
        return weatherClient.getWeather(formattedCity);
    }

    private String formatCityName(String city) {
        String trimmed = city.trim();
        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
    }
}