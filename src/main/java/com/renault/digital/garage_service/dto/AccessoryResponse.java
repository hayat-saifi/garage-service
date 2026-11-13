package com.renault.digital.garage_service.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import java.math.BigDecimal;

public record AccessoryResponse(
        Long id,
        Long vehicleId,
        String nom,
        String description,
        BigDecimal prix,
        String type) {
}