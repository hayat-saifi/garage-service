package com.renault.digital.garage_service.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record OpeningTimeDto(
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime) {
}