package com.fixsy.usuarios.service;

import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.model.Role;
import com.fixsy.usuarios.model.RoleType;
import com.fixsy.usuarios.model.User;
import com.fixsy.usuarios.repository.RoleRepository;
import com.fixsy.usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO createUser(UserRequestDTO userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Obtener el rol de la BD o crearlo si no existe
        Role role = getOrCreateRole(userRequest.getRole());

        User user = new User();
        user.setEmail(userRequest.getEmail());
        // Encriptar la contraseña con BCrypt
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setName(userRequest.getName());
        user.setPhone(userRequest.getPhone());
        user.setRole(role);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserRequestDTO userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos básicos
        user.setName(userRequest.getName());
        user.setPhone(userRequest.getPhone());

        // Validar email: verificar que no esté en uso por otro usuario
        if (!user.getEmail().equals(userRequest.getEmail())) {
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                throw new RuntimeException("El email ya está registrado por otro usuario");
            }
            user.setEmail(userRequest.getEmail());
        }

        // ⚠️ VALIDACIÓN CRÍTICA: Si se está cambiando la contraseña, validar la contraseña actual
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty() && userRequest.getPassword().length() >= 8) {
            // Si se proporciona una nueva contraseña, DEBE proporcionarse la contraseña actual
            if (userRequest.getCurrentPassword() == null || userRequest.getCurrentPassword().isEmpty()) {
                throw new IllegalArgumentException("La contraseña actual es requerida para cambiar la contraseña");
            }
            
            // Validar que la contraseña actual sea correcta
            if (!passwordEncoder.matches(userRequest.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("La contraseña actual es incorrecta");
            }
            
            // Si la validación pasa, encriptar y guardar la nueva contraseña
            String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());
            user.setPassword(encryptedPassword);
        }

        // Actualizar rol si se especifica
        if (userRequest.getRole() != null && !userRequest.getRole().isEmpty()) {
            Role role = getOrCreateRole(userRequest.getRole());
            user.setRole(role);
        }

        // Guardar cambios
        User updatedUser = userRepository.save(user);
        
        // ⚠️ CORRECCIÓN CRÍTICA: Forzar flush para asegurar persistencia inmediata
        // Esto garantiza que el UPDATE se ejecute en la BD antes de que termine la transacción
        userRepository.flush();
        
        return convertToDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDTO(user);
    }

    /**
     * Verifica las credenciales del usuario para login
     * ⚠️ IMPORTANTE: Este método valida la contraseña usando BCrypt
     * Solo la contraseña actual (la última guardada) será válida
     */
    public UserDTO verifyCredentials(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        
        // ⚠️ CORRECCIÓN CRÍTICA: Validar contraseña con BCrypt
        // passwordEncoder.matches() compara la contraseña en texto plano con el hash BCrypt almacenado
        // Solo funcionará si la contraseña coincide exactamente con la última guardada
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        return convertToDTO(user);
    }

    /**
     * Genera un token de recuperación de contraseña
     */
    @Transactional
    public String generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24)); // Token válido por 24 horas
        userRepository.save(user);

        return token;
    }

    /**
     * Restablece la contraseña usando el token de recuperación
     * Valida que el token pertenezca al email especificado (más seguro)
     */
    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        // Primero validar que el usuario existe
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que el token coincide con el del usuario
        if (user.getResetToken() == null || !user.getResetToken().equals(token)) {
            throw new RuntimeException("Token inválido o expirado");
        }

        // Validar que el token no ha expirado
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token inválido o expirado");
        }

        // Encriptar la nueva contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    /**
     * Obtiene o crea un rol a partir de su nombre
     */
    private Role getOrCreateRole(String roleName) {
        final RoleType roleType = getRoleType(roleName);

        return roleRepository.findByName(roleType)
                .orElseGet(() -> {
                    Role newRole = new Role(roleType, roleType.getDescription());
                    return roleRepository.save(newRole);
                });
    }

    /**
     * Obtiene el tipo de rol a partir del nombre, con CLIENT como valor por defecto
     */
    private RoleType getRoleType(String roleName) {
        try {
            return RoleType.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RoleType.CLIENT; // Rol por defecto
        }
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getRoleName()
        );
    }
}
