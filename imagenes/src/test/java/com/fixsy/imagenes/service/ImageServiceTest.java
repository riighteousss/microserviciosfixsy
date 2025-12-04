package com.fixsy.imagenes.service;

import com.fixsy.imagenes.dto.ImageDTO;
import com.fixsy.imagenes.model.Image;
import com.fixsy.imagenes.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    private Image testImage;

    @BeforeEach
    void setUp() {
        testImage = new Image();
        testImage.setId(1L);
        testImage.setFileName("test-image.jpg");
        testImage.setOriginalName("foto.jpg");
        testImage.setContentType("image/jpeg");
        testImage.setFileSize(1024L);
        testImage.setFilePath("./uploads/images/test-image.jpg");
        testImage.setUserId(1L);
        testImage.setEntityType("SERVICE_REQUEST");
        testImage.setEntityId(1L);
        testImage.setImageData(new byte[]{1, 2, 3, 4, 5});
        testImage.setCreatedAt(LocalDateTime.now());
        testImage.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Debe obtener todas las imágenes")
    void getAllImages_ShouldReturnAllImages() {
        // Arrange
        Image image2 = new Image();
        image2.setId(2L);
        image2.setFileName("image2.jpg");
        image2.setOriginalName("foto2.jpg");
        image2.setContentType("image/jpeg");
        image2.setUserId(1L);
        image2.setCreatedAt(LocalDateTime.now());

        when(imageRepository.findAll()).thenReturn(Arrays.asList(testImage, image2));

        // Act
        List<ImageDTO> result = imageService.getAllImages();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(imageRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener imagen por ID")
    void getImageById_ShouldReturnImage_WhenExists() {
        // Arrange
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // Act
        ImageDTO result = imageService.getImageById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test-image.jpg", result.getFileName());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando imagen no existe")
    void getImageById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(imageRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> imageService.getImageById(99L));
        assertEquals("Imagen no encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Debe obtener datos binarios de imagen")
    void getImageData_ShouldReturnBytes_WhenImageHasData() {
        // Arrange
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // Act
        byte[] result = imageService.getImageData(1L);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.length);
    }

    @Test
    @DisplayName("Debe obtener tipo de contenido de imagen")
    void getImageContentType_ShouldReturnContentType() {
        // Arrange
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // Act
        String result = imageService.getImageContentType(1L);

        // Assert
        assertEquals("image/jpeg", result);
    }

    @Test
    @DisplayName("Debe obtener imágenes por usuario")
    void getImagesByUserId_ShouldReturnUserImages() {
        // Arrange
        when(imageRepository.findByUserId(1L)).thenReturn(Collections.singletonList(testImage));

        // Act
        List<ImageDTO> result = imageService.getImagesByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    @DisplayName("Debe obtener imágenes por entidad")
    void getImagesByEntity_ShouldReturnEntityImages() {
        // Arrange
        when(imageRepository.findByEntityTypeAndEntityId("SERVICE_REQUEST", 1L))
            .thenReturn(Collections.singletonList(testImage));

        // Act
        List<ImageDTO> result = imageService.getImagesByEntity("SERVICE_REQUEST", 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SERVICE_REQUEST", result.get(0).getEntityType());
    }

    @Test
    @DisplayName("Debe subir imagen correctamente")
    void uploadImage_ShouldSaveImage() {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            new byte[]{1, 2, 3, 4, 5}
        );

        when(imageRepository.save(any(Image.class))).thenReturn(testImage);

        // Act
        ImageDTO result = imageService.uploadImage(mockFile, 1L, "SERVICE_REQUEST", 1L);

        // Assert
        assertNotNull(result);
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando archivo está vacío")
    void uploadImage_ShouldThrowException_WhenFileEmpty() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            new byte[]{}
        );

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> imageService.uploadImage(emptyFile, 1L, "SERVICE_REQUEST", 1L));
        assertEquals("El archivo está vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no es imagen")
    void uploadImage_ShouldThrowException_WhenNotImage() {
        // Arrange
        MockMultipartFile textFile = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            new byte[]{1, 2, 3}
        );

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> imageService.uploadImage(textFile, 1L, "SERVICE_REQUEST", 1L));
        assertEquals("Solo se permiten archivos de imagen", exception.getMessage());
    }

    @Test
    @DisplayName("Debe subir imagen Base64 correctamente")
    void uploadImageBase64_ShouldSaveImage() {
        // Arrange - Pequeña imagen JPEG en Base64
        String base64Data = "data:image/jpeg;base64,/9j/4AAQSkZJRg==";
        when(imageRepository.save(any(Image.class))).thenReturn(testImage);

        // Act
        ImageDTO result = imageService.uploadImageBase64(base64Data, "test.jpg", "image/jpeg", 1L, "SERVICE_REQUEST", 1L);

        // Assert
        assertNotNull(result);
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    @DisplayName("Debe eliminar imagen correctamente")
    void deleteImage_ShouldDeleteImage() {
        // Arrange
        when(imageRepository.existsById(1L)).thenReturn(true);
        doNothing().when(imageRepository).deleteById(1L);

        // Act
        imageService.deleteImage(1L);

        // Assert
        verify(imageRepository, times(1)).existsById(1L);
        verify(imageRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar imagen inexistente")
    void deleteImage_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(imageRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> imageService.deleteImage(99L));
        assertEquals("Imagen no encontrada", exception.getMessage());
        verify(imageRepository, times(1)).existsById(99L);
        verify(imageRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe eliminar imágenes por entidad")
    void deleteImagesByEntity_ShouldDeleteEntityImages() {
        // Arrange
        doNothing().when(imageRepository).deleteByEntityTypeAndEntityId("SERVICE_REQUEST", 1L);

        // Act
        imageService.deleteImagesByEntity("SERVICE_REQUEST", 1L);

        // Assert
        verify(imageRepository, times(1)).deleteByEntityTypeAndEntityId("SERVICE_REQUEST", 1L);
    }
}

