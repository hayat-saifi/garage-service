package com.renault.digital.garage_service.mapper;

import com.renault.digital.garage_service.dto.AccessoryRequest;
import com.renault.digital.garage_service.dto.AccessoryResponse;
import com.renault.digital.garage_service.entities.Accessory;
import com.renault.digital.garage_service.entities.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class AccessoryMapper {

    public Accessory toEntity(AccessoryRequest request, Vehicle vehicle) {
        Accessory accessory = new Accessory();
        accessory.setVehicle(vehicle);
        update(accessory, request);
        return accessory;
    }

    public void update(Accessory accessory, AccessoryRequest request) {
        accessory.setNom(request.nom().trim());
        accessory.setDescription(request.description());
        accessory.setPrix(request.prix());
        accessory.setType(request.type().trim());
    }

    public AccessoryResponse toResponse(Accessory accessory) {
        return new AccessoryResponse(
                accessory.getId(),
                accessory.getVehicle().getId(),
                accessory.getNom(),
                accessory.getDescription(),
                accessory.getPrix(),
                accessory.getType());
    }
}
