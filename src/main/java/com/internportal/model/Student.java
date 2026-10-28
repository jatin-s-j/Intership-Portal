package com.internportal.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private int id;
    private int userId;
    private String fullName;
    private String rollNumber;
    private String course; // 'MCA', 'M.Sc. IT', 'M.Tech'
    private int semester;
    private BigDecimal cgpa;
    private int activeBacklogs;
    private String preferredLocation;
    private String projects;
    private String resumeUrl;
    private Timestamp createdAt;

    // Supplementary User info
    private String email;

    // Associated skills
    private List<Skill> skills = new ArrayList<>();

    public Student() {
    }

    public Student(int id, int userId, String fullName, String rollNumber, String course,
                   int semester, BigDecimal cgpa, int activeBacklogs, String preferredLocation,
                   String projects, String resumeUrl, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.rollNumber = rollNumber;
        this.course = course;
        this.semester = semester;
        this.cgpa = cgpa;
        this.activeBacklogs = activeBacklogs;
        this.preferredLocation = preferredLocation;
        this.projects = projects;
        this.resumeUrl = resumeUrl;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public BigDecimal getCgpa() {
        return cgpa;
    }

    public void setCgpa(BigDecimal cgpa) {
        this.cgpa = cgpa;
    }

    public int getActiveBacklogs() {
        return activeBacklogs;
    }

    public void setActiveBacklogs(int activeBacklogs) {
        this.activeBacklogs = activeBacklogs;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public String getProjects() {
        return projects;
    }

    public void setProjects(String projects) {
        this.projects = projects;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", rollNumber='" + rollNumber + '\'' +
                ", course='" + course + '\'' +
                ", semester=" + semester +
                ", cgpa=" + cgpa +
                ", activeBacklogs=" + activeBacklogs +
                ", preferredLocation='" + preferredLocation + '\'' +
                '}';
    }
}

