package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse;
import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.exception.WeatherException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    public WeatherDTO getWeather(String city) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENWEATHER_API_KEY não configurada");
        }

        String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, city, apiKey);
        Mono<OpenWeatherResponse> response = webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        clientResponse -> {
                            return clientResponse.bodyToMono(String.class)
                                    .map(body -> new WeatherException("Cidade não encontrada ou inválida.", HttpStatus.valueOf(clientResponse.statusCode().value())));
                        }
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        clientResponse -> {
                            return clientResponse.bodyToMono(String.class)
                                    .map(body -> new WeatherException("Erro no servidor do OpenWeather. Tente novamente mais tarde.", HttpStatus.valueOf(clientResponse.statusCode().value())));
                        }
                )
                .bodyToMono(OpenWeatherResponse.class);

        OpenWeatherResponse openWeather = response.block();
        if (openWeather == null) {
            throw new WeatherException("Não foi possível obter dados do OpenWeather", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        return toWeatherDTO(openWeather);
    }

    private WeatherDTO toWeatherDTO(OpenWeatherResponse openWeather) {
        return new WeatherDTO(
                openWeather.name,
                openWeather.sys != null ? openWeather.sys.country : null,
                openWeather.main != null ? openWeather.main.temp : null,
                openWeather.main != null ? openWeather.main.humidity : null,
                openWeather.wind != null ? openWeather.wind.speed : null,
                (openWeather.weather != null && openWeather.weather.length > 0)
                        ? openWeather.weather[0].description : null,
                java.time.LocalDateTime.now()
        );
    }

}

