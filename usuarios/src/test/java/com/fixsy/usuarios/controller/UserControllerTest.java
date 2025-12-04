package com.fixsy.usuarios.controller;

import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO testUserDTO;
    private UserRequestDTO testUserRequest;

    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO(1L, "test@example.com", "Test User", "1234567890", "CLIENT");

        testUserRequest = new UserRequestDTO();
        testUserRequest.setEmail("test@example.com");
        testUserRequest.setPassword("password123");
        testUserRequest.setName("Test User");
        testUserRequest.setPhone("1234567890");
        testUserRequest.setRole("CLIENT");
    }

    @Test
    @DisplayName("GET /api/users - Debe retornar lista de usuarios")
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        UserDTO user2 = new UserDTO(2L, "user2@example.com", "User 2", "0987654321", "MECHANIC");
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUserDTO, user2));

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("GET /api/users/{id} - Debe retornar usuario por ID")
    void getUserById_ShouldReturnUser() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Debe retornar usuario por email")
    void getUserByEmail_ShouldReturnUser() {
        // Arrange
        when(userService.getUserByEmail("test@example.com")).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.getUserByEmail("test@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("POST /api/users - Debe crear usuario correctamente")
    void createUser_ShouldReturnCreatedUser() {
        // Arrange
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(testUserDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.createUser(testUserRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("POST /api/users - Debe fallar con email vacío")
    void createUser_ShouldFail_WhenEmailEmpty() {
        // Arrange
        testUserRequest.setEmail("");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userController.createUser(testUserRequest));
    }

    @Test
    @DisplayName("POST /api/users - Debe fallar con contraseña corta")
    void createUser_ShouldFail_WhenPasswordTooShort() {
        // Arrange
        testUserRequest.setPassword("short");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userController.createUser(testUserRequest));
        assertEquals("La contraseña debe tener al menos 8 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Debe actualizar usuario")
    void updateUser_ShouldReturnUpdatedUser() {
        // Arrange
        UserDTO updatedDTO = new UserDTO(1L, "updated@example.com", "Updated User", "9999999999", "CLIENT");
        when(userService.updateUser(anyLong(), any(UserRequestDTO.class))).thenReturn(updatedDTO);

        testUserRequest.setEmail("updated@example.com");
        testUserRequest.setName("Updated User");
        testUserRequest.setPassword(null); // No actualizar contraseña

        // Act
        ResponseEntity<?> response = userController.updateUser(1L, testUserRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserDTO);
        UserDTO responseBody = (UserDTO) response.getBody();
        assertEquals("updated@example.com", responseBody.getEmail());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Debe eliminar usuario")
    void deleteUser_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act
        ResponseEntity<Void> response = userController.deleteUser(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("POST /api/users/login - Debe iniciar sesión correctamente")
    void login_ShouldReturnUser_WhenCredentialsValid() {
        // Arrange
        when(userService.verifyCredentials("test@example.com", "password123")).thenReturn(testUserDTO);
        
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "test@example.com");
        credentials.put("password", "password123");

        // Act
        ResponseEntity<?> response = userController.login(credentials);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserDTO);
        UserDTO responseBody = (UserDTO) response.getBody();
        assertEquals("test@example.com", responseBody.getEmail());
    }

    @Test
    @DisplayName("POST /api/users/login - Debe fallar sin credenciales")
    void login_ShouldFail_WhenCredentialsMissing() {
        // Arrange
        Map<String, String> credentials = new HashMap<>();

        // Act
        ResponseEntity<?> response = userController.login(credentials);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> errorBody = (Map<String, String>) response.getBody();
        assertTrue(errorBody.containsKey("error"));
    }

    @Test
    @DisplayName("POST /api/users/forgot-password - Debe generar token de recuperación")
    void forgotPassword_ShouldReturnToken() {
        // Arrange
        when(userService.generatePasswordResetToken("test@example.com")).thenReturn("generated-token");
        
        Map<String, String> request = new HashMap<>();
        request.put("email", "test@example.com");

        // Act
        ResponseEntity<Map<String, String>> response = userController.forgotPassword(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("generated-token", response.getBody().get("token"));
    }

    @Test
    @DisplayName("POST /api/users/reset-password - Debe restablecer contraseña")
    void resetPassword_ShouldSuccess() {
        // Arrange
        doNothing().when(userService).resetPassword(anyString(), anyString(), anyString());
        
        Map<String, String> request = new HashMap<>();
        request.put("email", "test@example.com");
        request.put("token", "valid-token");
        request.put("newPassword", "newPassword123");

        // Act
        ResponseEntity<Map<String, String>> response = userController.resetPassword(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Contraseña restablecida exitosamente", response.getBody().get("message"));
    }

    @Test
    @DisplayName("POST /api/users/reset-password - Debe fallar con contraseña corta")
    void resetPassword_ShouldFail_WhenPasswordTooShort() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("token", "valid-token");
        request.put("newPassword", "short");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userController.resetPassword(request));
    }
}

