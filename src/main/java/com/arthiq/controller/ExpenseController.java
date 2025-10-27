package com.arthiq.controller;

import com.arthiq.dto.ExpenseDto;
import com.arthiq.model.User;
import com.arthiq.service.ExpenseService;
import com.arthiq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody ExpenseDto expenseDto) {
        User user = userService.findById(expenseDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ExpenseDto savedDto = expenseService.addExpense(expenseDto, user);
        return ResponseEntity.ok(savedDto);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseDto>> getExpensesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getExpensesByUser(userId));
    }

    @GetMapping("/user/{userId}/date")
    public ResponseEntity<List<ExpenseDto>> getExpensesByDateRange(
            @PathVariable Long userId,
            @RequestParam String start,
            @RequestParam String end) {

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return ResponseEntity.ok(expenseService.getExpensesByUserAndDateRange(userId, startDate, endDate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(
            @PathVariable Long id,
            @RequestBody ExpenseDto expenseDto) {

        User user = userService.findById(expenseDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ExpenseDto updatedDto = expenseService.updateExpense(id, expenseDto, user);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
