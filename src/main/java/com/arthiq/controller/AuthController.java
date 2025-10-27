package com.arthiq.controller;

import com.arthiq.dto.*;
import com.arthiq.model.User;
import com.arthiq.security.UserDetailsImpl;
import com.arthiq.service.OtpService;
import com.arthiq.service.UserService;
import com.arthiq.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    OtpService otpService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    // Step 1: Register user (sends OTP)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto registerRequest) {
        // Check if email already exists
        if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // Create user with unverified status
        UserDto userDto = new UserDto();
        userDto.setName(registerRequest.getName());
        userDto.setEmail(registerRequest.getEmail());

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        UserDto newUser = userService.register(userDto, encodedPassword);

        // Send OTP to email
        otpService.sendOtp(registerRequest.getEmail());

        return ResponseEntity.ok("User registered. Please verify your email with the OTP sent.");
    }

    // Step 2: Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyDto otpVerifyDto) {
        try {
            boolean verified = otpService.verifyOtp(otpVerifyDto.getEmail(), otpVerifyDto.getOtp());
            if (verified) {
                return ResponseEntity.ok("Email verified successfully. You can now login.");
            }
            return ResponseEntity.badRequest().body("Invalid OTP");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Resend OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody OtpRequestDto otpRequest) {
        try {
            otpService.resendOtp(otpRequest.getEmail());
            return ResponseEntity.ok("OTP resent successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Login (only for verified users)
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto loginRequest) {
        // Check if user is verified
        User user = userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Please verify your email before logging in");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getEmail())); // Update with actual password field

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(loginRequest.getEmail());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserDto userDto = userService.convertToDto(userDetails.getUser());

        return ResponseEntity.ok(new JwtResponse(jwt, userDto));
    }

    // JWT Response DTO
    public static class JwtResponse {
        private String token;
        private UserDto user;

        public JwtResponse(String token, UserDto user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() { return token; }
        public UserDto getUser() { return user; }
    }
}
