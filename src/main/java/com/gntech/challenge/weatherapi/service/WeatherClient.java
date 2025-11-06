package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse;

public interface WeatherClient {
    OpenWeatherResponse getWeather(String city);
}
