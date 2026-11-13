package com.renault.digital.garage_service.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        List<String> details,
        Instant timestamp) {

    public static ErrorResponse of(int status, String message, List<String> details) {
        return new ErrorResponse(status, message, details, Instant.now());
    }
}