package com.arthiq.service;

import com.arthiq.dto.ExpenseDto;
import com.arthiq.model.Expense;
import com.arthiq.model.User;
import com.arthiq.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    public ExpenseDto addExpense(ExpenseDto dto, User user) {
        Expense expense = convertToEntity(dto, user);
        Expense saved = expenseRepository.save(expense);
        return convertToDto(saved);
    }

    public List<ExpenseDto> getExpensesByUser(Long userId) {
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        return expenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ExpenseDto updateExpense(Long id, ExpenseDto dto, User user) {
        Expense expense = convertToEntity(dto, user);
        expense.setId(id);
        Expense updated = expenseRepository.save(expense);
        return convertToDto(updated);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public List<ExpenseDto> getExpensesByUserAndDateRange(Long userId, LocalDate start, LocalDate end) {
        List<Expense> expenses = expenseRepository.findByUserIdAndExpenseDateBetween(userId, start, end);
        return expenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Mapping methods
    public ExpenseDto convertToDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setTitle(expense.getTitle());
        dto.setAmount(expense.getAmount());
        dto.setCategory(expense.getCategory());
        dto.setDescription(expense.getDescription());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setMerchant(expense.getMerchant());
        dto.setRecurring(expense.getRecurring());
        dto.setUserId(expense.getUser().getId());
        return dto;
    }

    public Expense convertToEntity(ExpenseDto dto, User user) {
        Expense expense = new Expense();
        expense.setId(dto.getId());
        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setCategory(dto.getCategory());
        expense.setDescription(dto.getDescription());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setMerchant(dto.getMerchant());
        expense.setRecurring(dto.getRecurring());
        expense.setUser(user);
        return expense;
    }
}
