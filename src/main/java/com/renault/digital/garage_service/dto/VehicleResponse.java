package com.renault.digital.garage_service.dto;

import com.renault.digital.garage_service.enums.FuelType;

public record VehicleResponse(
        Long id,
        Long garageId,
        String brand,
        String model,
        Integer anneeFabrication,
        FuelType typeCarburant) {
}