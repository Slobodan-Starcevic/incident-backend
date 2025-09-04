package com.innovactions.incident.controller;

import com.innovactions.incident.service.GeminiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping("/generate")
    public String generate(@RequestParam(defaultValue = "Explain how AI works in a few words") String prompt) {
        return geminiService.generateText(prompt);
    }
}
