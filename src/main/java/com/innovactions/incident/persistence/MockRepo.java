package com.innovactions.incident.persistence;

import com.innovactions.incident.business.Incident;
import com.innovactions.incident.business.TeamMember;

import java.util.ArrayList;
import java.util.List;

public class MockRepo {
    public List<Incident> findAllIncidents(){
        List<Incident> incidents = new ArrayList<>();
        incidents.add(new Incident("client1","bugs",2, "pending"));
        incidents.add(new Incident("client2","downtime",5, "pending"));
        incidents.add(new Incident("client3","login issues",4, "pending"));
        incidents.add(new Incident("client4","downtime",5, "pending"));
        return incidents;
    }
    public List<TeamMember> findAllPersons(){
        List<TeamMember> teamMembers = new ArrayList<>();
        teamMembers.add(new TeamMember("John","available"));
        teamMembers.add(new TeamMember("Mona","available"));
        teamMembers.add(new TeamMember("Kurt","available"));
        return teamMembers;
    }
}
