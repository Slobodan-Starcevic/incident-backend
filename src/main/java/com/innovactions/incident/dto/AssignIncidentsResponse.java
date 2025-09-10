package com.innovactions.incident.dto;


import com.innovactions.incident.domain.model.Dispatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignIncidentsResponse {
    private List<Dispatch> assignedTasks;
}
