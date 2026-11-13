package com.renault.digital.garage_service.controllers;

import com.renault.digital.garage_service.dto.AccessoryRequest;
import com.renault.digital.garage_service.dto.AccessoryResponse;
import com.renault.digital.garage_service.services.AccessoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccessoryController {

    private final AccessoryService accessoryService;

    @PostMapping("/vehicles/{vehicleId}/accessories")
    @ResponseStatus(HttpStatus.CREATED)
    public AccessoryResponse createAccessory(@PathVariable Long vehicleId, @Valid @RequestBody AccessoryRequest request) {
        return accessoryService.createAccessory(vehicleId, request);
    }

    @GetMapping("/vehicles/{vehicleId}/accessories")
    public List<AccessoryResponse> getAllAccessoryByVehicle(@PathVariable Long vehicleId) {
        return accessoryService.getAllAccessoryByVehicle(vehicleId);
    }

    @PutMapping("/accessories/{id}")
    public AccessoryResponse updateAccessory(@PathVariable Long id, @Valid @RequestBody AccessoryRequest request) {
        return accessoryService.updateAccessory(id, request);
    }

    @DeleteMapping("/accessories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccessory(@PathVariable Long id) {
        accessoryService.deleteAccessory(id);
    }
}