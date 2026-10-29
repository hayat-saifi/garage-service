package com.renault.digital.garage_service.specifications;

import com.renault.digital.garage_service.entities.Accessory;
import com.renault.digital.garage_service.entities.Garage;
import com.renault.digital.garage_service.entities.Vehicle;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class GarageSpecifcations {
    public static Specification<Garage> queryInAllFields(String q) {
        return ((root, query, cb) -> {
            if (q == null || q.isBlank())
                return cb.conjunction();

            String like = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("address")), like),
                    cb.like(cb.lower(root.get("email")), like),
                    cb.like(cb.lower(root.get("telephone")), like)
            );

        });

    }

    public static Specification<Garage> hasVehicleType(String vehicleType) {
        return ((root, query, cb) -> {
            if (vehicleType == null || vehicleType.isBlank())
                return cb.conjunction();

            query.distinct(true);
            Join<Garage, Vehicle> v = root.join("vehicles", JoinType.INNER);

            return cb.equal(cb.lower((v.get("fuelType"))), vehicleType.toLowerCase()
            );

        });

    }

    public static Specification<Garage> hasAccessory(String accessoryQuery) {
        return ((root, query, cb) -> {
            if (accessoryQuery == null || accessoryQuery.isBlank())
                return cb.conjunction();

            query.distinct(true);
            Join<Garage, Vehicle> v = root.join("vehicles", JoinType.INNER);
            Join<Vehicle, Accessory> ac = v.join("accessories", JoinType.INNER);

            String like = "%" + accessoryQuery.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("type")), like)
            );

        });
    }

    public static Specification<Garage> searchByVehicleTypeAndAccessory(String vehicleType, String accessoryQuery) {

        Specification<Garage> spec = (root, query, cb) -> null;

        if (vehicleType != null) {
            spec = spec.and(hasVehicleType(vehicleType));
        }
        if (accessoryQuery != null) {
            spec = spec.and(hasAccessory(accessoryQuery));
        }

        return spec;
    }

}


