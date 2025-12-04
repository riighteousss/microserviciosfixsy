package com.fixsy.imagenes.controller;

import com.fixsy.imagenes.dto.ImageDTO;
import com.fixsy.imagenes.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Image Controller", description = "API para gestión de imágenes")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping
    @Operation(summary = "Obtener todas las imágenes", description = "Retorna una lista con todas las imágenes registradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de imágenes obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<ImageDTO>> getAllImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener imagen por ID", description = "Retorna la información de una imagen específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen encontrada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "404", description = "Imagen no encontrada",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ImageDTO> getImageById(
            @Parameter(description = "ID de la imagen", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Descargar imagen", description = "Retorna el contenido binario de la imagen")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Imagen descargada exitosamente",
                content = @Content(mediaType = "image/*")),
        @ApiResponse(responseCode = "404", description = "Imagen no encontrada",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<byte[]> downloadImage(
            @Parameter(description = "ID de la imagen", required = true, example = "1")
            @PathVariable Long id) {
        byte[] imageData = imageService.getImageData(id);
        String contentType = imageService.getImageContentType(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        
        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener imágenes por usuario", description = "Retorna todas las imágenes de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de imágenes del usuario obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<ImageDTO>> getImagesByUserId(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(imageService.getImagesByUserId(userId));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Obtener imágenes por entidad", description = "Retorna todas las imágenes asociadas a una entidad")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de imágenes de la entidad obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<ImageDTO>> getImagesByEntity(
            @Parameter(description = "Tipo de entidad", required = true, example = "SERVICE_REQUEST",
                    schema = @Schema(allowableValues = {"USER", "VEHICLE", "SERVICE_REQUEST"}))
            @PathVariable String entityType,
            @Parameter(description = "ID de la entidad", required = true, example = "1")
            @PathVariable Long entityId) {
        return ResponseEntity.ok(imageService.getImagesByEntity(entityType, entityId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir imagen (multipart)", description = "Sube una nueva imagen como archivo multipart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Imagen subida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Archivo inválido o vacío",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ImageDTO> uploadImage(
            @Parameter(description = "Archivo de imagen", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "ID del usuario", required = true)
            @RequestParam("userId") Long userId,
            @Parameter(description = "Tipo de entidad", example = "SERVICE_REQUEST")
            @RequestParam(value = "entityType", required = false) String entityType,
            @Parameter(description = "ID de la entidad")
            @RequestParam(value = "entityId", required = false) Long entityId) {
        ImageDTO savedImage = imageService.uploadImage(file, userId, entityType, entityId);
        return new ResponseEntity<>(savedImage, HttpStatus.CREATED);
    }

    @PostMapping("/base64")
    @Operation(summary = "Subir imagen (Base64)", description = "Sube una nueva imagen en formato Base64. La imagen se guarda OBLIGATORIAMENTE en la BD como BLOB.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Imagen subida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos Base64 inválidos",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ImageDTO> uploadImageBase64(
            @Parameter(description = "Datos de la imagen en Base64")
            @RequestBody Map<String, Object> request) {
        String base64Data = (String) request.get("base64Data");
        String fileName = (String) request.get("fileName");
        String mimeType = (String) request.get("mimeType"); // Usar mimeType del request
        Object userIdObj = request.get("userId");
        String entityType = (String) request.get("entityType");
        
        // Si viene requestId, convertir a entityType/entityId
        Object requestIdObj = request.get("requestId");
        Long entityId = null;
        if (requestIdObj != null) {
            entityId = ((Number) requestIdObj).longValue();
            // Si no se especifica entityType pero viene requestId, asumir SERVICE_REQUEST
            if (entityType == null || entityType.isEmpty()) {
                entityType = "SERVICE_REQUEST";
            }
        } else if (request.get("entityId") != null) {
            entityId = ((Number) request.get("entityId")).longValue();
        }
        
        // Si no viene entityType pero es imagen de perfil (sin requestId), usar USER
        if ((entityType == null || entityType.isEmpty()) && entityId == null) {
            entityType = "USER";
            // Para imágenes de perfil, entityId = userId
            entityId = userIdObj != null ? ((Number) userIdObj).longValue() : null;
        }

        if (base64Data == null || base64Data.isEmpty()) {
            throw new RuntimeException("Los datos Base64 son requeridos");
        }
        if (userIdObj == null) {
            throw new RuntimeException("El ID de usuario es requerido");
        }
        Long userId = ((Number) userIdObj).longValue();

        ImageDTO savedImage = imageService.uploadImageBase64(base64Data, fileName, mimeType, userId, entityType, entityId);
        return new ResponseEntity<>(savedImage, HttpStatus.CREATED);
    }
    
    @PostMapping
    @Operation(summary = "Subir imagen (Base64 - Alias)", description = "Alias para /base64. Sube una nueva imagen en formato Base64 desde DTO.")
    public ResponseEntity<ImageDTO> uploadImageFromDTO(
            @RequestBody Map<String, Object> request) {
        // Redirigir a uploadImageBase64
        return uploadImageBase64(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar imagen", description = "Elimina una imagen del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Imagen eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Imagen no encontrada",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> deleteImage(
            @Parameter(description = "ID de la imagen", required = true, example = "1")
            @PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Eliminar imágenes por entidad", description = "Elimina todas las imágenes asociadas a una entidad")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Imágenes eliminadas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> deleteImagesByEntity(
            @Parameter(description = "Tipo de entidad", required = true, example = "SERVICE_REQUEST")
            @PathVariable String entityType,
            @Parameter(description = "ID de la entidad", required = true, example = "1")
            @PathVariable Long entityId) {
        imageService.deleteImagesByEntity(entityType, entityId);
        return ResponseEntity.noContent().build();
    }
}

