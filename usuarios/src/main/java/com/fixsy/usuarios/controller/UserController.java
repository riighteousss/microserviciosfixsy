package com.fixsy.usuarios.controller;

import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "API para gestión de usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener usuario por email")
    public ResponseEntity<UserDTO> getUserByEmail(
            @PathVariable 
            @io.swagger.v3.oas.annotations.Parameter(description = "Email del usuario (puede venir codificado)", example = "usuario@example.com")
            String email) {
        try {
            // Decodificar el email si viene codificado (para manejar caracteres especiales como @)
            String decodedEmail = java.net.URLDecoder.decode(email, java.nio.charset.StandardCharsets.UTF_8);
            return ResponseEntity.ok(userService.getUserByEmail(decodedEmail));
        } catch (Exception e) {
            // Si falla la decodificación, intentar con el email original
            return ResponseEntity.ok(userService.getUserByEmail(email));
        }
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequestDTO userRequest) {
        // Validación manual para crear usuario
        if (userRequest.getEmail() == null || userRequest.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }
        if (!userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Formato de email inválido");
        }
        if (userRequest.getName() == null || userRequest.getName().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (userRequest.getPhone() == null || userRequest.getPhone().isBlank()) {
            throw new RuntimeException("El teléfono es obligatorio");
        }
        // Validar password (obligatorio al crear)
        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria al crear un usuario");
        }
        if (userRequest.getPassword().length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userRequest) {
        // Validación manual para permitir password null en actualizaciones
        if (userRequest.getEmail() == null || userRequest.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }
        if (!userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Formato de email inválido");
        }
        if (userRequest.getName() == null || userRequest.getName().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (userRequest.getPhone() == null || userRequest.getPhone().isBlank()) {
            throw new RuntimeException("El teléfono es obligatorio");
        }
        // Validar password solo si se proporciona (no es null ni vacío)
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty() && userRequest.getPassword().length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

