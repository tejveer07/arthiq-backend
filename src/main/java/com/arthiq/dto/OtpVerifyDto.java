package com.arthiq.dto;

public class OtpVerifyDto {
    private String email;
    private String otp;

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
