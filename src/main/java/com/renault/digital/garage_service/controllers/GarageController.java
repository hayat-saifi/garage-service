package com.renault.digital.garage_service.controllers;

import com.renault.digital.garage_service.dto.GarageRequest;
import com.renault.digital.garage_service.dto.GarageResponse;
import com.renault.digital.garage_service.dto.PageResponse;
import com.renault.digital.garage_service.enums.FuelType;
import com.renault.digital.garage_service.services.GarageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/garages")
@RequiredArgsConstructor
public class GarageController {

    private final GarageService garageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GarageResponse createGarage(@Valid @RequestBody GarageRequest request) {
        return garageService.createGarage(request);
    }


    @GetMapping
    public PageResponse<GarageResponse> getAllGarages(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return garageService.getAllGarages(pageable);
    }

    // Recherche par fuelType et accessory
    @GetMapping("/search")
    public PageResponse<GarageResponse> searchGarage(@RequestParam(required = false) FuelType fuelType,
                                               @RequestParam(required = false) String accessory,
                                               @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return garageService.searchGarages(fuelType, accessory, pageable);
    }

    @GetMapping("/{id}")
    public GarageResponse getGarageById(@PathVariable Long id) {
        return garageService.getGarageById(id);
    }

    @PutMapping("/{id}")
    public GarageResponse updateGarage(@PathVariable Long id, @Valid @RequestBody GarageRequest request) {
        return garageService.updateGarage(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGarage(@PathVariable Long id) {
        garageService.deleteGarage(id);
    }
}