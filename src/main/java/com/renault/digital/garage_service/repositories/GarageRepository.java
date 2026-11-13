package com.renault.digital.garage_service.repositories;

import com.renault.digital.garage_service.entities.Garage;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GarageRepository extends JpaRepository<Garage, Long>, JpaSpecificationExecutor<Garage> {

    /**
     * Charge le garage avec un verrou en écriture : sérialise les ajouts concurrents
     * de véhicules pour que le quota ne puisse pas être dépassé.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from Garage g where g.id = :id")
    Optional<Garage> findByIdForUpdate(@Param("id") Long id);
}
