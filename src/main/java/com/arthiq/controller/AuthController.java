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

import java.util.Optional;

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
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto registerRequest) {
//        // Check if email already exists
//        if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
//            return ResponseEntity.badRequest().body("Email already registered");
//        }
//
//        // Create user with unverified status
//        UserDto userDto = new UserDto();
//        userDto.setName(registerRequest.getName());
//        userDto.setEmail(registerRequest.getEmail());
//
//        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
//        UserDto newUser = userService.register(userDto, encodedPassword);
//
//        // Send OTP to email
//        otpService.sendOtp(registerRequest.getEmail());
//
//        return ResponseEntity.ok("Please verify your email with the OTP sent.");
//    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto registerRequest) {
        Optional<User> existingUser = userService.findByEmail(registerRequest.getEmail());

        // Check if user exists and is verified
        if (existingUser.isPresent()) {
            if (existingUser.get().getVerified()) {
                // User is verified - cannot re-register
                return ResponseEntity.badRequest().body("Email already registered and verified. Please login.");
            } else {
                // User exists but not verified - resend OTP
                try {
                    otpService.sendOtp(registerRequest.getEmail());
                    return ResponseEntity.ok("Account already exists but not verified. New OTP sent to your email.");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to resend OTP. Please try again.");
                }
            }
        }

        // New user - proceed with registration
        UserDto userDto = new UserDto();
        userDto.setName(registerRequest.getName());
        userDto.setEmail(registerRequest.getEmail());

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        UserDto newUser = userService.register(userDto, encodedPassword);

        try {
            otpService.sendOtp(registerRequest.getEmail());
            return ResponseEntity.ok("User registered. Please verify your email with the OTP sent.");
        } catch (Exception e) {
            return ResponseEntity.ok("User registered. Email service unavailable - please contact support.");
        }
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
//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@RequestBody UserDto loginRequest) {
//        // Check if user exists and is verified
//        User user = userService.findByEmail(loginRequest.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!user.getVerified()) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Please verify your email before logging in");
//        }
//
//        // Authenticate with email and raw password
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getEmail(),
//                        loginRequest.getPassword()));  // Use password from request
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = jwtUtil.generateJwtToken(loginRequest.getEmail());
//
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        UserDto userDto = userService.convertToDto(userDetails.getUser());
//
//        return ResponseEntity.ok(new JwtResponse(jwt, userDto));
//    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto loginRequest) {
        try {
            // Check if user exists
            User user = userService.findByEmail(loginRequest.getEmail())
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Email is not registered. Please register first.");
            }

            // Check if user is verified
            if (!user.getVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Please verify your email before logging in");
            }

            // Authenticate with email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(loginRequest.getEmail());

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            UserDto userDto = userService.convertToDto(userDetails.getUser());

            return ResponseEntity.ok(new JwtResponse(jwt, userDto));

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
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
