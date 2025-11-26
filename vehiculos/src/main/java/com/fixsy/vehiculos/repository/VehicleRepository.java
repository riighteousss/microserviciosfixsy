package com.fixsy.vehiculos.repository;

import com.fixsy.vehiculos.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUserId(Long userId);
    Optional<Vehicle> findByUserIdAndIsDefaultTrue(Long userId);
    Optional<Vehicle> findByIdAndUserId(Long id, Long userId);
    boolean existsByPlate(String plate);
    long countByUserId(Long userId);
}

