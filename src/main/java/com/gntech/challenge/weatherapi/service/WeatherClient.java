package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.WeatherDTO;

public interface WeatherClient {
    WeatherDTO getWeather(String city);
}
