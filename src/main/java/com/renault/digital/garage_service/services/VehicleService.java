package com.renault.digital.garage_service.services;

import com.renault.digital.garage_service.models.VehicleModel;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

public interface VehicleService {

    VehicleModel addVehicle(Long garageId, VehicleModel vehicleModel);
    void deleteVehicle(Long id, Long garageId);
    VehicleModel updateVehicle(Long id,Long garageId, VehicleModel vehicleModel);
    List<VehicleModel> getVehiclesByGarage(Long garageId);
    Page<VehicleModel> searchVehicleModel(String brand, Integer Year, String fuelType, Pageable pageable);
}
