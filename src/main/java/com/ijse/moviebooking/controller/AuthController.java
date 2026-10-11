package com.ijse.moviebooking.controller;

import com.ijse.moviebooking.dto.request.SignInRequest;
import com.ijse.moviebooking.dto.request.SignUpRequest;
import com.ijse.moviebooking.dto.response.JwtResponse;
import com.ijse.moviebooking.dto.response.UserResponse;
import com.ijse.moviebooking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        UserResponse registeredUser = authService.registerUser(signUpRequest);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(signInRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}
