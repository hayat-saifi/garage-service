package com.renault.digital.garage_service.services;

import com.renault.digital.garage_service.dto.PageResponse;
import com.renault.digital.garage_service.dto.VehicleRequest;
import com.renault.digital.garage_service.dto.VehicleResponse;
import com.renault.digital.garage_service.entities.Garage;
import com.renault.digital.garage_service.entities.Vehicle;
import com.renault.digital.garage_service.exceptions.GarageFullException;
import com.renault.digital.garage_service.exceptions.NotFoundException;
import com.renault.digital.garage_service.mapper.VehicleMapper;
import com.renault.digital.garage_service.repositories.GarageRepository;
import com.renault.digital.garage_service.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final GarageRepository garageRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    //Quota configurable (garage.max-vehicles)
    @Value("${garage.max-vehicles:50}")
    private int maxVehiclesPerGarage;

    @Override
    public VehicleResponse createVehicle(Long garageId, VehicleRequest request) {
        // Garage bloqué pendant l'ajout : deux ajouts simultanés ne peuvent pas dépasser le quota.
        Garage garage = garageRepository.findByIdForUpdate(garageId)
                .orElseThrow(() -> new NotFoundException("Garage " + garageId + " introuvable"));

        if (vehicleRepository.countByGarageId(garageId) >= maxVehiclesPerGarage) {
            throw new GarageFullException(garageId, maxVehiclesPerGarage);
        }

        Vehicle vehicle = vehicleRepository.save(vehicleMapper.toEntity(request, garage));
        return vehicleMapper.toResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        return vehicleMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByGarage(Long garageId) {
        if (!garageRepository.existsById(garageId)) {
            throw new NotFoundException("Garage " + garageId + " introuvable");
        }
        return vehicleRepository.findByGarageIdOrderById(garageId).stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VehicleResponse> getVehiclesByModel(String model, Pageable pageable) {
        Page<Vehicle> page = (model == null || model.isBlank())
                ? vehicleRepository.findAll(pageable)
                : vehicleRepository.findByModelIgnoreCase(model.trim(), pageable);
        return PageResponse.from(page.map(vehicleMapper::toResponse));
    }

    @Override
    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        Vehicle vehicle = findOrThrow(id);
        vehicleMapper.update(vehicle, request);
        return vehicleMapper.toResponse(vehicle);
    }

    @Override
    public void deleteVehicle(Long id) {
        vehicleRepository.delete(findOrThrow(id));
    }

    private Vehicle findOrThrow(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Véhicule " + id + " introuvable"));
    }
}