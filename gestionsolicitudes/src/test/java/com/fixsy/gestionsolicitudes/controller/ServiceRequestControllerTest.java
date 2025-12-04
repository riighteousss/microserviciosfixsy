package com.fixsy.gestionsolicitudes.controller;

import com.fixsy.gestionsolicitudes.dto.ServiceRequestDTO;
import com.fixsy.gestionsolicitudes.dto.ServiceRequestRequestDTO;
import com.fixsy.gestionsolicitudes.services.ServiceRequestService;
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
class ServiceRequestControllerTest {

    @Mock
    private ServiceRequestService serviceRequestService;

    @InjectMocks
    private ServiceRequestController serviceRequestController;

    private ServiceRequestDTO testRequestDTO;
    private ServiceRequestRequestDTO testRequestRequestDTO;

    @BeforeEach
    void setUp() {
        testRequestDTO = new ServiceRequestDTO();
        testRequestDTO.setId(1L);
        testRequestDTO.setUserId(1L);
        testRequestDTO.setServiceType("Emergencia");
        testRequestDTO.setVehicleInfo("Toyota Corolla 2020 - ABC123");
        testRequestDTO.setDescription("El auto no enciende");
        testRequestDTO.setStatus("Pendiente");
        testRequestDTO.setLocation("Bogotá");

        testRequestRequestDTO = new ServiceRequestRequestDTO();
        testRequestRequestDTO.setUserId(1L);
        testRequestRequestDTO.setServiceType("Emergencia");
        testRequestRequestDTO.setVehicleInfo("Toyota Corolla 2020 - ABC123");
        testRequestRequestDTO.setDescription("El auto no enciende");
        testRequestRequestDTO.setLocation("Bogotá");
    }

    @Test
    @DisplayName("GET /api/requests - Debe retornar lista de solicitudes")
    void getAllRequests_ShouldReturnListOfRequests() {
        // Arrange
        ServiceRequestDTO request2 = new ServiceRequestDTO();
        request2.setId(2L);
        request2.setUserId(2L);
        request2.setServiceType("Mantenimiento");
        request2.setStatus("En Proceso");

        when(serviceRequestService.getAllRequests()).thenReturn(Arrays.asList(testRequestDTO, request2));

        // Act
        ResponseEntity<List<ServiceRequestDTO>> response = serviceRequestController.getAllRequests();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(serviceRequestService, times(1)).getAllRequests();
    }

    @Test
    @DisplayName("GET /api/requests/{id} - Debe retornar solicitud por ID")
    void getRequestById_ShouldReturnRequest() {
        // Arrange
        when(serviceRequestService.getRequestById(1L)).thenReturn(testRequestDTO);

        // Act
        ResponseEntity<ServiceRequestDTO> response = serviceRequestController.getRequestById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Emergencia", response.getBody().getServiceType());
    }

    @Test
    @DisplayName("GET /api/requests/user/{userId} - Debe retornar solicitudes por usuario")
    void getRequestsByUserId_ShouldReturnUserRequests() {
        // Arrange
        when(serviceRequestService.getRequestsByUserId(1L)).thenReturn(Arrays.asList(testRequestDTO));

        // Act
        ResponseEntity<List<ServiceRequestDTO>> response = serviceRequestController.getRequestsByUserId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getUserId());
    }

    @Test
    @DisplayName("GET /api/requests/mechanic/{mechanicName} - Debe retornar solicitudes por mecánico")
    void getRequestsByMechanicName_ShouldReturnMechanicRequests() {
        // Arrange
        testRequestDTO.setMechanicAssigned("Juan Pérez");
        when(serviceRequestService.getRequestsByMechanicName("Juan Pérez")).thenReturn(Arrays.asList(testRequestDTO));

        // Act
        ResponseEntity<List<ServiceRequestDTO>> response = serviceRequestController.getRequestsByMechanicName("Juan Pérez");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Juan Pérez", response.getBody().get(0).getMechanicAssigned());
    }

    @Test
    @DisplayName("GET /api/requests/status/{status} - Debe retornar solicitudes por estado")
    void getRequestsByStatus_ShouldReturnFilteredRequests() {
        // Arrange
        when(serviceRequestService.getRequestsByStatus("Pendiente")).thenReturn(Arrays.asList(testRequestDTO));

        // Act
        ResponseEntity<List<ServiceRequestDTO>> response = serviceRequestController.getRequestsByStatus("Pendiente");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Pendiente", response.getBody().get(0).getStatus());
    }

    @Test
    @DisplayName("POST /api/requests - Debe crear solicitud correctamente")
    void createRequest_ShouldReturnCreatedRequest() {
        // Arrange
        when(serviceRequestService.createRequest(any(ServiceRequestRequestDTO.class))).thenReturn(testRequestDTO);

        // Act
        ResponseEntity<ServiceRequestDTO> response = serviceRequestController.createRequest(testRequestRequestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Emergencia", response.getBody().getServiceType());
    }

    @Test
    @DisplayName("PUT /api/requests/{id} - Debe actualizar solicitud")
    void updateRequest_ShouldReturnUpdatedRequest() {
        // Arrange
        ServiceRequestDTO updatedDTO = new ServiceRequestDTO();
        updatedDTO.setId(1L);
        updatedDTO.setServiceType("Mantenimiento");
        updatedDTO.setLocation("Medellín");

        when(serviceRequestService.updateRequest(anyLong(), any(ServiceRequestRequestDTO.class))).thenReturn(updatedDTO);

        // Act
        ResponseEntity<ServiceRequestDTO> response = serviceRequestController.updateRequest(1L, testRequestRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Mantenimiento", response.getBody().getServiceType());
    }

    @Test
    @DisplayName("PUT /api/requests/{id}/status - Debe actualizar estado")
    void updateStatus_ShouldReturnUpdatedRequest() {
        // Arrange
        testRequestDTO.setStatus("En Proceso");
        when(serviceRequestService.updateRequestStatus(1L, "En Proceso")).thenReturn(testRequestDTO);

        // Act
        ResponseEntity<ServiceRequestDTO> response = serviceRequestController.updateStatus(1L, "En Proceso");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("En Proceso", response.getBody().getStatus());
    }

    @Test
    @DisplayName("PUT /api/requests/{id}/assign - Debe asignar mecánico")
    void assignMechanic_ShouldReturnUpdatedRequest() {
        // Arrange
        testRequestDTO.setMechanicAssigned("Juan Pérez");
        testRequestDTO.setStatus("En Proceso");
        when(serviceRequestService.assignMechanic(1L, "Juan Pérez")).thenReturn(testRequestDTO);

        // Act
        ResponseEntity<ServiceRequestDTO> response = serviceRequestController.assignMechanic(1L, "Juan Pérez");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan Pérez", response.getBody().getMechanicAssigned());
        assertEquals("En Proceso", response.getBody().getStatus());
    }

    @Test
    @DisplayName("DELETE /api/requests/{id} - Debe eliminar solicitud")
    void deleteRequest_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(serviceRequestService).deleteRequest(1L);

        // Act
        ResponseEntity<Void> response = serviceRequestController.deleteRequest(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(serviceRequestService, times(1)).deleteRequest(1L);
    }
}

