package com.fixsy.gestionsolicitudes.services;

import com.fixsy.gestionsolicitudes.dto.ServiceRequestDTO;
import com.fixsy.gestionsolicitudes.dto.ServiceRequestRequestDTO;
import com.fixsy.gestionsolicitudes.model.ServiceRequest;
import com.fixsy.gestionsolicitudes.repository.ServiceRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRequestServiceTest {

    @Mock
    private ServiceRequestRepository repository;

    @InjectMocks
    private ServiceRequestService service;

    private ServiceRequest testRequest;
    private ServiceRequestRequestDTO testRequestDTO;

    @BeforeEach
    void setUp() {
        testRequest = new ServiceRequest();
        testRequest.setId(1L);
        testRequest.setUserId(1L);
        testRequest.setServiceType("Emergencia");
        testRequest.setVehicleInfo("Toyota Corolla 2020 - ABC123");
        testRequest.setDescription("El auto no enciende");
        testRequest.setStatus("Pendiente");
        testRequest.setCreatedAt(System.currentTimeMillis());
        testRequest.setImages("");
        testRequest.setMechanicAssigned("");
        testRequest.setEstimatedCost("");
        testRequest.setLocation("Bogotá");
        testRequest.setNotes("");

        testRequestDTO = new ServiceRequestRequestDTO();
        testRequestDTO.setUserId(1L);
        testRequestDTO.setServiceType("Emergencia");
        testRequestDTO.setVehicleInfo("Toyota Corolla 2020 - ABC123");
        testRequestDTO.setDescription("El auto no enciende");
        testRequestDTO.setLocation("Bogotá");
    }

    @Test
    @DisplayName("Debe obtener todas las solicitudes")
    void getAllRequests_ShouldReturnAllRequests() {
        // Arrange
        ServiceRequest request2 = new ServiceRequest();
        request2.setId(2L);
        request2.setUserId(2L);
        request2.setServiceType("Mantenimiento");
        request2.setVehicleInfo("Honda Civic 2021 - XYZ789");
        request2.setDescription("Cambio de aceite");
        request2.setStatus("En Proceso");
        request2.setCreatedAt(System.currentTimeMillis());

        when(repository.findAll()).thenReturn(Arrays.asList(testRequest, request2));

        // Act
        List<ServiceRequestDTO> result = service.getAllRequests();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Emergencia", result.get(0).getServiceType());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener solicitud por ID")
    void getRequestById_ShouldReturnRequest_WhenExists() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(testRequest));

        // Act
        ServiceRequestDTO result = service.getRequestById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Emergencia", result.getServiceType());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando solicitud no existe")
    void getRequestById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.getRequestById(99L));
        assertEquals("Solicitud no encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Debe obtener solicitudes por usuario")
    void getRequestsByUserId_ShouldReturnUserRequests() {
        // Arrange
        when(repository.findByUserId(1L)).thenReturn(Collections.singletonList(testRequest));

        // Act
        List<ServiceRequestDTO> result = service.getRequestsByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    @DisplayName("Debe obtener solicitudes por mecánico")
    void getRequestsByMechanicName_ShouldReturnMechanicRequests() {
        // Arrange
        testRequest.setMechanicAssigned("Juan Pérez");
        when(repository.findByMechanicAssigned("Juan Pérez")).thenReturn(Collections.singletonList(testRequest));

        // Act
        List<ServiceRequestDTO> result = service.getRequestsByMechanicName("Juan Pérez");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan Pérez", result.get(0).getMechanicAssigned());
    }

    @Test
    @DisplayName("Debe obtener solicitudes por estado")
    void getRequestsByStatus_ShouldReturnFilteredRequests() {
        // Arrange
        when(repository.findByStatus("Pendiente")).thenReturn(Collections.singletonList(testRequest));

        // Act
        List<ServiceRequestDTO> result = service.getRequestsByStatus("Pendiente");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pendiente", result.get(0).getStatus());
    }

    @Test
    @DisplayName("Debe crear solicitud correctamente")
    void createRequest_ShouldCreateRequest() {
        // Arrange
        when(repository.save(any(ServiceRequest.class))).thenReturn(testRequest);

        // Act
        ServiceRequestDTO result = service.createRequest(testRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Emergencia", result.getServiceType());
        assertEquals("Pendiente", result.getStatus());
        verify(repository, times(1)).save(any(ServiceRequest.class));
    }

    @Test
    @DisplayName("Debe actualizar solicitud correctamente")
    void updateRequest_ShouldUpdateRequest() {
        // Arrange
        ServiceRequestRequestDTO updateDTO = new ServiceRequestRequestDTO();
        updateDTO.setUserId(1L);
        updateDTO.setServiceType("Mantenimiento");
        updateDTO.setVehicleInfo("Toyota Corolla 2020 - ABC123");
        updateDTO.setDescription("Cambio de aceite programado");
        updateDTO.setLocation("Medellín");

        ServiceRequest updatedRequest = new ServiceRequest();
        updatedRequest.setId(1L);
        updatedRequest.setUserId(1L);
        updatedRequest.setServiceType("Mantenimiento");
        updatedRequest.setVehicleInfo("Toyota Corolla 2020 - ABC123");
        updatedRequest.setDescription("Cambio de aceite programado");
        updatedRequest.setStatus("Pendiente");
        updatedRequest.setLocation("Medellín");
        updatedRequest.setCreatedAt(System.currentTimeMillis());

        when(repository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(repository.save(any(ServiceRequest.class))).thenReturn(updatedRequest);

        // Act
        ServiceRequestDTO result = service.updateRequest(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Mantenimiento", result.getServiceType());
        assertEquals("Medellín", result.getLocation());
    }

    @Test
    @DisplayName("Debe actualizar estado de solicitud")
    void updateRequestStatus_ShouldUpdateStatus() {
        // Arrange
        ServiceRequest updatedRequest = new ServiceRequest();
        updatedRequest.setId(1L);
        updatedRequest.setUserId(1L);
        updatedRequest.setServiceType("Emergencia");
        updatedRequest.setVehicleInfo("Toyota Corolla 2020 - ABC123");
        updatedRequest.setDescription("El auto no enciende");
        updatedRequest.setStatus("En Proceso");
        updatedRequest.setCreatedAt(System.currentTimeMillis());

        when(repository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(repository.save(any(ServiceRequest.class))).thenReturn(updatedRequest);

        // Act
        ServiceRequestDTO result = service.updateRequestStatus(1L, "En Proceso");

        // Assert
        assertNotNull(result);
        assertEquals("En Proceso", result.getStatus());
    }

    @Test
    @DisplayName("Debe asignar mecánico correctamente")
    void assignMechanic_ShouldAssignMechanic() {
        // Arrange
        ServiceRequest updatedRequest = new ServiceRequest();
        updatedRequest.setId(1L);
        updatedRequest.setUserId(1L);
        updatedRequest.setServiceType("Emergencia");
        updatedRequest.setVehicleInfo("Toyota Corolla 2020 - ABC123");
        updatedRequest.setDescription("El auto no enciende");
        updatedRequest.setStatus("En Proceso");
        updatedRequest.setMechanicAssigned("Juan Pérez");
        updatedRequest.setCreatedAt(System.currentTimeMillis());

        when(repository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(repository.save(any(ServiceRequest.class))).thenReturn(updatedRequest);

        // Act
        ServiceRequestDTO result = service.assignMechanic(1L, "Juan Pérez");

        // Assert
        assertNotNull(result);
        assertEquals("Juan Pérez", result.getMechanicAssigned());
        assertEquals("En Proceso", result.getStatus());
    }

    @Test
    @DisplayName("Debe eliminar solicitud correctamente")
    void deleteRequest_ShouldDeleteRequest() {
        // Arrange
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        // Act
        service.deleteRequest(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar solicitud inexistente")
    void deleteRequest_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(repository.existsById(99L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.deleteRequest(99L));
        assertEquals("Solicitud no encontrada", exception.getMessage());
    }
}

