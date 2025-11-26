package com.fixsy.vehiculos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o actualizar un vehículo")
public class VehicleRequestDTO {
    @NotNull(message = "El ID de usuario es obligatorio")
    @Schema(description = "ID del usuario propietario", example = "1", required = true)
    private Long userId;

    @NotBlank(message = "La marca es obligatoria")
    @Schema(description = "Marca del vehículo", example = "Toyota", required = true)
    private String brand;

    @NotBlank(message = "El modelo es obligatorio")
    @Schema(description = "Modelo del vehículo", example = "Corolla", required = true)
    private String model;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1900, message = "El año debe ser mayor a 1900")
    @Max(value = 2100, message = "El año debe ser menor a 2100")
    @Schema(description = "Año del vehículo", example = "2020", required = true, minimum = "1900", maximum = "2100")
    private Integer year;

    @NotBlank(message = "La placa es obligatoria")
    @Schema(description = "Placa del vehículo", example = "ABC123", required = true)
    private String plate;

    @NotBlank(message = "El color es obligatorio")
    @Schema(description = "Color del vehículo", example = "Blanco", required = true)
    private String color;

    @Schema(description = "Indica si es el vehículo predeterminado", example = "false", defaultValue = "false")
    private Boolean isDefault = false;
}

