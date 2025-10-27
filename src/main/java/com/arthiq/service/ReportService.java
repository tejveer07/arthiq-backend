package com.arthiq.service;

import com.arthiq.dto.CategorySummaryDto;
import com.arthiq.dto.MonthlySummaryDto;
import com.arthiq.dto.SpendingTrendDto;
import com.arthiq.model.Expense;
import com.arthiq.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ExpenseRepository expenseRepository;

    // Monthly Summary Report
    public MonthlySummaryDto getMonthlySummary(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByUserIdAndExpenseDateBetween(userId, startDate, endDate);

        MonthlySummaryDto summary = new MonthlySummaryDto();
        summary.setMonth(startDate.getMonth().toString());
        summary.setYear(year);
        summary.setExpenseCount(expenses.size());
        summary.setTotalExpenses(expenses.stream().mapToDouble(Expense::getAmount).sum());

        // Category breakdown
        Map<String, Double> categoryBreakdown = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));
        summary.setCategoryBreakdown(categoryBreakdown);

        // Top merchants
        Map<String, Double> topMerchants = expenses.stream()
                .filter(e -> e.getMerchant() != null)
                .collect(Collectors.groupingBy(
                        Expense::getMerchant,
                        Collectors.summingDouble(Expense::getAmount)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        summary.setTopMerchants(topMerchants);

        return summary;
    }

    // Category-wise Summary
    public List<CategorySummaryDto> getCategorySummary(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = expenseRepository.findByUserIdAndExpenseDateBetween(userId, startDate, endDate);

        Double totalAmount = expenses.stream().mapToDouble(Expense::getAmount).sum();

        Map<String, List<Expense>> categoryMap = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory));

        List<CategorySummaryDto> summaries = new ArrayList<>();
        for (Map.Entry<String, List<Expense>> entry : categoryMap.entrySet()) {
            String category = entry.getKey();
            List<Expense> categoryExpenses = entry.getValue();
            Double categoryTotal = categoryExpenses.stream().mapToDouble(Expense::getAmount).sum();
            Integer count = categoryExpenses.size();

            CategorySummaryDto dto = new CategorySummaryDto(category, categoryTotal, count);
            dto.setPercentage(totalAmount > 0 ? (categoryTotal / totalAmount) * 100 : 0.0);
            summaries.add(dto);
        }

        // Sort by total amount descending
        summaries.sort((a, b) -> Double.compare(b.getTotalAmount(), a.getTotalAmount()));

        return summaries;
    }

    // Spending Trend (Last N months)
    public List<SpendingTrendDto> getSpendingTrend(Long userId, int months) {
        List<SpendingTrendDto> trends = new ArrayList<>();
        LocalDate endDate = LocalDate.now();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(endDate.minusMonths(i));
            LocalDate start = yearMonth.atDay(1);
            LocalDate end = yearMonth.atEndOfMonth();

            List<Expense> expenses = expenseRepository.findByUserIdAndExpenseDateBetween(userId, start, end);

            Double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
            String period = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            trends.add(new SpendingTrendDto(period, total, expenses.size()));
        }

        return trends;
    }

    // Top Spending Categories
    public List<CategorySummaryDto> getTopCategories(Long userId, int limit) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);

        List<CategorySummaryDto> categories = getCategorySummary(userId, startDate, endDate);
        return categories.stream().limit(limit).collect(Collectors.toList());
    }

    // Top Merchants
    public Map<String, Double> getTopMerchants(Long userId, int limit) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);

        List<Expense> expenses = expenseRepository.findByUserIdAndExpenseDateBetween(userId, startDate, endDate);

        return expenses.stream()
                .filter(e -> e.getMerchant() != null && !e.getMerchant().isEmpty())
                .collect(Collectors.groupingBy(
                        Expense::getMerchant,
                        Collectors.summingDouble(Expense::getAmount)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // Weekly Summary
    public MonthlySummaryDto getWeeklySummary(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(1);

        List<Expense> expenses = expenseRepository.findByUserIdAndExpenseDateBetween(userId, startDate, endDate);

        MonthlySummaryDto summary = new MonthlySummaryDto();
        summary.setMonth("Last 7 Days");
        summary.setYear(LocalDate.now().getYear());
        summary.setExpenseCount(expenses.size());
        summary.setTotalExpenses(expenses.stream().mapToDouble(Expense::getAmount).sum());

        Map<String, Double> categoryBreakdown = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));
        summary.setCategoryBreakdown(categoryBreakdown);

        return summary;
    }
}
