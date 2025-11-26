package com.fixsy.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar un usuario")
public class UserDTO {
    @Schema(description = "ID del usuario", example = "1")
    private Long id;
    
    @Schema(description = "Email del usuario", example = "usuario@example.com")
    private String email;
    
    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String name;
    
    @Schema(description = "Teléfono del usuario", example = "1234567890")
    private String phone;
    
    @Schema(description = "Rol del usuario", example = "CLIENT", allowableValues = {"CLIENT", "MECHANIC", "ADMIN"})
    private String role;
}

