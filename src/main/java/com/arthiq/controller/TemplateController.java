package com.arthiq.controller;

import com.arthiq.dto.TemplateDto;
import com.arthiq.model.User;
import com.arthiq.service.TemplateService;
import com.arthiq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> addTemplate(@RequestBody TemplateDto templateDto) {
        User user = userService.findById(templateDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        TemplateDto savedDto = templateService.saveTemplate(templateDto, user);
        return ResponseEntity.ok(savedDto);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TemplateDto>> getTemplates(@PathVariable Long userId) {
        return ResponseEntity.ok(templateService.getTemplatesByUser(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
