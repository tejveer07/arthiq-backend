package com.arthiq.service;

import com.arthiq.dto.BudgetDto;
import com.arthiq.model.Budget;
import com.arthiq.model.User;
import com.arthiq.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;

    public BudgetDto saveBudget(BudgetDto dto, User user) {
        Budget budget = convertToEntity(dto, user);
        Budget saved = budgetRepository.save(budget);
        return convertToDto(saved);
    }

    public List<BudgetDto> getBudgetsByUser(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return budgets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    // Mapping methods
    public BudgetDto convertToDto(Budget budget) {
        BudgetDto dto = new BudgetDto();
        dto.setId(budget.getId());
        dto.setCategory(budget.getCategory());
        dto.setMonthlyLimit(budget.getMonthlyLimit());
        dto.setAlertThreshold(budget.getAlertThreshold());
        dto.setUserId(budget.getUser().getId());
        return dto;
    }

    public Budget convertToEntity(BudgetDto dto, User user) {
        Budget budget = new Budget();
        budget.setId(dto.getId());
        budget.setCategory(dto.getCategory());
        budget.setMonthlyLimit(dto.getMonthlyLimit());
        budget.setAlertThreshold(dto.getAlertThreshold());
        budget.setUser(user);
        return budget;
    }
}
