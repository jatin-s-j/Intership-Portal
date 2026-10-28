package com.internportal.model;

import java.util.ArrayList;
import java.util.List;

public class EligibilityResult {
    private boolean eligible;
    private List<String> reasons = new ArrayList<>();
    private int internshipId;
    private int studentId;

    public EligibilityResult() {
        this.eligible = true;
    }

    public EligibilityResult(boolean eligible, List<String> reasons) {
        this.eligible = eligible;
        this.reasons = reasons != null ? reasons : new ArrayList<>();
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

    public void addReason(String reason) {
        if (this.reasons == null) {
            this.reasons = new ArrayList<>();
        }
        this.reasons.add(reason);
        this.eligible = false; // Any disqualifying reason marks candidate ineligible
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

    @Override
    public String toString() {
        return "EligibilityResult{" +
                "eligible=" + eligible +
                ", reasons=" + reasons +
                ", internshipId=" + internshipId +
                ", studentId=" + studentId +
                '}';
    }
}

