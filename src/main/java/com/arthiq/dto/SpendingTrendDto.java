package com.arthiq.dto;

public class SpendingTrendDto {
    private String period; // e.g., "2025-10" or "Week 42"
    private Double totalAmount;
    private Integer expenseCount;

    public SpendingTrendDto(String period, Double totalAmount, Integer expenseCount) {
        this.period = period;
        this.totalAmount = totalAmount;
        this.expenseCount = expenseCount;
    }

    // Getters and setters
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Integer getExpenseCount() { return expenseCount; }
    public void setExpenseCount(Integer expenseCount) { this.expenseCount = expenseCount; }
}
