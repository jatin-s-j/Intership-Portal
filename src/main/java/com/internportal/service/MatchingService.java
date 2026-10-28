package com.internportal.service;

import com.internportal.dao.ConfigDAO;
import com.internportal.model.EligibilityResult;
import com.internportal.model.Internship;
import com.internportal.model.InternshipMatch;
import com.internportal.model.Skill;
import com.internportal.model.Student;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Intelligent Matching Service for Internships and Applicants.
 * Computes multi-attribute compatibility scores (0 - 100) based on dynamic admin weights.
 */
public class MatchingService {

    private final ConfigDAO configDAO;
    private final EligibilityEngine eligibilityEngine;

    // Default configuration weights
    public static final double DEFAULT_WEIGHT_SKILL = 0.50;
    public static final double DEFAULT_WEIGHT_CGPA = 0.25;
    public static final double DEFAULT_WEIGHT_PROJECT = 0.15;
    public static final double DEFAULT_WEIGHT_LOCATION = 0.10;

    public MatchingService() {
        this.configDAO = new ConfigDAO();
        this.eligibilityEngine = new EligibilityEngine();
    }

    public MatchingService(ConfigDAO configDAO, EligibilityEngine eligibilityEngine) {
        this.configDAO = configDAO;
        this.eligibilityEngine = eligibilityEngine;
    }

    /**
     * Calculates the comprehensive matching score and breakdown for a student and an internship.
     */
    public InternshipMatch calculateMatch(Student student, Internship internship) {
        InternshipMatch match = new InternshipMatch();
        match.setStudent(student);
        match.setInternship(internship);

        if (student == null || internship == null) {
            match.setMatchScore(0.0);
            match.setEligible(false);
            return match;
        }

        // Determine eligibility first
        EligibilityResult eligibility = eligibilityEngine.checkEligibility(student, internship);
        match.setEligible(eligibility.isEligible());
        match.setReasons(eligibility.getReasons());

        // Read dynamic weights from database
        Map<String, Double> weights = configDAO != null ? configDAO.getMatchingWeights() : getFallbackWeights();
        double wSkill = weights.getOrDefault("weight_skill", DEFAULT_WEIGHT_SKILL);
        double wCgpa = weights.getOrDefault("weight_cgpa", DEFAULT_WEIGHT_CGPA);
        double wProject = weights.getOrDefault("weight_project", DEFAULT_WEIGHT_PROJECT);
        double wLocation = weights.getOrDefault("weight_location", DEFAULT_WEIGHT_LOCATION);

        // Normalize weights if sum != 1.0
        double totalWeight = wSkill + wCgpa + wProject + wLocation;
        if (totalWeight > 0 && Math.abs(totalWeight - 1.0) > 0.001) {
            wSkill /= totalWeight;
            wCgpa /= totalWeight;
            wProject /= totalWeight;
            wLocation /= totalWeight;
        }

        // Sub-score 1: Skill Match (0 - 100)
        double skillScore = calculateSkillScore(student.getSkills(), internship.getSkills());
        match.setSkillMatchScore(skillScore);

        // Sub-score 2: CGPA Score (0 - 100)
        double cgpaScore = calculateCgpaScore(student.getCgpa());
        match.setCgpaScore(cgpaScore);

        // Sub-score 3: Project Relevance (0 - 100)
        double projectScore = calculateProjectRelevance(student.getProjects(), internship);
        match.setProjectRelevanceScore(projectScore);

        // Sub-score 4: Location Preference (0 - 100)
        double locationScore = calculateLocationScore(student.getPreferredLocation(), internship.getLocation());
        match.setLocationScore(locationScore);

        // Total Weighted Score
        double overallScore = (wSkill * skillScore) + (wCgpa * cgpaScore) +
                              (wProject * projectScore) + (wLocation * locationScore);

        // Constrain score between 0.0 and 100.0
        overallScore = Math.max(0.0, Math.min(100.0, overallScore));
        match.setMatchScore(overallScore);

        return match;
    }

    /**
     * Ranks a list of internships for a student, returning eligible ones prioritized with match scores.
     */
    public List<InternshipMatch> rankInternshipsForStudent(Student student, List<Internship> internships) {
        List<InternshipMatch> matches = new ArrayList<>();
        if (student == null || internships == null) {
            return matches;
        }

        for (Internship i : internships) {
            InternshipMatch m = calculateMatch(student, i);
            matches.add(m);
        }

        // Sort descending by match score
        Collections.sort(matches);
        return matches;
    }

    /**
     * Ranks a list of applicant students for a specific internship.
     */
    public List<InternshipMatch> rankApplicantsForInternship(Internship internship, List<Student> applicants) {
        List<InternshipMatch> matches = new ArrayList<>();
        if (internship == null || applicants == null) {
            return matches;
        }

        for (Student s : applicants) {
            InternshipMatch m = calculateMatch(s, internship);
            matches.add(m);
        }

        Collections.sort(matches);
        return matches;
    }

    public double calculateSkillScore(List<Skill> studentSkills, List<Skill> internshipSkills) {
        if (internshipSkills == null || internshipSkills.isEmpty()) {
            return 85.0; // Baseline when no skills explicitly specified
        }

        Map<Integer, Skill> studentSkillMap = new HashMap<>();
        if (studentSkills != null) {
            for (Skill s : studentSkills) {
                studentSkillMap.put(s.getId(), s);
            }
        }

        double maxPossiblePoints = 0.0;
        double earnedPoints = 0.0;

        for (Skill reqSkill : internshipSkills) {
            double skillBaseWeight = reqSkill.isMandatory() ? 2.0 : 1.0;
            maxPossiblePoints += (skillBaseWeight * 1.2); // 1.2 for Advanced proficiency ceiling

            if (studentSkillMap.containsKey(reqSkill.getId())) {
                Skill candidateSkill = studentSkillMap.get(reqSkill.getId());
                double proficiencyFactor = 1.0;
                String level = candidateSkill.getProficiencyLevel() != null ? candidateSkill.getProficiencyLevel() : "Intermediate";
                if ("Advanced".equalsIgnoreCase(level)) {
                    proficiencyFactor = 1.2;
                } else if ("Beginner".equalsIgnoreCase(level)) {
                    proficiencyFactor = 0.75;
                }
                earnedPoints += (skillBaseWeight * proficiencyFactor);
            }
        }

        if (maxPossiblePoints == 0.0) {
            return 100.0;
        }

        double ratio = (earnedPoints / maxPossiblePoints) * 100.0;
        return Math.max(0.0, Math.min(100.0, ratio));
    }

    public double calculateCgpaScore(BigDecimal cgpa) {
        if (cgpa == null) {
            return 60.0;
        }
        // Normalize 10.0 scale to 100%
        double score = cgpa.doubleValue() * 10.0;
        return Math.max(0.0, Math.min(100.0, score));
    }

    public double calculateProjectRelevance(String studentProjects, Internship internship) {
        if (studentProjects == null || studentProjects.trim().isEmpty()) {
            return 45.0;
        }

        String projectText = studentProjects.toLowerCase(Locale.ENGLISH);

        // Keywords to search against: title, description, and internship skill names
        Set<String> targetKeywords = new HashSet<>();
        if (internship != null) {
            if (internship.getTitle() != null) {
                for (String word : internship.getTitle().toLowerCase(Locale.ENGLISH).split("[^a-zA-Z0-9+#]")) {
                    if (word.length() > 2) targetKeywords.add(word);
                }
            }
            if (internship.getDescription() != null) {
                for (String word : internship.getDescription().toLowerCase(Locale.ENGLISH).split("[^a-zA-Z0-9+#]")) {
                    if (word.length() > 3) targetKeywords.add(word);
                }
            }
            if (internship.getSkills() != null) {
                for (Skill s : internship.getSkills()) {
                    targetKeywords.add(s.getSkillName().toLowerCase(Locale.ENGLISH));
                }
            }
        }

        // Count matched keywords
        int matches = 0;
        for (String keyword : targetKeywords) {
            if (projectText.contains(keyword)) {
                matches++;
            }
        }

        // Project score formula: base 50.0 + 10 points per matched keyword up to 100.0
        double score = 50.0 + (matches * 10.0);
        return Math.min(100.0, score);
    }

    public double calculateLocationScore(String studentLocation, String internshipLocation) {
        if (studentLocation == null || internshipLocation == null) {
            return 60.0;
        }

        String sLoc = studentLocation.trim().toLowerCase(Locale.ENGLISH);
        String iLoc = internshipLocation.trim().toLowerCase(Locale.ENGLISH);

        if (sLoc.equals(iLoc) || sLoc.contains("remote") || iLoc.contains("remote") || sLoc.contains("any")) {
            return 100.0;
        }

        // Same major region proximity bonus (e.g. Bangalore - Hyderabad - Pune - Mumbai)
        boolean sIsSouth = sLoc.contains("bangalore") || sLoc.contains("hyderabad") || sLoc.contains("chennai");
        boolean iIsSouth = iLoc.contains("bangalore") || iLoc.contains("hyderabad") || iLoc.contains("chennai");

        boolean sIsWest = sLoc.contains("pune") || sLoc.contains("mumbai");
        boolean iIsWest = iLoc.contains("pune") || iLoc.contains("mumbai");

        if ((sIsSouth && iIsSouth) || (sIsWest && iIsWest)) {
            return 75.0;
        }

        return 40.0;
    }

    private Map<String, Double> getFallbackWeights() {
        Map<String, Double> w = new HashMap<>();
        w.put("weight_skill", DEFAULT_WEIGHT_SKILL);
        w.put("weight_cgpa", DEFAULT_WEIGHT_CGPA);
        w.put("weight_project", DEFAULT_WEIGHT_PROJECT);
        w.put("weight_location", DEFAULT_WEIGHT_LOCATION);
        return w;
    }
}
