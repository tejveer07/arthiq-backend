package com.arthiq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@arthiq.com");
            message.setTo(toEmail);
            message.setSubject("arthIQ - Email Verification OTP");
            message.setText("Your OTP for email verification is: " + otp +
                    "\n\nThis OTP is valid for 10 minutes." +
                    "\n\nIf you didn't request this, please ignore this email." +
                    "\n\nThanks,\narthIQ Team");

            mailSender.send(message);
            System.out.println("OTP email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send OTP email");
        }
    }
}
