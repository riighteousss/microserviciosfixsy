package com.fixsy.gestionsolicitudes.controller;

import com.fixsy.gestionsolicitudes.dto.ServiceRequestDTO;
import com.fixsy.gestionsolicitudes.dto.ServiceRequestRequestDTO;
import com.fixsy.gestionsolicitudes.services.ServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Service Request Controller", description = "API para gestión de solicitudes de servicio")
public class ServiceRequestController {
    @Autowired
    private ServiceRequestService service;

    @GetMapping
    @Operation(summary = "Obtener todas las solicitudes")
    public ResponseEntity<List<ServiceRequestDTO>> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener solicitud por ID")
    public ResponseEntity<ServiceRequestDTO> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRequestById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener solicitudes por usuario")
    public ResponseEntity<List<ServiceRequestDTO>> getRequestsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getRequestsByUserId(userId));
    }

    @GetMapping("/mechanic/{mechanicName}")
    @Operation(summary = "Obtener solicitudes por mecánico")
    public ResponseEntity<List<ServiceRequestDTO>> getRequestsByMechanicName(@PathVariable String mechanicName) {
        return ResponseEntity.ok(service.getRequestsByMechanicName(mechanicName));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener solicitudes por estado")
    public ResponseEntity<List<ServiceRequestDTO>> getRequestsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.getRequestsByStatus(status));
    }

    @PostMapping
    @Operation(summary = "Crear nueva solicitud")
    public ResponseEntity<ServiceRequestDTO> createRequest(@Valid @RequestBody ServiceRequestRequestDTO requestDTO) {
        return new ResponseEntity<>(service.createRequest(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar solicitud completa")
    public ResponseEntity<ServiceRequestDTO> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequestRequestDTO requestDTO) {
        return ResponseEntity.ok(service.updateRequest(id, requestDTO));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de solicitud")
    public ResponseEntity<ServiceRequestDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(service.updateRequestStatus(id, status));
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Asignar mecánico a solicitud")
    public ResponseEntity<ServiceRequestDTO> assignMechanic(
            @PathVariable Long id,
            @RequestParam String mechanicName) {
        return ResponseEntity.ok(service.assignMechanic(id, mechanicName));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar solicitud")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        service.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}

