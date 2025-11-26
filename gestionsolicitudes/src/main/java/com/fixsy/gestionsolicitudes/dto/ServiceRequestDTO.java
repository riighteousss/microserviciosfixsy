package com.fixsy.gestionsolicitudes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar una solicitud de servicio")
public class ServiceRequestDTO {
    @Schema(description = "ID de la solicitud", example = "1")
    private Long id;
    
    @Schema(description = "ID del usuario que creó la solicitud", example = "1")
    private Long userId;
    
    @Schema(description = "Tipo de servicio", example = "Mantenimiento")
    private String serviceType;
    
    @Schema(description = "Información del vehículo", example = "Toyota Corolla 2020")
    private String vehicleInfo;
    
    @Schema(description = "Descripción del problema", example = "El vehículo no enciende")
    private String description;
    
    @Schema(description = "Estado de la solicitud", example = "PENDING", allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"})
    private String status;
    
    @Schema(description = "Fecha de creación (timestamp)", example = "1700000000000")
    private Long createdAt;
    
    @Schema(description = "URLs de imágenes separadas por comas", example = "http://example.com/img1.jpg,http://example.com/img2.jpg")
    private String images;
    
    @Schema(description = "Nombre del mecánico asignado", example = "Juan Pérez")
    private String mechanicAssigned;
    
    @Schema(description = "Costo estimado", example = "50000")
    private String estimatedCost;
    
    @Schema(description = "Ubicación del servicio", example = "Av. Principal 123")
    private String location;
    
    @Schema(description = "Notas adicionales", example = "El cliente prefiere servicio en la mañana")
    private String notes;
}

