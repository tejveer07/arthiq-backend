package com.arthiq.controller;

import com.arthiq.dto.BudgetDto;
import com.arthiq.model.User;
import com.arthiq.service.BudgetService;
import com.arthiq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> saveBudget(@RequestBody BudgetDto budgetDto) {
        User user = userService.findById(budgetDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        BudgetDto savedDto = budgetService.saveBudget(budgetDto, user);
        return ResponseEntity.ok(savedDto);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetDto>> getBudgets(@PathVariable Long userId) {
        return ResponseEntity.ok(budgetService.getBudgetsByUser(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
