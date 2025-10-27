package com.arthiq.dto;

import java.util.Map;

public class MonthlySummaryDto {
    private String month;
    private Integer year;
    private Double totalExpenses;
    private Integer expenseCount;
    private Map<String, Double> categoryBreakdown;
    private Map<String, Double> topMerchants;

    // Getters and setters
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Double getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(Double totalExpenses) { this.totalExpenses = totalExpenses; }

    public Integer getExpenseCount() { return expenseCount; }
    public void setExpenseCount(Integer expenseCount) { this.expenseCount = expenseCount; }

    public Map<String, Double> getCategoryBreakdown() { return categoryBreakdown; }
    public void setCategoryBreakdown(Map<String, Double> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public Map<String, Double> getTopMerchants() { return topMerchants; }
    public void setTopMerchants(Map<String, Double> topMerchants) {
        this.topMerchants = topMerchants;
    }
}
