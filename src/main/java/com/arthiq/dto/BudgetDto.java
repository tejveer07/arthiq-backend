package com.arthiq.dto;

public class BudgetDto {
    private Long id;
    private String category;
    private Double monthlyLimit;
    private Double alertThreshold;
    private Long userId;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(Double monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public Double getAlertThreshold() { return alertThreshold; }
    public void setAlertThreshold(Double alertThreshold) { this.alertThreshold = alertThreshold; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
