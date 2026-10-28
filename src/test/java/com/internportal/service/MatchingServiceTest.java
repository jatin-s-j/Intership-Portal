package com.internportal.service;

import com.internportal.dao.ConfigDAO;
import com.internportal.model.Internship;
import com.internportal.model.InternshipMatch;
import com.internportal.model.Skill;
import com.internportal.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class MatchingServiceTest {

    private ConfigDAO configDAO;
    private EligibilityEngine eligibilityEngine;
    private MatchingService matchingService;

    @BeforeEach
    public void setUp() {
        configDAO = Mockito.mock(ConfigDAO.class);
        eligibilityEngine = Mockito.mock(EligibilityEngine.class);

        Map<String, Double> weights = new HashMap<>();
        weights.put("weight_skill", 0.50);
        weights.put("weight_cgpa", 0.25);
        weights.put("weight_project", 0.15);
        weights.put("weight_location", 0.10);
        when(configDAO.getMatchingWeights()).thenReturn(weights);

        matchingService = new MatchingService(configDAO, eligibilityEngine);
    }

    @Test
    @DisplayName("Calculate perfect match score when all attributes align")
    public void testHighMatchScore() {
        Student s = new Student();
        s.setId(1);
        s.setCgpa(new BigDecimal("10.00"));
        s.setPreferredLocation("Bangalore");
        s.setProjects("Java Spring Boot microservices and MySQL backend systems");

        List<Skill> sSkills = new ArrayList<>();
        Skill s1 = new Skill();
        s1.setId(1);
        s1.setSkillName("Java");
        s1.setProficiencyLevel("Advanced");
        sSkills.add(s1);
        s.setSkills(sSkills);

        Internship i = new Internship();
        i.setId(1);
        i.setTitle("Java Developer");
        i.setDescription("Build Spring Boot applications");
        i.setLocation("Bangalore");

        List<Skill> iSkills = new ArrayList<>();
        Skill i1 = new Skill();
        i1.setId(1);
        i1.setSkillName("Java");
        i1.setMandatory(true);
        iSkills.add(i1);
        i.setSkills(iSkills);

        InternshipMatch match = matchingService.calculateMatch(s, i);
        assertTrue(match.getMatchScore() >= 90.0, "Perfect alignment should produce a match score above 90%");
    }

    @Test
    @DisplayName("Location score calculation with regional proximity")
    public void testLocationScore() {
        double exact = matchingService.calculateLocationScore("Bangalore", "Bangalore");
        assertEquals(100.0, exact, 0.01);

        double southProximity = matchingService.calculateLocationScore("Bangalore", "Hyderabad");
        assertEquals(75.0, southProximity, 0.01);

        double distant = matchingService.calculateLocationScore("Delhi", "Bangalore");
        assertEquals(40.0, distant, 0.01);
    }
}

