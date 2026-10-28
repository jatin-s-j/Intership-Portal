package com.internportal.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Application {
    private int id;
    private int internshipId;
    private int studentId;
    private BigDecimal matchScore;
    private String status; // 'APPLIED', 'SHORTLISTED', 'SELECTED', 'REJECTED'
    private Timestamp appliedAt;

    // Joined student fields (for company view)
    private String studentFullName;
    private String studentRollNumber;
    private String studentCourse;
    private int studentSemester;
    private BigDecimal studentCgpa;
    private String studentEmail;
    private String studentResumeUrl;

    // Joined internship fields (for student view)
    private String internshipTitle;
    private String companyName;
    private String internshipLocation;
    private BigDecimal internshipStipend;

    public Application() {
    }

    public Application(int id, int internshipId, int studentId, BigDecimal matchScore,
                       String status, Timestamp appliedAt) {
        this.id = id;
        this.internshipId = internshipId;
        this.studentId = studentId;
        this.matchScore = matchScore;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(int internshipId) {
        this.internshipId = internshipId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public BigDecimal getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(BigDecimal matchScore) {
        this.matchScore = matchScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Timestamp appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getStudentFullName() {
        return studentFullName;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public String getStudentCourse() {
        return studentCourse;
    }

    public void setStudentCourse(String studentCourse) {
        this.studentCourse = studentCourse;
    }

    public int getStudentSemester() {
        return studentSemester;
    }

    public void setStudentSemester(int studentSemester) {
        this.studentSemester = studentSemester;
    }

    public BigDecimal getStudentCgpa() {
        return studentCgpa;
    }

    public void setStudentCgpa(BigDecimal studentCgpa) {
        this.studentCgpa = studentCgpa;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentResumeUrl() {
        return studentResumeUrl;
    }

    public void setStudentResumeUrl(String studentResumeUrl) {
        this.studentResumeUrl = studentResumeUrl;
    }

    public String getInternshipTitle() {
        return internshipTitle;
    }

    public void setInternshipTitle(String internshipTitle) {
        this.internshipTitle = internshipTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getInternshipLocation() {
        return internshipLocation;
    }

    public void setInternshipLocation(String internshipLocation) {
        this.internshipLocation = internshipLocation;
    }

    public BigDecimal getInternshipStipend() {
        return internshipStipend;
    }

    public void setInternshipStipend(BigDecimal internshipStipend) {
        this.internshipStipend = internshipStipend;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", internshipId=" + internshipId +
                ", studentId=" + studentId +
                ", matchScore=" + matchScore +
                ", status='" + status + '\'' +
                ", appliedAt=" + appliedAt +
                '}';
    }
}

