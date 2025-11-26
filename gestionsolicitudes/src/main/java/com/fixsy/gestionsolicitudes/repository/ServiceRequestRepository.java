package com.fixsy.gestionsolicitudes.repository;

import com.fixsy.gestionsolicitudes.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByUserId(Long userId);
    List<ServiceRequest> findByStatus(String status);
    List<ServiceRequest> findByUserIdAndStatus(Long userId, String status);
    List<ServiceRequest> findByMechanicAssigned(String mechanicAssigned);
}

