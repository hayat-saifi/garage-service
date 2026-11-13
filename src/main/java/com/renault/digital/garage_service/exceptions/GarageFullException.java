package com.renault.digital.garage_service.exceptions;

public class GarageFullException extends RuntimeException {

    public GarageFullException(Long garageId, int maxVehicles) {
        super("Le garage " + garageId + " a atteint son quota de " + maxVehicles + " véhicules");
    }
}
