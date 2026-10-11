package com.ijse.moviebooking.service.impl;

import com.ijse.moviebooking.dto.request.UserUpdateRequest;
import com.ijse.moviebooking.dto.response.UserResponse;
import com.ijse.moviebooking.entity.User;
import com.ijse.moviebooking.enums.Role;
import com.ijse.moviebooking.exception.BadRequestException;
import com.ijse.moviebooking.exception.ResourceNotFoundException;
import com.ijse.moviebooking.exception.UnauthorizedException;
import com.ijse.moviebooking.repository.UserRepository;
import com.ijse.moviebooking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest updateRequest, String currentUserUsername) {
        log.info("Updating user ID: {} by current user: {}", id, currentUserUsername);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        User currentUser = userRepository.findByUsername(currentUserUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Current logged-in user not found"));

        if (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to update this user's profile");
        }

        if (!user.getEmail().equals(updateRequest.getEmail()) && userRepository.existsByEmail(updateRequest.getEmail())) {
            throw new BadRequestException("Email is already in use by another account");
        }

        user.setEmail(updateRequest.getEmail());
        user.setName(updateRequest.getName());

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
