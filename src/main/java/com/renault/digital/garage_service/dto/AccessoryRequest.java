package com.renault.digital.garage_service.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccessoryRequest(
        @NotBlank String nom,
        String description,
        @NotNull @DecimalMin("0.0") BigDecimal prix,
        @NotBlank String type) {
}