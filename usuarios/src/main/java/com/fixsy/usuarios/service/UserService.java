package com.fixsy.usuarios.service;

import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.model.User;
import com.fixsy.usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

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

    public UserDTO createUser(UserRequestDTO userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword()); // En producción, hashear la contraseña
        user.setName(userRequest.getName());
        user.setPhone(userRequest.getPhone());
        user.setRole(userRequest.getRole());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UserRequestDTO userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail()); // Actualizar email también
        user.setPhone(userRequest.getPhone());
        // Solo actualizar contraseña si se proporciona y no está vacía
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty() && userRequest.getPassword().length() >= 8) {
            user.setPassword(userRequest.getPassword()); // En producción, hashear la contraseña
        }

        User updatedUser = userRepository.save(user);
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

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getRole()
        );
    }
}

