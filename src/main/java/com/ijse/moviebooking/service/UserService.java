package com.ijse.moviebooking.service;

import com.ijse.moviebooking.dto.request.UserUpdateRequest;
import com.ijse.moviebooking.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest updateRequest, String currentUserUsername);
    void deleteUser(Long id);
}
