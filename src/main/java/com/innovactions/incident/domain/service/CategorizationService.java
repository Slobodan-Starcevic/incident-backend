package com.innovactions.incident.domain.service;

import org.springframework.stereotype.Service;


@Service
public class CategorizationService {

    public SeverityEnum categorize(String text) {
        var lowerText = text == null ? "" : text.toLowerCase();

        if (lowerText.contains("urgent") || lowerText.contains("critical")) {
            return SeverityEnum.S1;
        }
        if (lowerText.contains("fail") || lowerText.contains("error") ||
                lowerText.contains("down") || lowerText.contains("broken")) {
            return SeverityEnum.S2;
        }
        return SeverityEnum.S3;
    }
}
