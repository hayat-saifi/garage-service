package com.renault.digital.garage_service.services;

import com.renault.digital.garage_service.dto.PageResponse;
import com.renault.digital.garage_service.dto.VehicleRequest;
import com.renault.digital.garage_service.dto.VehicleResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleService {

    VehicleResponse createVehicle(Long garageId, VehicleRequest request);

    VehicleResponse getVehicleById(Long id);

    List<VehicleResponse> getVehiclesByGarage(Long garageId);

    /** Véhicules d'un modèle donné, tous garages confondus ; sans modèle, tous les véhicules. */
    PageResponse<VehicleResponse> getVehiclesByModel(String model, Pageable pageable);

    VehicleResponse updateVehicle(Long id, VehicleRequest request);

    void deleteVehicle(Long id);
}