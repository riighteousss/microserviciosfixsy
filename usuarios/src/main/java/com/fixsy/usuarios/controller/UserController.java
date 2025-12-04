package com.fixsy.usuarios.controller;

import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "API para gestión de usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista con todos los usuarios registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Busca y retorna un usuario específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener usuario por email", description = "Busca y retorna un usuario específico por su email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<UserDTO> getUserByEmail(
            @Parameter(description = "Email del usuario (puede venir codificado)", required = true, example = "usuario@example.com")
            @PathVariable String email) {
        try {
            String decodedEmail = java.net.URLDecoder.decode(email, java.nio.charset.StandardCharsets.UTF_8);
            return ResponseEntity.ok(userService.getUserByEmail(decodedEmail));
        } catch (Exception e) {
            return ResponseEntity.ok(userService.getUserByEmail(email));
        }
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario", description = "Registra un nuevo usuario en el sistema con contraseña encriptada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequestDTO userRequest) {
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
        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria al crear un usuario");
        }
        if (userRequest.getPassword().length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long id, 
            @RequestBody UserRequestDTO userRequest) {
        if (userRequest.getEmail() == null || userRequest.getEmail().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (!userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.badRequest().build();
        }
        if (userRequest.getName() == null || userRequest.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (userRequest.getPhone() == null || userRequest.getPhone().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty() && userRequest.getPassword().length() < 8) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            return ResponseEntity.ok(userService.updateUser(id, userRequest));
        } catch (IllegalArgumentException e) {
            // Si el error es sobre contraseña actual incorrecta, devolver 401 (Unauthorized)
            if (e.getMessage() != null && e.getMessage().contains("contraseña actual")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
            }
            // Otros errores de validación retornan 400 (Bad Request)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error de validación"));
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
            }
            if (e.getMessage() != null && e.getMessage().contains("email ya está registrado")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error interno del servidor"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Verifica las credenciales del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> login(
            @Parameter(description = "Credenciales del usuario")
            @RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        if (email == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Email y contraseña son requeridos"));
        }
        
        try {
            UserDTO userDTO = userService.verifyCredentials(email, password);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            // Si las credenciales son inválidas, retornar 401 Unauthorized
            if (e.getMessage() != null && e.getMessage().contains("Credenciales inválidas")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Correo o contraseña incorrectos"));
            }
            // Otros errores retornan 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error interno del servidor"));
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperación de contraseña", description = "Genera un token de recuperación de contraseña")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token de recuperación generado exitosamente",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Parameter(description = "Email del usuario")
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.isBlank()) {
            throw new RuntimeException("El email es requerido");
        }
        
        String token = userService.generatePasswordResetToken(email);
        // En producción, aquí enviarías el token por email
        // Por ahora, lo retornamos para testing
        return ResponseEntity.ok(Map.of(
            "message", "Token de recuperación generado exitosamente",
            "token", token
        ));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña", description = "Restablece la contraseña usando el token de recuperación. Valida que el token pertenezca al email especificado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Token inválido o contraseña no cumple requisitos",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, String>> resetPassword(
            @Parameter(description = "Email, token y nueva contraseña")
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        
        if (email == null || email.isBlank()) {
            throw new RuntimeException("El email es requerido");
        }
        if (token == null || token.isBlank()) {
            throw new RuntimeException("El token es requerido");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("La nueva contraseña debe tener al menos 8 caracteres");
        }
        
        userService.resetPassword(email, token, newPassword);
        return ResponseEntity.ok(Map.of("message", "Contraseña restablecida exitosamente"));
    }
}
