package com.fixsy.vehiculos.service;

import com.fixsy.vehiculos.dto.VehicleDTO;
import com.fixsy.vehiculos.dto.VehicleRequestDTO;
import com.fixsy.vehiculos.model.Vehicle;
import com.fixsy.vehiculos.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle testVehicle;
    private VehicleRequestDTO testVehicleRequest;

    @BeforeEach
    void setUp() {
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setUserId(1L);
        testVehicle.setBrand("Toyota");
        testVehicle.setModel("Corolla");
        testVehicle.setYear(2020);
        testVehicle.setPlate("ABC123");
        testVehicle.setColor("Blanco");
        testVehicle.setIsDefault(true);
        testVehicle.setCreatedAt(LocalDateTime.now());
        testVehicle.setUpdatedAt(LocalDateTime.now());

        testVehicleRequest = new VehicleRequestDTO();
        testVehicleRequest.setUserId(1L);
        testVehicleRequest.setBrand("Toyota");
        testVehicleRequest.setModel("Corolla");
        testVehicleRequest.setYear(2020);
        testVehicleRequest.setPlate("ABC123");
        testVehicleRequest.setColor("Blanco");
        testVehicleRequest.setIsDefault(true);
    }

    @Test
    @DisplayName("Debe obtener todos los vehículos")
    void getAllVehicles_ShouldReturnAllVehicles() {
        // Arrange
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setId(2L);
        vehicle2.setUserId(1L);
        vehicle2.setBrand("Honda");
        vehicle2.setModel("Civic");
        vehicle2.setYear(2021);
        vehicle2.setPlate("XYZ789");
        vehicle2.setColor("Negro");
        vehicle2.setIsDefault(false);
        vehicle2.setCreatedAt(LocalDateTime.now());

        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(testVehicle, vehicle2));

        // Act
        List<VehicleDTO> result = vehicleService.getAllVehicles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener vehículo por ID")
    void getVehicleById_ShouldReturnVehicle_WhenExists() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // Act
        VehicleDTO result = vehicleService.getVehicleById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Toyota", result.getBrand());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando vehículo no existe")
    void getVehicleById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> vehicleService.getVehicleById(99L));
        assertEquals("Vehículo no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Debe obtener vehículos por usuario")
    void getVehiclesByUserId_ShouldReturnUserVehicles() {
        // Arrange
        when(vehicleRepository.findByUserId(1L)).thenReturn(Collections.singletonList(testVehicle));

        // Act
        List<VehicleDTO> result = vehicleService.getVehiclesByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    @DisplayName("Debe obtener vehículo predeterminado")
    void getDefaultVehicle_ShouldReturnDefaultVehicle() {
        // Arrange
        when(vehicleRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.of(testVehicle));

        // Act
        VehicleDTO result = vehicleService.getDefaultVehicle(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsDefault());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no hay vehículo predeterminado")
    void getDefaultVehicle_ShouldThrowException_WhenNoDefault() {
        // Arrange
        when(vehicleRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> vehicleService.getDefaultVehicle(1L));
        assertEquals("No se encontró vehículo predeterminado", exception.getMessage());
    }

    @Test
    @DisplayName("Debe crear vehículo correctamente")
    void createVehicle_ShouldCreateVehicle() {
        // Arrange
        when(vehicleRepository.existsByPlate("ABC123")).thenReturn(false);
        when(vehicleRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        VehicleDTO result = vehicleService.createVehicle(testVehicleRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Toyota", result.getBrand());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando placa ya existe")
    void createVehicle_ShouldThrowException_WhenPlateExists() {
        // Arrange
        when(vehicleRepository.existsByPlate("ABC123")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> vehicleService.createVehicle(testVehicleRequest));
        assertEquals("La placa ya está registrada", exception.getMessage());
    }

    @Test
    @DisplayName("Debe actualizar vehículo correctamente")
    void updateVehicle_ShouldUpdateVehicle() {
        // Arrange
        VehicleRequestDTO updateRequest = new VehicleRequestDTO();
        updateRequest.setUserId(1L);
        updateRequest.setBrand("Honda");
        updateRequest.setModel("Civic");
        updateRequest.setYear(2021);
        updateRequest.setPlate("ABC123");
        updateRequest.setColor("Negro");
        updateRequest.setIsDefault(false);

        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setId(1L);
        updatedVehicle.setUserId(1L);
        updatedVehicle.setBrand("Honda");
        updatedVehicle.setModel("Civic");
        updatedVehicle.setYear(2021);
        updatedVehicle.setPlate("ABC123");
        updatedVehicle.setColor("Negro");
        updatedVehicle.setIsDefault(false);
        updatedVehicle.setCreatedAt(LocalDateTime.now());

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(updatedVehicle);

        // Act
        VehicleDTO result = vehicleService.updateVehicle(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Honda", result.getBrand());
        assertEquals("Civic", result.getModel());
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar con placa duplicada")
    void updateVehicle_ShouldThrowException_WhenPlateExistsOnOtherVehicle() {
        // Arrange
        VehicleRequestDTO updateRequest = new VehicleRequestDTO();
        updateRequest.setUserId(1L);
        updateRequest.setBrand("Honda");
        updateRequest.setModel("Civic");
        updateRequest.setYear(2021);
        updateRequest.setPlate("XYZ789"); // Placa diferente
        updateRequest.setColor("Negro");
        updateRequest.setIsDefault(false);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.existsByPlate("XYZ789")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> vehicleService.updateVehicle(1L, updateRequest));
        assertEquals("La placa ya está registrada en otro vehículo", exception.getMessage());
    }

    @Test
    @DisplayName("Debe eliminar vehículo correctamente")
    void deleteVehicle_ShouldDeleteVehicle() {
        // Arrange
        when(vehicleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vehicleRepository).deleteById(1L);

        // Act
        vehicleService.deleteVehicle(1L);

        // Assert
        verify(vehicleRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar vehículo inexistente")
    void deleteVehicle_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(vehicleRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> vehicleService.deleteVehicle(99L));
        assertEquals("Vehículo no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Debe obtener conteo de vehículos por usuario")
    void getVehicleCount_ShouldReturnCount() {
        // Arrange
        when(vehicleRepository.countByUserId(1L)).thenReturn(3L);

        // Act
        Long result = vehicleService.getVehicleCount(1L);

        // Assert
        assertEquals(3L, result);
    }

    @Test
    @DisplayName("Debe establecer vehículo como predeterminado")
    void setAsDefault_ShouldSetVehicleAsDefault() {
        // Arrange
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setId(2L);
        vehicle2.setUserId(1L);
        vehicle2.setIsDefault(true);
        vehicle2.setCreatedAt(LocalDateTime.now());

        testVehicle.setIsDefault(false);

        when(vehicleRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.findByUserId(1L)).thenReturn(Arrays.asList(testVehicle, vehicle2));
        when(vehicleRepository.saveAll(any())).thenReturn(Arrays.asList(testVehicle, vehicle2));
        
        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(1L);
        savedVehicle.setUserId(1L);
        savedVehicle.setIsDefault(true);
        savedVehicle.setBrand("Toyota");
        savedVehicle.setModel("Corolla");
        savedVehicle.setYear(2020);
        savedVehicle.setPlate("ABC123");
        savedVehicle.setColor("Blanco");
        savedVehicle.setCreatedAt(LocalDateTime.now());
        
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        // Act
        VehicleDTO result = vehicleService.setAsDefault(1L, 1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsDefault());
    }

    @Test
    @DisplayName("Debe lanzar excepción al establecer predeterminado en vehículo de otro usuario")
    void setAsDefault_ShouldThrowException_WhenVehicleNotOwnedByUser() {
        // Arrange
        when(vehicleRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> vehicleService.setAsDefault(1L, 2L));
        assertEquals("Vehículo no encontrado o no pertenece al usuario", exception.getMessage());
    }
}

