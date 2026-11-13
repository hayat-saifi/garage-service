package com.renault.digital.garage_service.services;

import com.renault.digital.garage_service.dto.GarageRequest;
import com.renault.digital.garage_service.dto.GarageResponse;
import com.renault.digital.garage_service.dto.PageResponse;
import com.renault.digital.garage_service.enums.FuelType;
import org.springframework.data.domain.Pageable;

public interface GarageService {

    GarageResponse createGarage(GarageRequest request);

    GarageResponse getGarageById(Long id);

    PageResponse<GarageResponse> getAllGarages(Pageable pageable);

    GarageResponse updateGarage(Long id, GarageRequest request);

    void deleteGarage(Long id);

    PageResponse<GarageResponse> searchGarages(FuelType fuelType, String accessory, Pageable pageable);
}
