package com.renault.digital.garage_service.repositories;

import com.renault.digital.garage_service.entities.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

 long countByGarageId(Long garageId);

 List<Vehicle> findByGarageIdOrderById(Long garageId);

 Page<Vehicle> findByModelIgnoreCase(String model, Pageable pageable);

}
