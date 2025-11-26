package com.fixsy.vehiculos.service;

import com.fixsy.vehiculos.dto.VehicleDTO;
import com.fixsy.vehiculos.dto.VehicleRequestDTO;
import com.fixsy.vehiculos.model.Vehicle;
import com.fixsy.vehiculos.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        return convertToDTO(vehicle);
    }

    public List<VehicleDTO> getVehiclesByUserId(Long userId) {
        return vehicleRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public VehicleDTO getDefaultVehicle(Long userId) {
        Vehicle vehicle = vehicleRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new RuntimeException("No se encontró vehículo predeterminado"));
        return convertToDTO(vehicle);
    }

    @Transactional
    public VehicleDTO createVehicle(VehicleRequestDTO vehicleRequest) {
        if (vehicleRepository.existsByPlate(vehicleRequest.getPlate())) {
            throw new RuntimeException("La placa ya está registrada");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setUserId(vehicleRequest.getUserId());
        vehicle.setBrand(vehicleRequest.getBrand());
        vehicle.setModel(vehicleRequest.getModel());
        vehicle.setYear(vehicleRequest.getYear());
        vehicle.setPlate(vehicleRequest.getPlate());
        vehicle.setColor(vehicleRequest.getColor());
        vehicle.setIsDefault(vehicleRequest.getIsDefault());

        // Si se marca como predeterminado, quitar el predeterminado de otros vehículos del usuario
        if (vehicleRequest.getIsDefault()) {
            List<Vehicle> userVehicles = vehicleRepository.findByUserId(vehicleRequest.getUserId());
            userVehicles.forEach(v -> v.setIsDefault(false));
            vehicleRepository.saveAll(userVehicles);
        }

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return convertToDTO(savedVehicle);
    }

    @Transactional
    public VehicleDTO updateVehicle(Long id, VehicleRequestDTO vehicleRequest) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        // Verificar que la placa no esté en uso por otro vehículo
        if (!vehicle.getPlate().equals(vehicleRequest.getPlate()) && 
            vehicleRepository.existsByPlate(vehicleRequest.getPlate())) {
            throw new RuntimeException("La placa ya está registrada en otro vehículo");
        }

        vehicle.setBrand(vehicleRequest.getBrand());
        vehicle.setModel(vehicleRequest.getModel());
        vehicle.setYear(vehicleRequest.getYear());
        vehicle.setPlate(vehicleRequest.getPlate());
        vehicle.setColor(vehicleRequest.getColor());

        // Si se marca como predeterminado, quitar el predeterminado de otros vehículos del usuario
        if (vehicleRequest.getIsDefault() && !vehicle.getIsDefault()) {
            List<Vehicle> userVehicles = vehicleRepository.findByUserId(vehicleRequest.getUserId());
            userVehicles.forEach(v -> {
                if (!v.getId().equals(id)) {
                    v.setIsDefault(false);
                }
            });
            vehicleRepository.saveAll(userVehicles);
        }
        vehicle.setIsDefault(vehicleRequest.getIsDefault());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return convertToDTO(updatedVehicle);
    }

    @Transactional
    public VehicleDTO setAsDefault(Long id, Long userId) {
        Vehicle vehicle = vehicleRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado o no pertenece al usuario"));

        // Quitar el predeterminado de otros vehículos del usuario
        List<Vehicle> userVehicles = vehicleRepository.findByUserId(userId);
        userVehicles.forEach(v -> v.setIsDefault(false));
        vehicleRepository.saveAll(userVehicles);

        // Establecer este vehículo como predeterminado
        vehicle.setIsDefault(true);
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return convertToDTO(updatedVehicle);
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new RuntimeException("Vehículo no encontrado");
        }
        vehicleRepository.deleteById(id);
    }

    public Long getVehicleCount(Long userId) {
        return vehicleRepository.countByUserId(userId);
    }

    private VehicleDTO convertToDTO(Vehicle vehicle) {
        return new VehicleDTO(
                vehicle.getId(),
                vehicle.getUserId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getPlate(),
                vehicle.getColor(),
                vehicle.getIsDefault(),
                vehicle.getCreatedAt() != null ? 
                    vehicle.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : 
                    System.currentTimeMillis()
        );
    }
}

