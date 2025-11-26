package com.fixsy.gestionsolicitudes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o actualizar una solicitud de servicio")
public class ServiceRequestRequestDTO {
    @NotNull(message = "El ID de usuario es obligatorio")
    @Schema(description = "ID del usuario que crea la solicitud", example = "1", required = true)
    private Long userId;

    @NotBlank(message = "El tipo de servicio es obligatorio")
    @Schema(description = "Tipo de servicio", example = "Mantenimiento", required = true)
    private String serviceType;

    @NotBlank(message = "La información del vehículo es obligatoria")
    @Schema(description = "Información del vehículo", example = "Toyota Corolla 2020", required = true)
    private String vehicleInfo;

    @Schema(description = "Descripción del problema", example = "El vehículo no enciende")
    private String description;
    
    @Schema(description = "URLs de imágenes separadas por comas", example = "http://example.com/img1.jpg,http://example.com/img2.jpg", defaultValue = "")
    private String images = "";
    
    @Schema(description = "Ubicación del servicio", example = "Av. Principal 123", defaultValue = "")
    private String location = "";
    
    @Schema(description = "Notas adicionales", example = "El cliente prefiere servicio en la mañana", defaultValue = "")
    private String notes = "";
}

