package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WeatherClientImpl implements WeatherClient {

    private final WebClient webClient;
    private final String apiKey;
    private final String apiUrl;

    public WeatherClientImpl(WebClient.Builder builder,
                             @Value("${openweather.api.key}") String apiKey,
                             @Value("${openweather.api.url}") String apiUrl) {
        this.webClient = builder.build();
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    @Override
    public OpenWeatherResponse getWeather(String city) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENWEATHER_API_KEY não configurada");
        }

        String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, city, apiKey);
        Mono<OpenWeatherResponse> response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(OpenWeatherResponse.class);

        return response.block();
    }
}

