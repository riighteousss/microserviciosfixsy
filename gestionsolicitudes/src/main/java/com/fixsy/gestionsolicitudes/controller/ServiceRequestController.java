package com.fixsy.gestionsolicitudes.controller;

import com.fixsy.gestionsolicitudes.dto.ServiceRequestDTO;
import com.fixsy.gestionsolicitudes.dto.ServiceRequestRequestDTO;
import com.fixsy.gestionsolicitudes.services.ServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Obtener todas las solicitudes", description = "Retorna una lista con todas las solicitudes de servicio")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de solicitudes obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<ServiceRequestDTO>> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener solicitud por ID", description = "Busca y retorna una solicitud específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud encontrada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestDTO.class))),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ServiceRequestDTO> getRequestById(
            @Parameter(description = "ID de la solicitud", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getRequestById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener solicitudes por usuario", description = "Retorna todas las solicitudes de un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de solicitudes del usuario obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<ServiceRequestDTO>> getRequestsByUserId(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(service.getRequestsByUserId(userId));
    }

    @GetMapping("/mechanic/{mechanicName}")
    @Operation(summary = "Obtener solicitudes por mecánico", description = "Retorna todas las solicitudes asignadas a un mecánico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de solicitudes del mecánico obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<ServiceRequestDTO>> getRequestsByMechanicName(
            @Parameter(description = "Nombre del mecánico", required = true, example = "Juan Pérez")
            @PathVariable String mechanicName) {
        return ResponseEntity.ok(service.getRequestsByMechanicName(mechanicName));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener solicitudes por estado", description = "Retorna todas las solicitudes con un estado específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de solicitudes filtradas por estado obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<ServiceRequestDTO>> getRequestsByStatus(
            @Parameter(description = "Estado de la solicitud", required = true, example = "Pendiente",
                    schema = @Schema(allowableValues = {"Pendiente", "En Proceso", "Completado", "Cancelado"}))
            @PathVariable String status) {
        return ResponseEntity.ok(service.getRequestsByStatus(status));
    }

    @PostMapping
    @Operation(summary = "Crear nueva solicitud", description = "Registra una nueva solicitud de servicio")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ServiceRequestDTO> createRequest(
            @Valid @RequestBody ServiceRequestRequestDTO requestDTO) {
        return new ResponseEntity<>(service.createRequest(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar solicitud completa", description = "Actualiza todos los datos de una solicitud existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud actualizada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ServiceRequestDTO> updateRequest(
            @Parameter(description = "ID de la solicitud", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequestRequestDTO requestDTO) {
        return ResponseEntity.ok(service.updateRequest(id, requestDTO));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de solicitud", description = "Cambia únicamente el estado de una solicitud")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestDTO.class))),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ServiceRequestDTO> updateStatus(
            @Parameter(description = "ID de la solicitud", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado", required = true, example = "En Proceso",
                    schema = @Schema(allowableValues = {"Pendiente", "En Proceso", "Completado", "Cancelado"}))
            @RequestParam String status) {
        return ResponseEntity.ok(service.updateRequestStatus(id, status));
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Asignar mecánico a solicitud", description = "Asigna un mecánico a una solicitud y cambia su estado a 'En Proceso'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mecánico asignado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceRequestDTO.class))),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ServiceRequestDTO> assignMechanic(
            @Parameter(description = "ID de la solicitud", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nombre del mecánico", required = true, example = "Juan Pérez")
            @RequestParam String mechanicName) {
        return ResponseEntity.ok(service.assignMechanic(id, mechanicName));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar solicitud", description = "Elimina una solicitud del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Solicitud eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> deleteRequest(
            @Parameter(description = "ID de la solicitud", required = true, example = "1")
            @PathVariable Long id) {
        service.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}
