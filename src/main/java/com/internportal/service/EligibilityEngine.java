package com.internportal.service;

import com.internportal.dao.ApplicationDAO;
import com.internportal.model.EligibilityResult;
import com.internportal.model.Internship;
import com.internportal.model.Skill;
import com.internportal.model.Student;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Core Eligibility Engine for post-graduate students.
 * Validates candidates against 6 strict institutional and corporate criteria.
 */
public class EligibilityEngine {

    private final ApplicationDAO applicationDAO;

    public EligibilityEngine() {
        this.applicationDAO = new ApplicationDAO();
    }

    public EligibilityEngine(ApplicationDAO applicationDAO) {
        this.applicationDAO = applicationDAO;
    }

    /**
     * Evaluates whether a student is eligible for an internship.
     *
     * @param student    The student candidate
     * @param internship The internship post
     * @return EligibilityResult containing eligible boolean and list of reasons if ineligible
     */
    public EligibilityResult checkEligibility(Student student, Internship internship) {
        EligibilityResult result = new EligibilityResult();
        result.setStudentId(student != null ? student.getId() : 0);
        result.setInternshipId(internship != null ? internship.getId() : 0);

        if (student == null || internship == null) {
            result.addReason("Invalid student or internship reference.");
            return result;
        }

        // 1. Minimum CGPA Check (student CGPA >= min_cgpa)
        if (internship.getMinCgpa() != null && student.getCgpa() != null) {
            if (student.getCgpa().compareTo(internship.getMinCgpa()) < 0) {
                result.addReason(String.format("Student CGPA (%.2f) is below required minimum CGPA (%.2f).",
                        student.getCgpa(), internship.getMinCgpa()));
            }
        }

        // 2. Course Check (course in allowed courses)
        if (internship.getAllowedCourses() != null && !internship.getAllowedCourses().trim().isEmpty()) {
            List<String> allowedCourses = Arrays.stream(internship.getAllowedCourses().split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());

            String studentCourse = student.getCourse() != null ? student.getCourse().trim().toUpperCase() : "";
            if (!allowedCourses.contains(studentCourse)) {
                result.addReason(String.format("Student course '%s' is not in allowed courses (%s).",
                        student.getCourse(), internship.getAllowedCourses()));
            }
        }

        // 3. Active Backlogs Check (active_backlogs <= max_backlogs)
        if (student.getActiveBacklogs() > internship.getMaxBacklogs()) {
            result.addReason(String.format("Student active backlogs (%d) exceed maximum allowed (%d).",
                    student.getActiveBacklogs(), internship.getMaxBacklogs()));
        }

        // 4. Minimum Semester Check (semester >= min_semester)
        if (student.getSemester() < internship.getMinSemester()) {
            result.addReason(String.format("Student semester (%d) is below required minimum semester (%d).",
                    student.getSemester(), internship.getMinSemester()));
        }

        // 5. Mandatory Skills Check (student has ALL mandatory skills)
        if (internship.getSkills() != null && !internship.getSkills().isEmpty()) {
            Set<Integer> studentSkillIds = new HashSet<>();
            if (student.getSkills() != null) {
                for (Skill s : student.getSkills()) {
                    studentSkillIds.add(s.getId());
                }
            }

            for (Skill reqSkill : internship.getSkills()) {
                if (reqSkill.isMandatory() && !studentSkillIds.contains(reqSkill.getId())) {
                    result.addReason(String.format("Missing required mandatory skill: %s.", reqSkill.getSkillName()));
                }
            }
        }

        // 6. Deadline Check & Duplicate Application Check
        if (internship.getDeadline() != null) {
            LocalDate deadlineDate = internship.getDeadline().toLocalDate();
            if (deadlineDate.isBefore(LocalDate.now())) {
                result.addReason(String.format("Application deadline passed on %s.", internship.getDeadline()));
            }
        }

        if ("CLOSED".equalsIgnoreCase(internship.getStatus())) {
            result.addReason("This internship is marked as CLOSED by the recruiter.");
        }

        if (student.getId() > 0 && internship.getId() > 0 && applicationDAO != null) {
            if (applicationDAO.hasStudentApplied(internship.getId(), student.getId())) {
                result.addReason("Student has already submitted an application for this internship.");
            }
        }

        // If no disqualifying reasons were recorded, student is eligible
        if (result.getReasons().isEmpty()) {
            result.setEligible(true);
        }

        return result;
    }
}
