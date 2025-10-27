package com.arthiq.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TemplateDto {
    private Long id;
    private String name;
    private Double amount;
    private String category;
    private Long userId;

}
