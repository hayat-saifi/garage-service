package com.renault.digital.garage_service.specifications;

import com.renault.digital.garage_service.entities.Accessory;
import com.renault.digital.garage_service.enums.FuelType;
import com.renault.digital.garage_service.entities.Garage;
import com.renault.digital.garage_service.entities.Vehicle;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

/**
 * Critères de recherche des garages.
 */
public final class GarageSpecification {

    private GarageSpecification() {
    }

    /** Garages qui ont au moins un véhicule du type de carburant demandé. */
    public static Specification<Garage> hasFuelType(FuelType fuelType) {
        return (root, query, cb) -> {
            if (fuelType == null) {
                return cb.conjunction();
            }
            query.distinct(true);
            Join<Garage, Vehicle> vehicles = root.join("vehicles", JoinType.INNER);
            return cb.equal(vehicles.get("typeCarburant"), fuelType);
        };
    }

    /** Garages dont au moins un véhicule possède l'accessoire demandé (nom). */
    public static Specification<Garage> hasAccessory(String accessoryName) {
        return (root, query, cb) -> {
            if (accessoryName == null || accessoryName.isBlank()) {
                return cb.conjunction();
            }
            query.distinct(true);
            Join<Garage, Vehicle> vehicles = root.join("vehicles", JoinType.INNER);
            Join<Vehicle, Accessory> accessories = vehicles.join("accessories", JoinType.INNER);
            return cb.equal(cb.lower(accessories.get("nom")), accessoryName.trim().toLowerCase());
        };
    }
}