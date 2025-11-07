package com.gntech.challenge.weatherapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Modelo de erro padronizado para respostas de falha")
public class ErrorResponseDTO {

    @Schema(description = "Mensagem descritiva do erro", example = "informa causa do erro")
    private String error;

    @Schema(description = "Data e hora do erro", example = "2025-11-07T10:10:33.5559308")
    private LocalDateTime timestamp;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(String error, LocalDateTime timestamp) {
        this.error = error;
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}