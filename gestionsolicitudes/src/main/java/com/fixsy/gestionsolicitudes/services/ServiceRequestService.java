package com.fixsy.gestionsolicitudes.services;

import com.fixsy.gestionsolicitudes.dto.ServiceRequestDTO;
import com.fixsy.gestionsolicitudes.dto.ServiceRequestRequestDTO;
import com.fixsy.gestionsolicitudes.model.ServiceRequest;
import com.fixsy.gestionsolicitudes.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRequestService {
    @Autowired
    private ServiceRequestRepository repository;

    public List<ServiceRequestDTO> getAllRequests() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ServiceRequestDTO getRequestById(Long id) {
        ServiceRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        return convertToDTO(request);
    }

    public List<ServiceRequestDTO> getRequestsByUserId(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceRequestDTO> getRequestsByMechanicName(String mechanicName) {
        return repository.findByMechanicAssigned(mechanicName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceRequestDTO> getRequestsByStatus(String status) {
        return repository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ServiceRequestDTO createRequest(ServiceRequestRequestDTO requestDTO) {
        ServiceRequest request = new ServiceRequest();
        request.setUserId(requestDTO.getUserId());
        request.setServiceType(requestDTO.getServiceType());
        request.setVehicleInfo(requestDTO.getVehicleInfo());
        request.setDescription(requestDTO.getDescription());
        request.setImages(requestDTO.getImages() != null ? requestDTO.getImages() : "");
        request.setLocation(requestDTO.getLocation() != null ? requestDTO.getLocation() : "");
        request.setNotes(requestDTO.getNotes() != null ? requestDTO.getNotes() : "");
        request.setStatus("Pendiente");

        ServiceRequest saved = repository.save(request);
        return convertToDTO(saved);
    }

    public ServiceRequestDTO updateRequest(Long id, ServiceRequestRequestDTO requestDTO) {
        ServiceRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        // Actualizar todos los campos editables
        request.setServiceType(requestDTO.getServiceType());
        request.setVehicleInfo(requestDTO.getVehicleInfo());
        request.setDescription(requestDTO.getDescription());
        if (requestDTO.getImages() != null && !requestDTO.getImages().isEmpty()) {
            request.setImages(requestDTO.getImages());
        }
        if (requestDTO.getLocation() != null) {
            request.setLocation(requestDTO.getLocation());
        }
        if (requestDTO.getNotes() != null) {
            request.setNotes(requestDTO.getNotes());
        }
        
        ServiceRequest updated = repository.save(request);
        return convertToDTO(updated);
    }

    public ServiceRequestDTO updateRequestStatus(Long id, String status) {
        ServiceRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        request.setStatus(status);
        ServiceRequest updated = repository.save(request);
        return convertToDTO(updated);
    }

    public ServiceRequestDTO assignMechanic(Long id, String mechanicName) {
        ServiceRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        request.setMechanicAssigned(mechanicName);
        request.setStatus("En Proceso");
        ServiceRequest updated = repository.save(request);
        return convertToDTO(updated);
    }

    public void deleteRequest(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Solicitud no encontrada");
        }
        repository.deleteById(id);
    }

    private ServiceRequestDTO convertToDTO(ServiceRequest request) {
        return new ServiceRequestDTO(
                request.getId(),
                request.getUserId(),
                request.getServiceType(),
                request.getVehicleInfo(),
                request.getDescription(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getImages(),
                request.getMechanicAssigned(),
                request.getEstimatedCost(),
                request.getLocation(),
                request.getNotes()
        );
    }
}

