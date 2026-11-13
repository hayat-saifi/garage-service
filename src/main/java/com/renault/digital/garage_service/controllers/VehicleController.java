package com.renault.digital.garage_service.controllers;

import com.renault.digital.garage_service.dto.PageResponse;
import com.renault.digital.garage_service.dto.VehicleRequest;
import com.renault.digital.garage_service.dto.VehicleResponse;
import com.renault.digital.garage_service.services.VehicleService;
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

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    /** Ajoute un véhicule au garage (409 si le quota de 50 véhicules est atteint). */
    @PostMapping("/garages/{garageId}/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse create(@PathVariable Long garageId, @Valid @RequestBody VehicleRequest request) {
        return vehicleService.createVehicle(garageId, request);
    }

    @GetMapping("/garages/{garageId}/vehicles")
    public List<VehicleResponse> getVehiclesByGarage(@PathVariable Long garageId) {
        return vehicleService.getVehiclesByGarage(garageId);
    }

    /** Véhicules d'un modèle donné dans tous les garages : ?model=Clio */
    @GetMapping("/vehicles")
    public PageResponse<VehicleResponse> getVehiclesByModel(@RequestParam(required = false) String model,
                                                     @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return vehicleService.getVehiclesByModel(model, pageable);
    }

    @GetMapping("/vehicles/{id}")
    public VehicleResponse getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    @PutMapping("/vehicles/{id}")
    public VehicleResponse updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return vehicleService.updateVehicle(id, request);
    }

    @DeleteMapping("/vehicles/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }
}