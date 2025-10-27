package com.arthiq.repository;

import com.arthiq.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);
    List<Expense> findByUserIdAndCategory(Long userId, String category);
    List<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate start, LocalDate end);
}
