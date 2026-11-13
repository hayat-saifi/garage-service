package com.renault.digital.garage_service.services;

import com.renault.digital.garage_service.dto.AccessoryRequest;
import com.renault.digital.garage_service.dto.AccessoryResponse;

import java.util.List;

public interface AccessoryService {

    AccessoryResponse createAccessory(Long vehicleId, AccessoryRequest request);

    List<AccessoryResponse> getAllAccessoryByVehicle(Long vehicleId);

    AccessoryResponse updateAccessory(Long id, AccessoryRequest request);

    void deleteAccessory(Long id);
}