package com.fixsy.usuarios.service;

import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.model.Role;
import com.fixsy.usuarios.model.RoleType;
import com.fixsy.usuarios.model.User;
import com.fixsy.usuarios.repository.RoleRepository;
import com.fixsy.usuarios.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role clientRole;
    private UserRequestDTO testUserRequest;

    @BeforeEach
    void setUp() {
        clientRole = new Role(RoleType.CLIENT, "Cliente del sistema");
        clientRole.setId(1L);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setName("Test User");
        testUser.setPhone("1234567890");
        testUser.setRole(clientRole);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUserRequest = new UserRequestDTO();
        testUserRequest.setEmail("test@example.com");
        testUserRequest.setPassword("password123");
        testUserRequest.setName("Test User");
        testUserRequest.setPhone("1234567890");
        testUserRequest.setRole("CLIENT");
    }

    @Test
    @DisplayName("Debe obtener todos los usuarios correctamente")
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setName("User 2");
        user2.setPhone("0987654321");
        user2.setRole(clientRole);

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener un usuario por ID correctamente")
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        UserDTO result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getUserById(99L));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Debe crear un usuario con contraseña encriptada")
    void createUser_ShouldCreateUserWithEncryptedPassword() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(RoleType.CLIENT)).thenReturn(Optional.of(clientRole));
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.createUser(testUserRequest);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email ya está registrado")
    void createUser_ShouldThrowException_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.createUser(testUserRequest));
        assertEquals("El email ya está registrado", exception.getMessage());
    }

    @Test
    @DisplayName("Debe actualizar un usuario correctamente")
    void updateUser_ShouldUpdateUser_WhenUserExists() {
        // Arrange
        UserRequestDTO updateRequest = new UserRequestDTO();
        updateRequest.setEmail("updated@example.com");
        updateRequest.setName("Updated Name");
        updateRequest.setPhone("9999999999");
        updateRequest.setRole("CLIENT");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("updated@example.com");
        updatedUser.setName("Updated Name");
        updatedUser.setPhone("9999999999");
        updatedUser.setRole(clientRole);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(RoleType.CLIENT)).thenReturn(Optional.of(clientRole));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserDTO result = userService.updateUser(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("Updated Name", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe eliminar un usuario correctamente")
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar usuario inexistente")
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.deleteUser(99L));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Debe obtener usuario por email correctamente")
    void getUserByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        UserDTO result = userService.getUserByEmail("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Debe verificar credenciales correctamente")
    void verifyCredentials_ShouldReturnUser_WhenCredentialsValid() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        // Act
        UserDTO result = userService.verifyCredentials("test@example.com", "password123");

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Debe lanzar excepción con credenciales inválidas")
    void verifyCredentials_ShouldThrowException_WhenPasswordInvalid() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.verifyCredentials("test@example.com", "wrongpassword"));
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    @DisplayName("Debe generar token de recuperación de contraseña")
    void generatePasswordResetToken_ShouldGenerateToken() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        String token = userService.generatePasswordResetToken("test@example.com");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe restablecer contraseña con token válido")
    void resetPassword_ShouldResetPassword_WhenTokenValid() {
        // Arrange
        testUser.setResetToken("valid-token");
        testUser.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        assertDoesNotThrow(() -> userService.resetPassword("test@example.com", "valid-token", "newPassword123"));

        // Assert
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción con token expirado")
    void resetPassword_ShouldThrowException_WhenTokenExpired() {
        // Arrange
        testUser.setResetToken("expired-token");
        testUser.setResetTokenExpiry(LocalDateTime.now().minusHours(1)); // Token expirado

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.resetPassword("test@example.com", "expired-token", "newPassword123"));
        assertEquals("Token inválido o expirado", exception.getMessage());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción cuando el token no coincide")
    void resetPassword_ShouldThrowException_WhenTokenMismatch() {
        // Arrange
        testUser.setResetToken("valid-token");
        testUser.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.resetPassword("test@example.com", "wrong-token", "newPassword123"));
        assertEquals("Token inválido o expirado", exception.getMessage());
    }
}

