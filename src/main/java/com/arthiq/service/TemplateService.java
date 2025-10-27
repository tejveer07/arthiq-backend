package com.arthiq.service;

import com.arthiq.dto.TemplateDto;
import com.arthiq.model.Template;
import com.arthiq.model.User;
import com.arthiq.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TemplateService {
    @Autowired
    private TemplateRepository templateRepository;

    public TemplateDto saveTemplate(TemplateDto dto, User user) {
        Template template = convertToEntity(dto, user);
        Template saved = templateRepository.save(template);
        return convertToDto(saved);
    }

    public List<TemplateDto> getTemplatesByUser(Long userId) {
        List<Template> templates = templateRepository.findByUserId(userId);
        return templates.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteTemplate(Long id) {
        templateRepository.deleteById(id);
    }

    // Mapping methods
    public TemplateDto convertToDto(Template template) {
        TemplateDto dto = new TemplateDto();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setAmount(template.getAmount());
        dto.setCategory(template.getCategory());
        dto.setUserId(template.getUser().getId());
        return dto;
    }

    public Template convertToEntity(TemplateDto dto, User user) {
        Template template = new Template();
        template.setId(dto.getId());
        template.setName(dto.getName());
        template.setAmount(dto.getAmount());
        template.setCategory(dto.getCategory());
        template.setUser(user);
        return template;
    }
}
