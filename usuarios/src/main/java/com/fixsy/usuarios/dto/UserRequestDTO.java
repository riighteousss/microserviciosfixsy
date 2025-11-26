package com.fixsy.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o actualizar un usuario")
public class UserRequestDTO {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Schema(description = "Email del usuario", example = "usuario@example.com", required = true)
    private String email;

    @Schema(description = "Contraseña del usuario (opcional en actualizaciones, requerida al crear)", example = "password123")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del usuario", example = "Juan Pérez", required = true)
    private String name;

    @NotBlank(message = "El teléfono es obligatorio")
    @Schema(description = "Teléfono del usuario", example = "1234567890", required = true)
    private String phone;

    @Schema(description = "Rol del usuario", example = "CLIENT", allowableValues = {"CLIENT", "MECHANIC", "ADMIN"}, defaultValue = "CLIENT")
    private String role = "CLIENT";
}

