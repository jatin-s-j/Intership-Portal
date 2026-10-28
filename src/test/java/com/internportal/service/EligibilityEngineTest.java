package com.internportal.service;

import com.internportal.dao.ApplicationDAO;
import com.internportal.model.EligibilityResult;
import com.internportal.model.Internship;
import com.internportal.model.Skill;
import com.internportal.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class EligibilityEngineTest {

    private ApplicationDAO applicationDAO;
    private EligibilityEngine engine;

    @BeforeEach
    public void setUp() {
        applicationDAO = Mockito.mock(ApplicationDAO.class);
        when(applicationDAO.hasStudentApplied(anyInt(), anyInt())).thenReturn(false);
        engine = new EligibilityEngine(applicationDAO);
    }

    private Student createStandardStudent() {
        Student s = new Student();
        s.setId(1);
        s.setFullName("Rahul Sharma");
        s.setCourse("MCA");
        s.setSemester(3);
        s.setCgpa(new BigDecimal("8.50"));
        s.setActiveBacklogs(0);

        List<Skill> skills = new ArrayList<>();
        Skill java = new Skill();
        java.setId(1);
        java.setSkillName("Java");
        skills.add(java);

        s.setSkills(skills);
        return s;
    }

    private Internship createStandardInternship() {
        Internship i = new Internship();
        i.setId(10);
        i.setTitle("Java Intern");
        i.setMinCgpa(new BigDecimal("7.00"));
        i.setMinSemester(2);
        i.setMaxBacklogs(0);
        i.setAllowedCourses("MCA,M.Tech");
        i.setDeadline(Date.valueOf(LocalDate.now().plusMonths(1)));
        i.setStatus("OPEN");

        List<Skill> skills = new ArrayList<>();
        Skill java = new Skill();
        java.setId(1);
        java.setSkillName("Java");
        java.setMandatory(true);
        skills.add(java);

        i.setSkills(skills);
        return i;
    }

    @Test
    @DisplayName("Eligible student meets all corporate criteria")
    public void testEligibleStudent() {
        Student s = createStandardStudent();
        Internship i = createStandardInternship();

        EligibilityResult result = engine.checkEligibility(s, i);
        assertTrue(result.isEligible(), "Student should be eligible");
        assertTrue(result.getReasons().isEmpty());
    }

    @Test
    @DisplayName("Student with lower CGPA is marked ineligible")
    public void testIneligibleLowCgpa() {
        Student s = createStandardStudent();
        s.setCgpa(new BigDecimal("6.20"));
        Internship i = createStandardInternship();

        EligibilityResult result = engine.checkEligibility(s, i);
        assertFalse(result.isEligible(), "Student should be ineligible due to low CGPA");
        assertFalse(result.getReasons().isEmpty());
    }

    @Test
    @DisplayName("Student with active backlogs exceeding allowed limit is rejected")
    public void testIneligibleBacklogs() {
        Student s = createStandardStudent();
        s.setActiveBacklogs(1);
        Internship i = createStandardInternship();

        EligibilityResult result = engine.checkEligibility(s, i);
        assertFalse(result.isEligible(), "Student should be ineligible due to active backlog");
    }

    @Test
    @DisplayName("Student missing mandatory skill is rejected")
    public void testIneligibleMissingSkill() {
        Student s = createStandardStudent();
        s.setSkills(new ArrayList<>()); // no skills
        Internship i = createStandardInternship();

        EligibilityResult result = engine.checkEligibility(s, i);
        assertFalse(result.isEligible(), "Student should be ineligible due to missing mandatory Java skill");
    }
}

