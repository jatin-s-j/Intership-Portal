package com.internportal.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Internship {
    private int id;
    private int companyId;
    private String title;
    private String description;
    private String location;
    private BigDecimal stipend;
    private int durationMonths;
    private BigDecimal minCgpa;
    private int minSemester;
    private int maxBacklogs;
    private String allowedCourses; // Comma-separated: 'MCA,M.Sc. IT,M.Tech'
    private Date deadline;
    private String status; // 'OPEN', 'CLOSED'
    private Timestamp createdAt;

    // Joined fields
    private String companyName;
    private String companyLocation;

    // Associated required skills
    private List<Skill> skills = new ArrayList<>();

    public Internship() {
    }

    public Internship(int id, int companyId, String title, String description, String location,
                      BigDecimal stipend, int durationMonths, BigDecimal minCgpa, int minSemester,
                      int maxBacklogs, String allowedCourses, Date deadline, String status,
                      Timestamp createdAt) {
        this.id = id;
        this.companyId = companyId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.stipend = stipend;
        this.durationMonths = durationMonths;
        this.minCgpa = minCgpa;
        this.minSemester = minSemester;
        this.maxBacklogs = maxBacklogs;
        this.allowedCourses = allowedCourses;
        this.deadline = deadline;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getStipend() {
        return stipend;
    }

    public void setStipend(BigDecimal stipend) {
        this.stipend = stipend;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(BigDecimal minCgpa) {
        this.minCgpa = minCgpa;
    }

    public int getMinSemester() {
        return minSemester;
    }

    public void setMinSemester(int minSemester) {
        this.minSemester = minSemester;
    }

    public int getMaxBacklogs() {
        return maxBacklogs;
    }

    public void setMaxBacklogs(int maxBacklogs) {
        this.maxBacklogs = maxBacklogs;
    }

    public String getAllowedCourses() {
        return allowedCourses;
    }

    public void setAllowedCourses(String allowedCourses) {
        this.allowedCourses = allowedCourses;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLocation() {
        return companyLocation;
    }

    public void setCompanyLocation(String companyLocation) {
        this.companyLocation = companyLocation;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    @Override
    public String toString() {
        return "Internship{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", companyName='" + companyName + '\'' +
                ", location='" + location + '\'' +
                ", minCgpa=" + minCgpa +
                ", deadline=" + deadline +
                ", status='" + status + '\'' +
                '}';
    }
}

