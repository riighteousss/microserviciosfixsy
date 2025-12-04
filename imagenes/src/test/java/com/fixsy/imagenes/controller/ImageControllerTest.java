package com.fixsy.imagenes.controller;

import com.fixsy.imagenes.dto.ImageDTO;
import com.fixsy.imagenes.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    private ImageDTO testImageDTO;

    @BeforeEach
    void setUp() {
        testImageDTO = new ImageDTO();
        testImageDTO.setId(1L);
        testImageDTO.setFileName("test-image.jpg");
        testImageDTO.setOriginalName("foto.jpg");
        testImageDTO.setContentType("image/jpeg");
        testImageDTO.setFileSize(1024L);
        testImageDTO.setUserId(1L);
        testImageDTO.setEntityType("SERVICE_REQUEST");
        testImageDTO.setEntityId(1L);
    }

    @Test
    @DisplayName("GET /api/images - Debe retornar lista de imágenes")
    void getAllImages_ShouldReturnListOfImages() {
        // Arrange
        ImageDTO image2 = new ImageDTO();
        image2.setId(2L);
        image2.setFileName("image2.jpg");

        when(imageService.getAllImages()).thenReturn(Arrays.asList(testImageDTO, image2));

        // Act
        ResponseEntity<List<ImageDTO>> response = imageController.getAllImages();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(imageService, times(1)).getAllImages();
    }

    @Test
    @DisplayName("GET /api/images/{id} - Debe retornar imagen por ID")
    void getImageById_ShouldReturnImage() {
        // Arrange
        when(imageService.getImageById(1L)).thenReturn(testImageDTO);

        // Act
        ResponseEntity<ImageDTO> response = imageController.getImageById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-image.jpg", response.getBody().getFileName());
    }

    @Test
    @DisplayName("GET /api/images/{id}/download - Debe descargar imagen")
    void downloadImage_ShouldReturnImageBytes() {
        // Arrange
        byte[] imageData = new byte[]{1, 2, 3, 4, 5};
        when(imageService.getImageData(1L)).thenReturn(imageData);
        when(imageService.getImageContentType(1L)).thenReturn("image/jpeg");

        // Act
        ResponseEntity<byte[]> response = imageController.downloadImage(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().length);
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
    }

    @Test
    @DisplayName("GET /api/images/user/{userId} - Debe retornar imágenes por usuario")
    void getImagesByUserId_ShouldReturnUserImages() {
        // Arrange
        when(imageService.getImagesByUserId(1L)).thenReturn(Arrays.asList(testImageDTO));

        // Act
        ResponseEntity<List<ImageDTO>> response = imageController.getImagesByUserId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getUserId());
    }

    @Test
    @DisplayName("GET /api/images/entity/{entityType}/{entityId} - Debe retornar imágenes por entidad")
    void getImagesByEntity_ShouldReturnEntityImages() {
        // Arrange
        when(imageService.getImagesByEntity("SERVICE_REQUEST", 1L)).thenReturn(Arrays.asList(testImageDTO));

        // Act
        ResponseEntity<List<ImageDTO>> response = imageController.getImagesByEntity("SERVICE_REQUEST", 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("SERVICE_REQUEST", response.getBody().get(0).getEntityType());
    }

    @Test
    @DisplayName("POST /api/images - Debe subir imagen multipart correctamente")
    void uploadImage_ShouldReturnCreatedImage() {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            new byte[]{1, 2, 3, 4, 5}
        );

        when(imageService.uploadImage(any(MultipartFile.class), anyLong(), anyString(), anyLong()))
            .thenReturn(testImageDTO);

        // Act
        ResponseEntity<ImageDTO> response = imageController.uploadImage(
            mockFile, 1L, "SERVICE_REQUEST", 1L);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-image.jpg", response.getBody().getFileName());
    }

    @Test
    @DisplayName("POST /api/images/base64 - Debe subir imagen Base64 correctamente")
    void uploadImageBase64_ShouldReturnCreatedImage() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("base64Data", "data:image/jpeg;base64,/9j/4AAQSkZJRg==");
        request.put("fileName", "test.jpg");
        request.put("mimeType", "image/jpeg");
        request.put("userId", 1L);
        request.put("entityType", "SERVICE_REQUEST");
        request.put("entityId", 1L);

        when(imageService.uploadImageBase64(anyString(), anyString(), anyString(), anyLong(), anyString(), anyLong()))
            .thenReturn(testImageDTO);

        // Act
        ResponseEntity<ImageDTO> response = imageController.uploadImageBase64(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-image.jpg", response.getBody().getFileName());
    }

    @Test
    @DisplayName("POST /api/images/base64 - Debe fallar sin datos Base64")
    void uploadImageBase64_ShouldFail_WhenBase64DataMissing() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("fileName", "test.jpg");
        request.put("userId", 1L);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> imageController.uploadImageBase64(request));
        assertEquals("Los datos Base64 son requeridos", exception.getMessage());
    }

    @Test
    @DisplayName("POST /api/images/base64 - Debe fallar sin userId")
    void uploadImageBase64_ShouldFail_WhenUserIdMissing() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("base64Data", "data:image/jpeg;base64,/9j/4AAQSkZJRg==");
        request.put("fileName", "test.jpg");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> imageController.uploadImageBase64(request));
        assertEquals("El ID de usuario es requerido", exception.getMessage());
    }

    @Test
    @DisplayName("DELETE /api/images/{id} - Debe eliminar imagen")
    void deleteImage_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(imageService).deleteImage(1L);

        // Act
        ResponseEntity<Void> response = imageController.deleteImage(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(imageService, times(1)).deleteImage(1L);
    }

    @Test
    @DisplayName("DELETE /api/images/entity/{entityType}/{entityId} - Debe eliminar imágenes por entidad")
    void deleteImagesByEntity_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(imageService).deleteImagesByEntity("SERVICE_REQUEST", 1L);

        // Act
        ResponseEntity<Void> response = imageController.deleteImagesByEntity("SERVICE_REQUEST", 1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(imageService, times(1)).deleteImagesByEntity("SERVICE_REQUEST", 1L);
    }
}

