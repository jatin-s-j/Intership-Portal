package com.internportal.model;

import java.util.ArrayList;
import java.util.List;

public class InternshipMatch implements Comparable<InternshipMatch> {
    private Internship internship;
    private Student student;
    private double matchScore;
    private double skillMatchScore;
    private double cgpaScore;
    private double projectRelevanceScore;
    private double locationScore;
    private boolean eligible;
    private List<String> reasons = new ArrayList<>();
    private String applicationStatus; // If already applied

    public InternshipMatch() {
    }

    public InternshipMatch(Internship internship, double matchScore, boolean eligible) {
        this.internship = internship;
        this.matchScore = matchScore;
        this.eligible = eligible;
    }

    public Internship getInternship() {
        return internship;
    }

    public void setInternship(Internship internship) {
        this.internship = internship;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = Math.round(matchScore * 100.0) / 100.0;
    }

    public double getSkillMatchScore() {
        return skillMatchScore;
    }

    public void setSkillMatchScore(double skillMatchScore) {
        this.skillMatchScore = Math.round(skillMatchScore * 100.0) / 100.0;
    }

    public double getCgpaScore() {
        return cgpaScore;
    }

    public void setCgpaScore(double cgpaScore) {
        this.cgpaScore = Math.round(cgpaScore * 100.0) / 100.0;
    }

    public double getProjectRelevanceScore() {
        return projectRelevanceScore;
    }

    public void setProjectRelevanceScore(double projectRelevanceScore) {
        this.projectRelevanceScore = Math.round(projectRelevanceScore * 100.0) / 100.0;
    }

    public double getLocationScore() {
        return locationScore;
    }

    public void setLocationScore(double locationScore) {
        this.locationScore = Math.round(locationScore * 100.0) / 100.0;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    @Override
    public int compareTo(InternshipMatch other) {
        // Sort descending by match score
        return Double.compare(other.matchScore, this.matchScore);
    }

    @Override
    public String toString() {
        return "InternshipMatch{" +
                "internship=" + (internship != null ? internship.getTitle() : "null") +
                ", student=" + (student != null ? student.getFullName() : "null") +
                ", matchScore=" + matchScore +
                ", eligible=" + eligible +
                '}';
    }
}

