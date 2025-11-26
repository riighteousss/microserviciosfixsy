# Fixsy - Microservicios

Este directorio contiene los microservicios del sistema Fixsy desarrollados con Spring Boot.

## Microservicios

### 1. Usuarios Service (Puerto 8081)
- **Descripción**: Gestión de usuarios del sistema
- **Base de datos**: `fixsy_usuarios`
- **Endpoints principales**:
  - GET `/api/users` - Obtener todos los usuarios
  - GET `/api/users/{id}` - Obtener usuario por ID
  - POST `/api/users` - Crear nuevo usuario
  - PUT `/api/users/{id}` - Actualizar usuario
  - DELETE `/api/users/{id}` - Eliminar usuario

### 2. Gestión Solicitudes Service (Puerto 8082)
- **Descripción**: Gestión de solicitudes de servicio
- **Base de datos**: `fixsy_solicitudes`
- **Endpoints principales**:
  - GET `/api/requests` - Obtener todas las solicitudes
  - GET `/api/requests/user/{userId}` - Obtener solicitudes por usuario
  - POST `/api/requests` - Crear nueva solicitud
  - PUT `/api/requests/{id}/status` - Actualizar estado
  - PUT `/api/requests/{id}/assign` - Asignar mecánico

### 3. Notificaciones Service (Puerto 8083)
- **Descripción**: Gestión de notificaciones
- **Base de datos**: `fixsy_notificaciones`
- **Endpoints principales**:
  - GET `/api/notifications/user/{userId}` - Obtener notificaciones por usuario
  - POST `/api/notifications` - Crear notificación
  - PUT `/api/notifications/{id}/read` - Marcar como leída

### 4. Media Service (Puerto 8084)
- **Descripción**: Gestión de archivos e imágenes
- **Base de datos**: `fixsy_media`
- **Endpoints principales**:
  - POST `/api/media/upload` - Subir archivo
  - GET `/api/media/files/{id}/download` - Descargar archivo
  - DELETE `/api/media/files/{id}` - Eliminar archivo

### 5. Vehículos Service (Puerto 8085) ✨ NUEVO
- **Descripción**: Gestión de vehículos de usuarios
- **Base de datos**: `fixsy_vehiculos`
- **Endpoints principales**:
  - GET `/api/vehicles` - Obtener todos los vehículos
  - GET `/api/vehicles/user/{userId}` - Obtener vehículos por usuario
  - GET `/api/vehicles/user/{userId}/default` - Obtener vehículo predeterminado
  - POST `/api/vehicles` - Crear nuevo vehículo
  - PUT `/api/vehicles/{id}` - Actualizar vehículo
  - PUT `/api/vehicles/{id}/set-default` - Establecer como predeterminado
  - DELETE `/api/vehicles/{id}` - Eliminar vehículo

## Configuración

### Requisitos
- Java 21
- Maven 3.6+
- MySQL 8.0+
- Spring Boot 3.4.6

### Base de Datos
Cada microservicio crea automáticamente su base de datos si no existe. Asegúrate de tener MySQL corriendo y configurado con:
- Usuario: `root`
- Contraseña: (vacía por defecto, configurable en `application.properties`)

### Swagger UI
Cada microservicio incluye Swagger UI disponible en:
- Usuarios: http://localhost:8081/swagger-ui.html
- Solicitudes: http://localhost:8082/swagger-ui.html
- Notificaciones: http://localhost:8083/swagger-ui.html
- Media: http://localhost:8084/swagger-ui.html
- Vehículos: http://localhost:8085/swagger-ui.html

## Ejecución

Para ejecutar cada microservicio:

```bash
cd usuarios
mvn spring-boot:run
```

Repite el proceso para cada microservicio en su respectiva carpeta.

## Estructura de Proyecto

Cada microservicio sigue la siguiente estructura (igual al repositorio original, sin HATEOAS):

```
microservicio/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fixsy/
│   │   │       └── [microservicio]/
│   │   │           ├── [Microservicio]Application.java
│   │   │           ├── controller/
│   │   │           ├── services/ (o service/)
│   │   │           ├── repository/
│   │   │           ├── model/
│   │   │           ├── dto/
│   │   │           ├── config/
│   │   │           └── webclient/ (para comunicación entre microservicios)
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/fixsy/
│               └── [microservicio]/
│                   └── [Microservicio]ApplicationTests.java
```

## Notas

- Los microservicios siguen la misma estructura del repositorio original `proyecto_semestral`
- **Sin HATEOAS**: A diferencia del repositorio original, estos microservicios NO incluyen HATEOAS
- Todos incluyen Swagger para documentación de API
- `gestionsolicitudes` incluye `webclient` para comunicación con otros microservicios
- **Modelo alineado**: El modelo `ServiceRequest` está alineado con `RequestHistoryEntity` de la app Android
- Las contraseñas se almacenan en texto plano (implementar hashing en producción)
- Los archivos se almacenan localmente en la carpeta `uploads` del servicio de media
- Usa `MySQL8Dialect` en lugar de `MySQLDialect`
- Java 21 y Spring Boot 3.4.6

## Microservicios y su Relación con la App

### ✅ usuarios
- **Necesario**: Gestiona usuarios con roles (CLIENT, MECHANIC, ADMIN)
- **Coincide con**: `UserEntity` de la app Android

### ✅ gestionsolicitudes
- **Necesario**: Gestiona solicitudes de servicio
- **Coincide con**: `RequestHistoryEntity` de la app Android
- **Campos alineados**: serviceType, vehicleInfo, description, status, images, mechanicAssigned, etc.

### ⚠️ notificaciones
- **Opcional**: Para notificaciones push futuras
- **Nota**: La app actualmente usa Snackbars locales, no notificaciones del servidor

### ⚠️ media
- **Opcional**: Para subida de imágenes al servidor
- **Nota**: La app actualmente guarda imágenes localmente como string

### ✅ vehiculos
- **Necesario**: Gestiona vehículos de usuarios
- **Coincide con**: `VehicleEntity` de la app Android
- **Migrado a Retrofit**: La app Android ahora consume este microservicio

Ver `ANALISIS_MICROSERVICIOS.md` para más detalles.
Ver `INSTRUCCIONES_PRUEBA.md` para instrucciones detalladas de cómo iniciar y probar todo.

