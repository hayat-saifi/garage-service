package com.renault.digital.garage_service.services;

import com.renault.digital.garage_service.dto.AccessoryRequest;
import com.renault.digital.garage_service.dto.AccessoryResponse;
import com.renault.digital.garage_service.entities.Accessory;
import com.renault.digital.garage_service.entities.Vehicle;
import com.renault.digital.garage_service.exceptions.NotFoundException;
import com.renault.digital.garage_service.mapper.AccessoryMapper;
import com.renault.digital.garage_service.repositories.AccessoryRepository;
import com.renault.digital.garage_service.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AccessoryServiceImpl implements AccessoryService {

    private final AccessoryRepository accessoryRepository;
    private final VehicleRepository vehicleRepository;
    private final AccessoryMapper accessoryMapper;

    @Override
    public AccessoryResponse createAccessory(Long vehicleId, AccessoryRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Véhicule " + vehicleId + " introuvable"));
        Accessory accessory = accessoryRepository.save(accessoryMapper.toEntity(request, vehicle));
        return accessoryMapper.toResponse(accessory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccessoryResponse> getAllAccessoryByVehicle(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new NotFoundException("Véhicule " + vehicleId + " introuvable");
        }
        return accessoryRepository.findByVehicleIdOrderById(vehicleId).stream()
                .map(accessoryMapper::toResponse)
                .toList();
    }

    @Override
    public AccessoryResponse updateAccessory(Long id, AccessoryRequest request) {
        Accessory accessory = findOrThrow(id);
        accessoryMapper.update(accessory, request);
        return accessoryMapper.toResponse(accessory);
    }

    @Override
    public void deleteAccessory(Long id) {
        accessoryRepository.delete(findOrThrow(id));
    }

    private Accessory findOrThrow(Long id) {
        return accessoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Accessoire " + id + " introuvable"));
    }
}