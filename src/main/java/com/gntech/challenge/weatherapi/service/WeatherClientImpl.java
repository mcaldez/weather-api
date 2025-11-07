package com.gntech.challenge.weatherapi.service;

import com.gntech.challenge.weatherapi.dto.OpenWeatherResponse;
import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.exception.WeatherException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Component
@Slf4j
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

        log.info("Consultando OpenWeather API para cidade: {}", city);

        String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, city, apiKey);

        try {
            OpenWeatherResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(OpenWeatherResponse.class)
                    .block();

            if (response == null) {
                throw new WeatherException("Resposta vazia do OpenWeather", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return toWeatherDTO(response);

        } catch (WebClientRequestException ex) {
            log.error("Erro de conexão ao consultar OpenWeather para '{}': {}", city, ex.getMessage());
            throw new WeatherException("Erro de conexão com o OpenWeather", HttpStatus.BAD_GATEWAY);

        }catch (WeatherException ex) {
            throw ex;

        } catch (Exception ex) {
            boolean notFound = ex.getMessage() != null && ex.getMessage().contains("404");
            log.error("Erro ao consultar OpenWeather para '{}': {}", city, ex.getMessage());

            throw new WeatherException(
                    notFound ? "Cidade não encontrada ou inválida."
                            : "Erro ao consultar OpenWeather.",
                    notFound ? HttpStatus.NOT_FOUND : HttpStatus.BAD_GATEWAY
            );
        }
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

