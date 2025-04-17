package com.esprit.microservice.pfespace.Entities;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MatchProjectsResponse {
    private List<Map<String, Object>> matchedProjects;
    private List<String> keySkills;
    private String cvText;

    public MatchProjectsResponse() {
    }

    public MatchProjectsResponse(List<Map<String, Object>> matchedProjects, List<String> keySkills, String cvText) {
        this.matchedProjects = matchedProjects;
        this.keySkills = keySkills;
        this.cvText = cvText;
    }

    public List<String> getKeySkills() {
        return keySkills;
    }

    public void setKeySkills(List<String> keySkills) {
        this.keySkills = keySkills;
    }

    public List<Map<String, Object>> getMatchedProjects() {
        return matchedProjects;
    }

    public void setMatchedProjects(List<Map<String, Object>> matchedProjects) {
        this.matchedProjects = matchedProjects;
    }

    public String getCvText() {
        return cvText;
    }

    public void setCvText(String cvText) {
        this.cvText = cvText;
    }
}
