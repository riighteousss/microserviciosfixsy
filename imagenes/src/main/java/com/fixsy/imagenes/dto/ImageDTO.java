package com.fixsy.imagenes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar una imagen")
public class ImageDTO {
    @Schema(description = "ID de la imagen", example = "1")
    private Long id;

    @Schema(description = "Nombre del archivo", example = "abc123-image.jpg")
    private String fileName;

    @Schema(description = "Nombre original del archivo", example = "mi_foto.jpg")
    private String originalName;

    @Schema(description = "Tipo de contenido", example = "image/jpeg")
    private String contentType;

    @Schema(description = "Tamaño del archivo en bytes", example = "102400")
    private Long fileSize;

    @Schema(description = "ID del usuario propietario", example = "1")
    private Long userId;

    @Schema(description = "Tipo de entidad asociada", example = "SERVICE_REQUEST")
    private String entityType;

    @Schema(description = "ID de la entidad asociada", example = "1")
    private Long entityId;

    @Schema(description = "URL para acceder a la imagen", example = "/api/images/1/download")
    private String downloadUrl;

    @Schema(description = "Datos de la imagen en Base64 (opcional, para descarga)")
    private String base64Data;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;
}

