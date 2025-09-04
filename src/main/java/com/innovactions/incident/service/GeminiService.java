package com.innovactions.incident.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService() {
        // Reads GEMINI_API_KEY from environment variable
        this.client = new Client();
    }

    public String generateText(String prompt) {
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null
        );
        return response.text();
    }
}
