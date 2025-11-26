package com.fixsy.vehiculos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar un vehículo")
public class VehicleDTO {
    @Schema(description = "ID del vehículo", example = "1")
    private Long id;
    
    @Schema(description = "ID del usuario propietario", example = "1")
    private Long userId;
    
    @Schema(description = "Marca del vehículo", example = "Toyota")
    private String brand;
    
    @Schema(description = "Modelo del vehículo", example = "Corolla")
    private String model;
    
    @Schema(description = "Año del vehículo", example = "2020")
    private Integer year;
    
    @Schema(description = "Placa del vehículo", example = "ABC123")
    private String plate;
    
    @Schema(description = "Color del vehículo", example = "Blanco")
    private String color;
    
    @Schema(description = "Indica si es el vehículo predeterminado", example = "true")
    private Boolean isDefault;
    
    @Schema(description = "Fecha de creación (timestamp)", example = "1700000000000")
    private Long createdAt;
}

