package com.fixsy.vehiculos.controller;

import com.fixsy.vehiculos.dto.VehicleDTO;
import com.fixsy.vehiculos.dto.VehicleRequestDTO;
import com.fixsy.vehiculos.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Obtener todos los vehículos")
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener vehículo por ID")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener vehículos de un usuario")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByUserId(userId));
    }

    @GetMapping("/user/{userId}/default")
    @Operation(summary = "Obtener vehículo predeterminado de un usuario")
    public ResponseEntity<VehicleDTO> getDefaultVehicle(@PathVariable Long userId) {
        return ResponseEntity.ok(vehicleService.getDefaultVehicle(userId));
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Obtener cantidad de vehículos de un usuario")
    public ResponseEntity<Long> getVehicleCount(@PathVariable Long userId) {
        return ResponseEntity.ok(vehicleService.getVehicleCount(userId));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo vehículo")
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody VehicleRequestDTO vehicleRequest) {
        return new ResponseEntity<>(vehicleService.createVehicle(vehicleRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vehículo")
    public ResponseEntity<VehicleDTO> updateVehicle(
            @PathVariable Long id, 
            @Valid @RequestBody VehicleRequestDTO vehicleRequest) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleRequest));
    }

    @PutMapping("/{id}/set-default")
    @Operation(summary = "Establecer vehículo como predeterminado")
    public ResponseEntity<VehicleDTO> setAsDefault(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(vehicleService.setAsDefault(id, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar vehículo")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}

