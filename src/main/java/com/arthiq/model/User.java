package com.arthiq.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private Boolean verified = false;  // NEW: Email verification status

    private String otp;  // NEW: Store OTP temporarily

    private LocalDateTime otpGeneratedTime;  // NEW: OTP expiration tracking

    @OneToMany(mappedBy = "user")
    private List<Expense> expenses;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public LocalDateTime getOtpGeneratedTime() { return otpGeneratedTime; }
    public void setOtpGeneratedTime(LocalDateTime otpGeneratedTime) {
        this.otpGeneratedTime = otpGeneratedTime;
    }

    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }
}
