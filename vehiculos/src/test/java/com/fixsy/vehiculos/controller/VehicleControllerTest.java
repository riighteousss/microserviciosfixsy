package com.fixsy.vehiculos.controller;

import com.fixsy.vehiculos.dto.VehicleDTO;
import com.fixsy.vehiculos.dto.VehicleRequestDTO;
import com.fixsy.vehiculos.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private VehicleDTO testVehicleDTO;
    private VehicleRequestDTO testVehicleRequestDTO;

    @BeforeEach
    void setUp() {
        testVehicleDTO = new VehicleDTO();
        testVehicleDTO.setId(1L);
        testVehicleDTO.setUserId(1L);
        testVehicleDTO.setBrand("Toyota");
        testVehicleDTO.setModel("Corolla");
        testVehicleDTO.setYear(2020);
        testVehicleDTO.setPlate("ABC123");
        testVehicleDTO.setColor("Blanco");
        testVehicleDTO.setIsDefault(true);

        testVehicleRequestDTO = new VehicleRequestDTO();
        testVehicleRequestDTO.setUserId(1L);
        testVehicleRequestDTO.setBrand("Toyota");
        testVehicleRequestDTO.setModel("Corolla");
        testVehicleRequestDTO.setYear(2020);
        testVehicleRequestDTO.setPlate("ABC123");
        testVehicleRequestDTO.setColor("Blanco");
        testVehicleRequestDTO.setIsDefault(true);
    }

    @Test
    @DisplayName("GET /api/vehicles - Debe retornar lista de vehículos")
    void getAllVehicles_ShouldReturnListOfVehicles() {
        // Arrange
        VehicleDTO vehicle2 = new VehicleDTO();
        vehicle2.setId(2L);
        vehicle2.setBrand("Honda");
        vehicle2.setModel("Civic");

        when(vehicleService.getAllVehicles()).thenReturn(Arrays.asList(testVehicleDTO, vehicle2));

        // Act
        ResponseEntity<List<VehicleDTO>> response = vehicleController.getAllVehicles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(vehicleService, times(1)).getAllVehicles();
    }

    @Test
    @DisplayName("GET /api/vehicles/{id} - Debe retornar vehículo por ID")
    void getVehicleById_ShouldReturnVehicle() {
        // Arrange
        when(vehicleService.getVehicleById(1L)).thenReturn(testVehicleDTO);

        // Act
        ResponseEntity<VehicleDTO> response = vehicleController.getVehicleById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Toyota", response.getBody().getBrand());
    }

    @Test
    @DisplayName("GET /api/vehicles/user/{userId} - Debe retornar vehículos por usuario")
    void getVehiclesByUserId_ShouldReturnUserVehicles() {
        // Arrange
        when(vehicleService.getVehiclesByUserId(1L)).thenReturn(Arrays.asList(testVehicleDTO));

        // Act
        ResponseEntity<List<VehicleDTO>> response = vehicleController.getVehiclesByUserId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getUserId());
    }

    @Test
    @DisplayName("GET /api/vehicles/user/{userId}/default - Debe retornar vehículo predeterminado")
    void getDefaultVehicle_ShouldReturnDefaultVehicle() {
        // Arrange
        when(vehicleService.getDefaultVehicle(1L)).thenReturn(testVehicleDTO);

        // Act
        ResponseEntity<VehicleDTO> response = vehicleController.getDefaultVehicle(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getIsDefault());
    }

    @Test
    @DisplayName("GET /api/vehicles/user/{userId}/count - Debe retornar cantidad de vehículos")
    void getVehicleCount_ShouldReturnCount() {
        // Arrange
        when(vehicleService.getVehicleCount(1L)).thenReturn(3L);

        // Act
        ResponseEntity<Long> response = vehicleController.getVehicleCount(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody());
    }

    @Test
    @DisplayName("POST /api/vehicles - Debe crear vehículo correctamente")
    void createVehicle_ShouldReturnCreatedVehicle() {
        // Arrange
        when(vehicleService.createVehicle(any(VehicleRequestDTO.class))).thenReturn(testVehicleDTO);

        // Act
        ResponseEntity<VehicleDTO> response = vehicleController.createVehicle(testVehicleRequestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Toyota", response.getBody().getBrand());
    }

    @Test
    @DisplayName("PUT /api/vehicles/{id} - Debe actualizar vehículo")
    void updateVehicle_ShouldReturnUpdatedVehicle() {
        // Arrange
        VehicleDTO updatedDTO = new VehicleDTO();
        updatedDTO.setId(1L);
        updatedDTO.setBrand("Honda");
        updatedDTO.setModel("Civic");

        when(vehicleService.updateVehicle(anyLong(), any(VehicleRequestDTO.class))).thenReturn(updatedDTO);

        // Act
        ResponseEntity<VehicleDTO> response = vehicleController.updateVehicle(1L, testVehicleRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Honda", response.getBody().getBrand());
    }

    @Test
    @DisplayName("PUT /api/vehicles/{id}/set-default - Debe establecer vehículo como predeterminado")
    void setAsDefault_ShouldReturnUpdatedVehicle() {
        // Arrange
        when(vehicleService.setAsDefault(1L, 1L)).thenReturn(testVehicleDTO);

        // Act
        ResponseEntity<VehicleDTO> response = vehicleController.setAsDefault(1L, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getIsDefault());
    }

    @Test
    @DisplayName("DELETE /api/vehicles/{id} - Debe eliminar vehículo")
    void deleteVehicle_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(vehicleService).deleteVehicle(1L);

        // Act
        ResponseEntity<Void> response = vehicleController.deleteVehicle(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(vehicleService, times(1)).deleteVehicle(1L);
    }
}

