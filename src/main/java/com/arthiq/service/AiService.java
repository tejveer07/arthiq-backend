package com.arthiq.service;

import com.arthiq.model.Expense;
import com.arthiq.repository.ExpenseRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final WebClient webClient;
    private final ExpenseRepository expenseRepository;
    private final ObjectMapper objectMapper;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    public AiService(ExpenseRepository expenseRepository, ObjectMapper objectMapper) {
        this.expenseRepository = expenseRepository;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .baseUrl(GROQ_API_URL)
                .build();
    }

    public String chat(Long userId, String userMessage) {
        try {
            System.out.println("Starting AI chat for user: " + userId);
            System.out.println("User message: " + userMessage);

            // Get user's expense data
            List<Expense> expenses = expenseRepository.findByUserId(userId);
            System.out.println("Found " + expenses.size() + " expenses");

            // Build context with user data
            String context = buildExpenseContext(expenses);
            System.out.println("Context built successfully");

            // Create system prompt with context
            String systemPrompt = buildSystemPrompt(context);

            // Build request body as proper JSON structure
            Map<String, Object> message1 = new HashMap<>();
            message1.put("role", "system");
            message1.put("content", systemPrompt);

            Map<String, Object> message2 = new HashMap<>();
            message2.put("role", "user");
            message2.put("content", userMessage);

            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(message1);
            messages.add(message2);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);

            System.out.println("Calling Groq API...");
            System.out.println("API Key present: " + (groqApiKey != null && !groqApiKey.isEmpty()));

            // Call Groq API with proper error handling
            String response = webClient.post()
                    .uri("")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(error -> {
                        System.err.println("Groq API Error: " + error.getMessage());
                        if (error instanceof WebClientResponseException) {
                            WebClientResponseException wcre = (WebClientResponseException) error;
                            System.err.println("Status: " + wcre.getStatusCode());
                            System.err.println("Response: " + wcre.getResponseBodyAsString());
                        }
                    })
                    .block();

            System.out.println("Got response from Groq API");

            // Parse response
            JsonNode jsonNode = objectMapper.readTree(response);
            String aiReply = jsonNode.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            System.out.println("AI Reply: " + aiReply);

            return aiReply;

        } catch (WebClientResponseException e) {
            System.err.println("Groq API Error - Status: " + e.getStatusCode());
            System.err.println("Response body: " + e.getResponseBodyAsString());
            return "Sorry, I couldn't process your request. The AI service returned an error.";
        } catch (Exception e) {
            System.err.println("Error in AI chat: " + e.getMessage());
            e.printStackTrace();
            return "Sorry, I couldn't process your request. Please try again.";
        }
    }

    private String buildExpenseContext(List<Expense> expenses) {
        if (expenses.isEmpty()) {
            return "User has no expenses yet.";
        }

        // Calculate totals
        double totalSpent = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        // Group by category
        Map<String, Double> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        // Get current month expenses
        LocalDate now = LocalDate.now();
        double monthlyTotal = expenses.stream()
                .filter(e -> {
                    LocalDate expenseDate = e.getExpenseDate();
                    return expenseDate.getMonth() == now.getMonth()
                            && expenseDate.getYear() == now.getYear();
                })
                .mapToDouble(Expense::getAmount)
                .sum();

        // Get last month for comparison
        LocalDate lastMonth = now.minusMonths(1);
        double lastMonthTotal = expenses.stream()
                .filter(e -> {
                    LocalDate expenseDate = e.getExpenseDate();
                    return expenseDate.getMonth() == lastMonth.getMonth()
                            && expenseDate.getYear() == lastMonth.getYear();
                })
                .mapToDouble(Expense::getAmount)
                .sum();

        // Top merchants
        Map<String, Double> merchantTotals = expenses.stream()
                .filter(e -> e.getMerchant() != null && !e.getMerchant().isEmpty())
                .collect(Collectors.groupingBy(
                        Expense::getMerchant,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        // Recent expenses (last 5)
        List<Expense> recentExpenses = expenses.stream()
                .sorted(Comparator.comparing(Expense::getExpenseDate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Build context string
        StringBuilder context = new StringBuilder();
        context.append("EXPENSE DATA SUMMARY:\n\n");
        context.append("Total Expenses: Rs.").append(String.format("%.2f", totalSpent)).append("\n");
        context.append("Total Transactions: ").append(expenses.size()).append("\n\n");

        context.append("Current Month (").append(now.getMonth()).append(" ").append(now.getYear()).append("): Rs.")
                .append(String.format("%.2f", monthlyTotal)).append("\n");
        context.append("Last Month: Rs.").append(String.format("%.2f", lastMonthTotal)).append("\n");

        if (lastMonthTotal > 0) {
            double change = ((monthlyTotal - lastMonthTotal) / lastMonthTotal) * 100;
            context.append("Change: ").append(String.format("%.1f%%", change)).append("\n\n");
        }

        context.append("SPENDING BY CATEGORY:\n");
        categoryTotals.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    double percentage = (entry.getValue() / totalSpent) * 100;
                    context.append("- ").append(entry.getKey()).append(": Rs.")
                            .append(String.format("%.2f", entry.getValue()))
                            .append(" (").append(String.format("%.1f%%", percentage)).append(")\n");
                });

        if (!merchantTotals.isEmpty()) {
            context.append("\nTOP MERCHANTS:\n");
            merchantTotals.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry -> {
                        context.append("- ").append(entry.getKey()).append(": Rs.")
                                .append(String.format("%.2f", entry.getValue())).append("\n");
                    });
        }

        context.append("\nRECENT EXPENSES:\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        recentExpenses.forEach(expense -> {
            context.append("- ").append(expense.getTitle())
                    .append(" (").append(expense.getCategory()).append("): Rs.")
                    .append(String.format("%.2f", expense.getAmount()))
                    .append(" on ").append(expense.getExpenseDate().format(formatter)).append("\n");
        });

        return context.toString();
    }

    private String buildSystemPrompt(String context) {
        return "You are arthIQ Assistant, a friendly and intelligent expense tracking AI assistant. " +
                "Your role is to help users understand their spending habits, provide insights, " +
                "and offer practical financial advice.\n\n" +
                "USER'S EXPENSE DATA:\n" + context + "\n\n" +
                "GUIDELINES:\n" +
                "- Answer questions about spending clearly and concisely\n" +
                "- Use Indian Rupee (Rs.) for all amounts\n" +
                "- Provide actionable insights and suggestions\n" +
                "- Be encouraging but honest about spending patterns\n" +
                "- Format responses with bullet points when listing items\n" +
                "- Keep responses under 200 words\n" +
                "- If asked about specific expenses, refer to the data above\n" +
                "- If data is missing, politely inform the user\n\n" +
                "Always be helpful, accurate, and supportive!";
    }

    public List<String> getSuggestedQuestions(Long userId) {
        List<Expense> expenses = expenseRepository.findByUserId(userId);

        List<String> suggestions = new ArrayList<>();

        if (expenses.isEmpty()) {
            suggestions.add("How do I start tracking expenses?");
            suggestions.add("What categories should I use?");
            suggestions.add("Tips for better expense management");
        } else {
            suggestions.add("How much did I spend this month?");
            suggestions.add("What's my biggest expense category?");
            suggestions.add("Show my spending trends");
            suggestions.add("Compare this month to last month");
            suggestions.add("Give me budget recommendations");
        }

        return suggestions;
    }
}
