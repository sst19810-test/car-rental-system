package com.carrentalsystem.app.service.impl;

import com.carrentalsystem.app.dto.UserDTO;
import com.carrentalsystem.app.entity.User;
import com.carrentalsystem.app.exception.ResourceNotFoundException;
import com.carrentalsystem.app.helper.Role;
import com.carrentalsystem.app.repository.UserRepository;
import com.carrentalsystem.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return mapToDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole().name().equals(Role.USER.name()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO registerUser(User user) {
        // The password must be encoded before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER); // default role
        User saved = userRepository.save(user);
        return mapToDTO(saved);
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Cannot delete. User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Note: The custom authenticateUser and authenticateAdmin methods are no longer needed for the API.
    // The AuthenticationManager now handles this by calling the UserDetailsServiceImpl.
    // I am removing them to avoid confusion, but they could be kept for other purposes if needed.

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        return dto;
    }
}
