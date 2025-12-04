package com.fixsy.usuarios.model;

/**
 * Enum que define los tipos de roles disponibles en el sistema
 */
public enum RoleType {
    CLIENT("Cliente del sistema"),
    MECHANIC("Mecánico registrado"),
    ADMIN("Administrador del sistema");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

