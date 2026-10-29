package com.renault.digital.garage_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FuelType {
    DIESEL,
    PETROL,
    HYBRID,
    ELECTRIC;

    @JsonCreator
    public static FuelType from(String value){
        return FuelType.valueOf(value.trim().toUpperCase());
    }
}
