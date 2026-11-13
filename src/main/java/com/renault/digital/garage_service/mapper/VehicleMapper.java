package com.renault.digital.garage_service.mapper;

import com.renault.digital.garage_service.dto.VehicleRequest;
import com.renault.digital.garage_service.dto.VehicleResponse;
import com.renault.digital.garage_service.entities.Garage;
import com.renault.digital.garage_service.entities.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public Vehicle toEntity(VehicleRequest request, Garage garage) {
        Vehicle vehicle = new Vehicle();
        vehicle.setGarage(garage);
        update(vehicle, request);
        return vehicle;
    }

    public void update(Vehicle vehicle, VehicleRequest request) {
        vehicle.setBrand(request.brand().trim());
        vehicle.setModel(request.model().trim());
        vehicle.setAnneeFabrication(request.anneeFabrication());
        vehicle.setTypeCarburant(request.typeCarburant());
    }

    public VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getGarage().getId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getAnneeFabrication(),
                vehicle.getTypeCarburant());
    }
}