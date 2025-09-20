package com.innovactions.incident.domain.service;

import com.innovactions.incident.domain.model.Severity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ChannelNameGenerator {

    public String generateChannelName(Severity severity) {
        LocalDateTime now = LocalDateTime.now();
        
        String day = String.format("%02d", now.getDayOfMonth());
        String month = String.format("%02d", now.getMonthValue());
        String year = String.valueOf(now.getYear());
        String hour = String.format("%02d", now.getHour());
        String minute = String.format("%02d", now.getMinute());
        
        return String.format("%s_%s-%s-%s_%s-%s", 
                severity.name().toLowerCase(), 
                day, 
                month, 
                year, 
                hour, 
                minute);
    }
}