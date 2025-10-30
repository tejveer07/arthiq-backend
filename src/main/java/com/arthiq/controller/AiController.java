package com.arthiq.controller;

import com.arthiq.dto.ChatRequest;
import com.arthiq.dto.ChatResponse;
import com.arthiq.security.UserDetailsImpl;
import com.arthiq.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        try {
            // Get authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            // Get AI response
            String aiReply = aiService.chat(userId, request.getMessage());

            // Get suggested questions
            List<String> suggestions = aiService.getSuggestedQuestions(userId);

            ChatResponse response = new ChatResponse(aiReply, suggestions);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error processing your request: " + e.getMessage());
        }
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            List<String> suggestions = aiService.getSuggestedQuestions(userId);

            return ResponseEntity.ok(suggestions);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
