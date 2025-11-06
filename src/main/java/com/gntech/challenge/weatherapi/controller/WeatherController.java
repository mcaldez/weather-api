package com.gntech.challenge.weatherapi.controller;

import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.service.WeatherService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/weather")
@Validated
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/fetch")
    public WeatherDTO fetch(
            @RequestParam
            @NotBlank(message = "O nome da cidade é obrigatório.")
            @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]+$", message = "O nome da cidade contém caracteres inválidos.")
            String city) {
        return weatherService.getWeather(city);
    }
}
