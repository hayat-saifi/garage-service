package com.renault.digital.garage_service.services;

import com.renault.digital.garage_service.entities.Garage;
import com.renault.digital.garage_service.entities.Vehicle;
import com.renault.digital.garage_service.models.VehicleModel;
import com.renault.digital.garage_service.repositories.GarageRepository;
import com.renault.digital.garage_service.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService{

    private final VehicleRepository vehicleRepository;
    private  final GarageRepository garageRepository;

    @Override
    public VehicleModel addVehicle(Long garageId, VehicleModel vehicleModel) {
        Garage garage = garageRepository.findById(garageId).orElseThrow(()-> new RuntimeException("Garage not found"));
        Vehicle saved = vehicleRepository.save(vehicleModel.toEntity(garage));

        return VehicleModel.of(saved);
    }

    @Override
    public void deleteVehicle(Long id, Long garageId) {
      Vehicle vehicle = vehicleRepository.findByIdAndGarageId(id, garageId).orElseThrow(()-> new RuntimeException("vehicle not found"));
        vehicleRepository.delete(vehicle);
    }

    @Override
    public VehicleModel updateVehicle(Long id, Long garageId, VehicleModel vehicleModel) {
        Vehicle existing = vehicleRepository.findByIdAndGarageId(id, garageId).orElseThrow(()-> new RuntimeException("vehicle not found"));
        existing.setBrand(vehicleModel.brand());
        existing.setYear(vehicleModel.year());
        existing.setFuelType(vehicleModel.fuelType());
        Vehicle saved = vehicleRepository.save(existing);

        return VehicleModel.of(saved);
    }

    @Override
    public List<VehicleModel> getVehiclesByGarage(Long garageId) {
        return VehicleModel.of(vehicleRepository.findByGarageId(garageId));
    }

    @Override
    public Page<VehicleModel> searchVehicleModel(String brand, Integer Year, String fuelType, Pageable pageable) {
        return null;
    }

}
