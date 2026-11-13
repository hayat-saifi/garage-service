package com.renault.digital.garage_service.dto;

import com.renault.digital.garage_service.enums.FuelType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleRequest(
        @NotBlank String brand,
        @NotBlank String model,
        @NotNull @Min(1886) @Max(2100) Integer anneeFabrication,
        @NotNull FuelType typeCarburant) {
}