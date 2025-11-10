package com.gntech.challenge.weatherapi.controller;

import com.gntech.challenge.weatherapi.dto.WeatherDTO;
import com.gntech.challenge.weatherapi.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/v1/weather")
@Validated
@Slf4j
@Tag(name = "Weather API", description = "Operações para consultar e armazenar dados de clima")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    @Operation(summary = "Retorna o clima atual de uma cidade",
    description = "Este endpoint consulta a API externa OpenWeather e retorna os dados de clima atual da cidade informada, incluindo temperatura, umidade, velocidade do vento e descrição do clima."
            )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso! Dados do clima retornados.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WeatherDTO.class),
                            examples = @ExampleObject(value = """
                                {
                                  "city": "Florianópolis",
                                  "country": "BR",
                                  "temperature": 25.3,
                                  "humidity": 70,
                                  "windSpeed": 5.2,
                                  "description": "céu limpo",
                                  "dateTime": "2025-11-07T10:00:00"
                                }
                                """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetro de cidade inválido ou ausente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cidade não encontrada.", content = @Content),
            @ApiResponse(responseCode = "502", description = "Erro ao chamar a API externa OpenWeather.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content)
    })
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
    @Operation(summary = "Retorna todos os registros de clima paginados",
            description = "Este endpoint retorna todos os registros de clima armazenados no banco de dados, com paginação configurável pelos parâmetros 'page' e 'size'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso! Lista de registros retornada.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WeatherDTO.class),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "city": "Florianópolis",
                                        "country": "BR",
                                        "temperature": 25.3,
                                        "humidity": 70,
                                        "windSpeed": 5.2,
                                        "description": "céu limpo",
                                        "dateTime": "2025-11-07T10:00:00"
                                      },
                                      {
                                        "city": "São Paulo",
                                        "country": "BR",
                                        "temperature": 22.1,
                                        "humidity": 65,
                                        "windSpeed": 3.5,
                                        "description": "nublado",
                                        "dateTime": "2025-11-07T10:05:00"
                                      }
                                    ]
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content)
    })
    public List<WeatherDTO> getAllWeather(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Número da página (0-based)", example = "0")
            int page,
            @RequestParam(defaultValue = "10")
            @Parameter(description = "Quantidade de itens por página", example = "10")
            int size) {
        log.info("Requisição GET /all recebida");
        return weatherService.getAllWeather(page, size);
    }

    @GetMapping("/by-city")
    @Operation(summary = "Retorna todos os registros de clima de uma cidade específica",
            description = "Este endpoint retorna todos os registros de clima armazenados no banco para a cidade informada, podendo incluir múltiplos registros históricos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso! Lista de registros retornada.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WeatherDTO.class),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "city": "Florianópolis",
                                        "country": "BR",
                                        "temperature": 25.3,
                                        "humidity": 70,
                                        "windSpeed": 5.2,
                                        "description": "céu limpo",
                                        "dateTime": "2025-11-07T10:00:00"
                                      }
                                    ]
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetro de cidade inválido ou ausente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cidade não encontrada.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content)
    })
    public List<WeatherDTO> getWeatherByCity(
            @RequestParam
            @NotBlank(message = "O nome da cidade é obrigatório.")
            @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]+$", message = "O nome da cidade contém caracteres inválidos.")
            String city) {
        log.info("Requisição GET /weather?city={} recebida", city);
        return weatherService.getWeatherByCity(city);
    }

    @GetMapping("/latest")
    @Operation(summary = "Retorna o último registro de clima de uma cidade",
            description = "Este endpoint retorna o registro mais recente de clima para a cidade informada, consultando os dados armazenados no banco de dados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso! Último registro retornado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WeatherDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "city": "Florianópolis",
                                        "country": "BR",
                                        "temperature": 25.3,
                                        "humidity": 70,
                                        "windSpeed": 5.2,
                                        "description": "céu limpo",
                                        "dateTime": "2025-11-07T10:00:00"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetro de cidade inválido ou ausente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Nenhum registro encontrado para a cidade informada.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content)
    })
    public WeatherDTO getLatestWeatherByCity(
            @RequestParam
            @NotBlank(message = "O nome da cidade é obrigatório.")
            @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]+$", message = "O nome da cidade contém caracteres inválidos.")
            String city) {
        log.info("Requisição GET /latest?city={} recebida", city);
        return weatherService.getLatestWeatherByCityOrThrow(city);
    }
}
