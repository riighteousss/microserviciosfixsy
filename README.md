# Fixsy - Microservicios

Este directorio contiene los microservicios del sistema Fixsy desarrollados con Spring Boot.

## Microservicios

### 1. Usuarios Service (Puerto 8081)
- **Descripción**: Gestión de usuarios del sistema con roles normalizados y contraseñas encriptadas
- **Base de datos**: `fixsy_usuarios`
- **Características**:
  - ✅ Tabla de roles normalizada (CLIENT, MECHANIC, ADMIN)
  - ✅ Contraseñas encriptadas con BCrypt
  - ✅ Recuperación de contraseña con tokens
  - ✅ Login con verificación de credenciales
- **Endpoints principales**:
  - GET `/api/users` - Obtener todos los usuarios
  - GET `/api/users/{id}` - Obtener usuario por ID
  - GET `/api/users/email/{email}` - Obtener usuario por email
  - POST `/api/users` - Crear nuevo usuario
  - PUT `/api/users/{id}` - Actualizar usuario
  - DELETE `/api/users/{id}` - Eliminar usuario
  - POST `/api/users/login` - Iniciar sesión
  - POST `/api/users/forgot-password` - Solicitar recuperación de contraseña
  - POST `/api/users/reset-password` - Restablecer contraseña

### 2. Gestión Solicitudes Service (Puerto 8082)
- **Descripción**: Gestión de solicitudes de servicio
- **Base de datos**: `fixsy_solicitudes`
- **Endpoints principales**:
  - GET `/api/requests` - Obtener todas las solicitudes
  - GET `/api/requests/user/{userId}` - Obtener solicitudes por usuario
  - GET `/api/requests/mechanic/{mechanicName}` - Obtener solicitudes por mecánico
  - GET `/api/requests/status/{status}` - Obtener solicitudes por estado
  - POST `/api/requests` - Crear nueva solicitud
  - PUT `/api/requests/{id}` - Actualizar solicitud completa
  - PUT `/api/requests/{id}/status` - Actualizar estado
  - PUT `/api/requests/{id}/assign` - Asignar mecánico
  - DELETE `/api/requests/{id}` - Eliminar solicitud

### 3. Vehículos Service (Puerto 8083)
- **Descripción**: Gestión de vehículos de usuarios
- **Base de datos**: `fixsy_vehiculos`
- **Endpoints principales**:
  - GET `/api/vehicles` - Obtener todos los vehículos
  - GET `/api/vehicles/{id}` - Obtener vehículo por ID
  - GET `/api/vehicles/user/{userId}` - Obtener vehículos por usuario
  - GET `/api/vehicles/user/{userId}/default` - Obtener vehículo predeterminado
  - GET `/api/vehicles/user/{userId}/count` - Obtener cantidad de vehículos
  - POST `/api/vehicles` - Crear nuevo vehículo
  - PUT `/api/vehicles/{id}` - Actualizar vehículo
  - PUT `/api/vehicles/{id}/set-default` - Establecer como predeterminado
  - DELETE `/api/vehicles/{id}` - Eliminar vehículo

### 4. Imágenes Service (Puerto 8084) ✨ NUEVO
- **Descripción**: Gestión de imágenes del sistema
- **Base de datos**: `fixsy_imagenes`
- **Características**:
  - ✅ Almacenamiento en BD (LONGBLOB) y sistema de archivos
  - ✅ Soporte para upload multipart y Base64
  - ✅ Asociación con entidades (USER, VEHICLE, SERVICE_REQUEST)
- **Endpoints principales**:
  - GET `/api/images` - Obtener todas las imágenes
  - GET `/api/images/{id}` - Obtener info de imagen
  - GET `/api/images/{id}/download` - Descargar imagen (binario)
  - GET `/api/images/user/{userId}` - Obtener imágenes por usuario
  - GET `/api/images/entity/{entityType}/{entityId}` - Obtener imágenes por entidad
  - POST `/api/images` - Subir imagen (multipart)
  - POST `/api/images/base64` - Subir imagen en Base64
  - DELETE `/api/images/{id}` - Eliminar imagen
  - DELETE `/api/images/entity/{entityType}/{entityId}` - Eliminar imágenes de entidad

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
- Vehículos: http://localhost:8083/swagger-ui.html
- Imágenes: http://localhost:8084/swagger-ui.html

## Ejecución

Para ejecutar cada microservicio:

```bash
cd usuarios
mvn spring-boot:run
```

Repite el proceso para cada microservicio en su respectiva carpeta.

## Tests

Cada microservicio incluye tests unitarios. Para ejecutarlos:

```bash
cd usuarios
mvn test
```

Los tests cubren:
- Servicios (UserService, VehicleService, etc.)
- Controladores
- Validaciones

## Estructura de Proyecto

Cada microservicio sigue la siguiente estructura:

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
│   │   │           ├── service/ (o services/)
│   │   │           ├── repository/
│   │   │           ├── model/
│   │   │           ├── dto/
│   │   │           ├── config/
│   │   │           └── exception/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/fixsy/
│               └── [microservicio]/
│                   ├── [Microservicio]ApplicationTests.java
│                   ├── service/
│                   │   └── [Entity]ServiceTest.java
│                   └── controller/
│                       └── [Entity]ControllerTest.java
```

## Características Implementadas

### Seguridad
- ✅ Contraseñas encriptadas con BCrypt
- ✅ Tabla de roles normalizada
- ✅ Recuperación de contraseña con tokens temporales (24h)

### Documentación
- ✅ Swagger/OpenAPI completo con códigos de respuesta
- ✅ Códigos HTTP apropiados (200, 201, 400, 401, 404, 409, 500)
- ✅ Descripciones detalladas en endpoints

### Tests
- ✅ Tests unitarios para servicios
- ✅ Tests unitarios para controladores
- ✅ Cobertura de casos de éxito y error

### Almacenamiento de Imágenes
- ✅ Microservicio dedicado para imágenes
- ✅ Almacenamiento dual (BD + sistema de archivos)
- ✅ Soporte para multipart y Base64

## Notas Importantes

- Las contraseñas ahora se almacenan encriptadas con BCrypt
- Los roles están normalizados en una tabla separada
- El servicio de imágenes almacena las fotos tanto en la BD como en el sistema de archivos
- Todos los endpoints tienen documentación Swagger completa
- Los tests usan Mockito para simular dependencias
