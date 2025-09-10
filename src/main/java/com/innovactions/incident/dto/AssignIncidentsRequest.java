package com.innovactions.incident.dto;


import com.innovactions.incident.domain.model.Developer;
import com.innovactions.incident.domain.model.Incident;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignIncidentsRequest {
    List<Developer> teamMembers;
    List<Incident> incidents;
}
