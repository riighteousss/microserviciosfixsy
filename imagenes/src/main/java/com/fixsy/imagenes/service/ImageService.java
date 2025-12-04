package com.fixsy.imagenes.service;

import com.fixsy.imagenes.dto.ImageDTO;
import com.fixsy.imagenes.model.Image;
import com.fixsy.imagenes.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de imágenes
 * Simplificado siguiendo el patrón del microservicio de solicitudes
 */
@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    /**
     * Obtiene todas las imágenes
     */
    public List<ImageDTO> getAllImages() {
        return imageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una imagen por su ID
     */
    public ImageDTO getImageById(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        ImageDTO dto = convertToDTO(image);
        // Incluir datos base64 si están disponibles
        if (image.getImageData() != null && image.getImageData().length > 0) {
            dto.setBase64Data(Base64.getEncoder().encodeToString(image.getImageData()));
        }
        return dto;
    }

    /**
     * Obtiene el contenido binario de una imagen
     */
    public byte[] getImageData(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        
        if (image.getImageData() == null || image.getImageData().length == 0) {
            throw new RuntimeException("Los datos de la imagen no están disponibles");
        }
        
        return image.getImageData();
    }

    /**
     * Obtiene el tipo de contenido de una imagen
     */
    public String getImageContentType(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        return image.getContentType();
    }

    /**
     * Obtiene imágenes por usuario
     */
    public List<ImageDTO> getImagesByUserId(Long userId) {
        return imageRepository.findByUserId(userId).stream()
                .map(image -> {
                    ImageDTO dto = convertToDTO(image);
                    if (image.getImageData() != null && image.getImageData().length > 0) {
                        dto.setBase64Data(Base64.getEncoder().encodeToString(image.getImageData()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene imágenes por entidad (tipo e ID)
     */
    public List<ImageDTO> getImagesByEntity(String entityType, Long entityId) {
        return imageRepository.findByEntityTypeAndEntityId(entityType, entityId).stream()
                .map(image -> {
                    ImageDTO dto = convertToDTO(image);
                    if (image.getImageData() != null && image.getImageData().length > 0) {
                        dto.setBase64Data(Base64.getEncoder().encodeToString(image.getImageData()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Sube una imagen y la guarda en la BD (BLOB)
     * Patrón simple siguiendo el microservicio de solicitudes
     */
    @Transactional
    public ImageDTO uploadImage(MultipartFile file, Long userId, String entityType, Long entityId) {
        validateFile(file);

        try {
            String originalName = file.getOriginalFilename();
            String extension = getFileExtension(originalName);
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // Crear registro en la BD - SIMPLE Y DIRECTO
            Image image = new Image();
            image.setFileName(uniqueFileName);
            image.setOriginalName(originalName);
            image.setContentType(file.getContentType());
            image.setFileSize(file.getSize());
            image.setFilePath(""); // No guardamos ruta, solo BD
            image.setUserId(userId);
            image.setEntityType(entityType);
            image.setEntityId(entityId);
            
            // ⚠️ CRÍTICO: Guardar imagen como BLOB en la BD (obligatorio)
            image.setImageData(file.getBytes());

            Image savedImage = imageRepository.save(image);
            imageRepository.flush(); // Forzar persistencia inmediata
            
            return convertToDTO(savedImage);

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage());
        }
    }

    /**
     * Sube una imagen como Base64 y la guarda OBLIGATORIAMENTE en la BD (BLOB)
     * Patrón simple siguiendo el microservicio de solicitudes
     */
    @Transactional
    public ImageDTO uploadImageBase64(String base64Data, String fileName, String mimeType, Long userId, String entityType, Long entityId) {
        try {
            // Decodificar Base64
            byte[] imageBytes;
            String contentType = mimeType != null && !mimeType.isEmpty() ? mimeType : "image/jpeg";
            
            if (base64Data.contains(",")) {
                // Formato: data:image/jpeg;base64,/9j/4AAQ...
                String[] parts = base64Data.split(",");
                String data = parts[1];
                
                // Si no viene mimeType, intentar inferirlo del header
                if (mimeType == null || mimeType.isEmpty()) {
                    String header = parts[0];
                    if (header.contains("image/png")) {
                        contentType = "image/png";
                    } else if (header.contains("image/gif")) {
                        contentType = "image/gif";
                    } else if (header.contains("image/webp")) {
                        contentType = "image/webp";
                    }
                }
                
                imageBytes = Base64.getDecoder().decode(data);
            } else {
                imageBytes = Base64.getDecoder().decode(base64Data);
            }

            if (imageBytes == null || imageBytes.length == 0) {
                throw new RuntimeException("Los datos Base64 están vacíos o son inválidos");
            }

            // Generar nombre de archivo único
            String extension = getExtensionFromContentType(contentType);
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // Crear registro en la BD - SIMPLE Y DIRECTO (como ServiceRequestService)
            Image image = new Image();
            image.setFileName(uniqueFileName);
            image.setOriginalName(fileName != null ? fileName : uniqueFileName);
            image.setContentType(contentType);
            image.setFileSize((long) imageBytes.length);
            image.setFilePath(""); // No guardamos ruta, solo BD
            image.setUserId(userId);
            image.setEntityType(entityType);
            image.setEntityId(entityId);
            
            // ⚠️ CRÍTICO: Guardar imagen como BLOB en la BD (obligatorio)
            image.setImageData(imageBytes);

            // Guardar en BD - SIMPLE Y DIRECTO (como ServiceRequestService.createRequest)
            Image savedImage = imageRepository.save(image);
            imageRepository.flush(); // Forzar persistencia inmediata
            
            return convertToDTO(savedImage);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error al decodificar Base64: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la imagen Base64: " + e.getMessage());
        }
    }

    /**
     * Elimina una imagen
     */
    @Transactional
    public void deleteImage(Long id) {
        if (!imageRepository.existsById(id)) {
            throw new RuntimeException("Imagen no encontrada");
        }
        imageRepository.deleteById(id);
    }

    /**
     * Elimina todas las imágenes de una entidad
     */
    @Transactional
    public void deleteImagesByEntity(String entityType, Long entityId) {
        imageRepository.deleteByEntityTypeAndEntityId(entityType, entityId);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Solo se permiten archivos de imagen");
        }

        // Validar tamaño (10MB máximo)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("El archivo excede el tamaño máximo permitido (10MB)");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String getExtensionFromContentType(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    /**
     * Convierte Image a ImageDTO
     * Patrón simple como ServiceRequestService.convertToDTO
     */
    private ImageDTO convertToDTO(Image image) {
        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setFileName(image.getFileName());
        dto.setOriginalName(image.getOriginalName());
        dto.setContentType(image.getContentType());
        dto.setFileSize(image.getFileSize());
        dto.setUserId(image.getUserId());
        dto.setEntityType(image.getEntityType());
        dto.setEntityId(image.getEntityId());
        dto.setDownloadUrl("/api/images/" + image.getId() + "/download");
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }
}
