package com.arthiq.service;

import com.arthiq.model.User;
import com.arthiq.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Value("${otp.expiration.minutes:10}")
    private int otpExpirationMinutes;

    // Generate 6-digit OTP
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Send OTP to user email
    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (user.getVerified()) {
            throw new RuntimeException("Email already verified");
        }

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendOtpEmail(email, otp);
    }

    // Verify OTP
    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getVerified()) {
            throw new RuntimeException("Email already verified");
        }

        if (user.getOtp() == null) {
            throw new RuntimeException("No OTP found. Please request a new OTP");
        }

        // Check if OTP expired
        LocalDateTime otpGeneratedTime = user.getOtpGeneratedTime();
        if (otpGeneratedTime.plusMinutes(otpExpirationMinutes).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired. Please request a new OTP");
        }

        // Verify OTP
        if (user.getOtp().equals(otp)) {
            user.setVerified(true);
            user.setOtp(null);  // Clear OTP after verification
            user.setOtpGeneratedTime(null);
            userRepository.save(user);
            return true;
        }

        throw new RuntimeException("Invalid OTP");
    }

    // Resend OTP
    public void resendOtp(String email) {
        sendOtp(email);
    }
}
