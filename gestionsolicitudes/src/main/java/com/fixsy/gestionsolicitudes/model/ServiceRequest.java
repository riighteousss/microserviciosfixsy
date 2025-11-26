package com.fixsy.gestionsolicitudes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String serviceType; // "Emergencia", "Mantenimiento", "Revisión", etc.

    @Column(name = "vehicle_info", nullable = false)
    private String vehicleInfo; // Información del vehículo seleccionado

    @Column(columnDefinition = "TEXT")
    private String description; // Descripción del problema

    @Column(nullable = false)
    private String status = "Pendiente"; // "Pendiente", "En Proceso", "Completado", "Cancelado"

    @Column(name = "created_at")
    private Long createdAt; // Timestamp de creación

    @Column(columnDefinition = "TEXT")
    private String images = ""; // String separado por comas con rutas de imágenes

    @Column(name = "mechanic_assigned")
    private String mechanicAssigned = ""; // Nombre del mecánico asignado (si aplica)

    @Column(name = "estimated_cost")
    private String estimatedCost = ""; // Costo estimado

    @Column(name = "location")
    private String location = ""; // Ubicación del servicio

    @Column(columnDefinition = "TEXT")
    private String notes = ""; // Notas adicionales

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = System.currentTimeMillis();
        }
        if (status == null) {
            status = "Pendiente";
        }
    }
}

