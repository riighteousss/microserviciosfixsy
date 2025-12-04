package com.fixsy.vehiculos.controller;

import com.fixsy.vehiculos.dto.VehicleDTO;
import com.fixsy.vehiculos.dto.VehicleRequestDTO;
import com.fixsy.vehiculos.service.VehicleService;
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
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicle Controller", description = "API para gestión de vehículos")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    @Operation(summary = "Obtener todos los vehículos", description = "Retorna una lista con todos los vehículos registrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de vehículos obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener vehículo por ID", description = "Busca y retorna un vehículo específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehículo encontrado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<VehicleDTO> getVehicleById(
            @Parameter(description = "ID del vehículo", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener vehículos de un usuario", description = "Retorna todos los vehículos asociados a un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de vehículos del usuario obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<VehicleDTO>> getVehiclesByUserId(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByUserId(userId));
    }

    @GetMapping("/user/{userId}/default")
    @Operation(summary = "Obtener vehículo predeterminado de un usuario", description = "Retorna el vehículo marcado como predeterminado del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehículo predeterminado encontrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))),
        @ApiResponse(responseCode = "404", description = "No se encontró vehículo predeterminado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<VehicleDTO> getDefaultVehicle(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(vehicleService.getDefaultVehicle(userId));
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Obtener cantidad de vehículos de un usuario", description = "Retorna el número total de vehículos de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cantidad obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Long> getVehicleCount(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(vehicleService.getVehicleCount(userId));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo vehículo", description = "Registra un nuevo vehículo en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Vehículo creado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "La placa ya está registrada",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<VehicleDTO> createVehicle(
            @Valid @RequestBody VehicleRequestDTO vehicleRequest) {
        return new ResponseEntity<>(vehicleService.createVehicle(vehicleRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vehículo", description = "Actualiza los datos de un vehículo existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehículo actualizado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "La placa ya está registrada en otro vehículo",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<VehicleDTO> updateVehicle(
            @Parameter(description = "ID del vehículo", required = true, example = "1")
            @PathVariable Long id, 
            @Valid @RequestBody VehicleRequestDTO vehicleRequest) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleRequest));
    }

    @PutMapping("/{id}/set-default")
    @Operation(summary = "Establecer vehículo como predeterminado", description = "Marca un vehículo como el predeterminado del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehículo establecido como predeterminado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado o no pertenece al usuario",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<VehicleDTO> setAsDefault(
            @Parameter(description = "ID del vehículo", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @RequestParam Long userId) {
        return ResponseEntity.ok(vehicleService.setAsDefault(id, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar vehículo", description = "Elimina un vehículo del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vehículo eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> deleteVehicle(
            @Parameter(description = "ID del vehículo", required = true, example = "1")
            @PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
