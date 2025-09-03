package com.innovactions.incident.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Incident {
    private String address;
    private String message;
    private Integer severity;
    private String status;
}
