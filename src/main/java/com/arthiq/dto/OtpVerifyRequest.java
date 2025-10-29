package com.arthiq.dto;

public class OtpVerifyRequest {
    private String email;
    private String otp;

    // Constructors
    public OtpVerifyRequest() {
    }

    public OtpVerifyRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
