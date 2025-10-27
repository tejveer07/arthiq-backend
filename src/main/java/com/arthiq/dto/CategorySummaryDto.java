package com.arthiq.dto;

public class CategorySummaryDto {
    private String category;
    private Double totalAmount;
    private Integer expenseCount;
    private Double percentage;

    public CategorySummaryDto(String category, Double totalAmount, Integer expenseCount) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.expenseCount = expenseCount;
    }

    // Getters and setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Integer getExpenseCount() { return expenseCount; }
    public void setExpenseCount(Integer expenseCount) { this.expenseCount = expenseCount; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
}
