package com.gntech.challenge.weatherapi.controller;

import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.service.WeatherService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/weather")
@Validated
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public WeatherDTO getCurrentWeather(
            @RequestParam
            @NotBlank(message = "O nome da cidade é obrigatório.")
            @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]+$", message = "O nome da cidade contém caracteres inválidos.")
            String city) {
        log.info("Recebida requisição para cidade: '{}'", city);
        WeatherDTO weather = weatherService.getWeather(city);
        log.info("Resposta retornada para cidade '{}', timestamp: {}",
                weather.getCity(),
                weather.getDateTime());
        return weather;
    }

    @GetMapping("/all")
    public List<WeatherDTO> getAllWeather(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Requisição GET /all recebida");
        return weatherService.getAllWeather(page, size);
    }

    @GetMapping("/by-city")
    public List<WeatherDTO> getWeatherByCity(
            @RequestParam
            @NotBlank(message = "O nome da cidade é obrigatório.")
            @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]+$", message = "O nome da cidade contém caracteres inválidos.")
            String city) {
        log.info("Requisição GET /weather?city={} recebida", city);
        return weatherService.getWeatherByCity(city);
    }

    @GetMapping("/latest")
    public WeatherDTO getLatestWeatherByCity(
            @RequestParam
            @NotBlank(message = "O nome da cidade é obrigatório.")
            @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]+$", message = "O nome da cidade contém caracteres inválidos.")
            String city) {
        log.info("Requisição GET /latest?city={} recebida", city);
        return weatherService.getLatestWeatherByCityOrThrow(city);
    }
}
