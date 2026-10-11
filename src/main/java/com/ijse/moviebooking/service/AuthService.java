package com.ijse.moviebooking.service;

import com.ijse.moviebooking.dto.request.SignInRequest;
import com.ijse.moviebooking.dto.request.SignUpRequest;
import com.ijse.moviebooking.dto.response.JwtResponse;
import com.ijse.moviebooking.dto.response.UserResponse;

public interface AuthService {
    JwtResponse authenticateUser(SignInRequest signInRequest);
    UserResponse registerUser(SignUpRequest signUpRequest);
}
