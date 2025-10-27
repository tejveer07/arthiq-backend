package com.arthiq.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExpenseDto {
    private Long id;
    private String title;
    private Double amount;
    private String category;
    private String description;
    private LocalDate expenseDate;
    private String merchant;
    private Boolean recurring;
    private Long userId;

    // Getters and setters
}
