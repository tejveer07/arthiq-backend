package com.arthiq.controller;

import com.arthiq.dto.CategorySummaryDto;
import com.arthiq.dto.MonthlySummaryDto;
import com.arthiq.dto.SpendingTrendDto;
import com.arthiq.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Get Monthly Summary
    @GetMapping("/monthly/{userId}")
    public ResponseEntity<MonthlySummaryDto> getMonthlySummary(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(reportService.getMonthlySummary(userId, year, month));
    }

    // Get Weekly Summary
    @GetMapping("/weekly/{userId}")
    public ResponseEntity<MonthlySummaryDto> getWeeklySummary(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getWeeklySummary(userId));
    }

    // Get Category Summary
    @GetMapping("/categories/{userId}")
    public ResponseEntity<List<CategorySummaryDto>> getCategorySummary(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getCategorySummary(userId, startDate, endDate));
    }

    // Get Spending Trend
    @GetMapping("/trend/{userId}")
    public ResponseEntity<List<SpendingTrendDto>> getSpendingTrend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(reportService.getSpendingTrend(userId, months));
    }

    // Get Top Categories
    @GetMapping("/top-categories/{userId}")
    public ResponseEntity<List<CategorySummaryDto>> getTopCategories(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reportService.getTopCategories(userId, limit));
    }

    // Get Top Merchants
    @GetMapping("/top-merchants/{userId}")
    public ResponseEntity<Map<String, Double>> getTopMerchants(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reportService.getTopMerchants(userId, limit));
    }
}
