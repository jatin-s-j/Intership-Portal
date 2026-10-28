package com.internportal.service;

import com.internportal.dao.ApplicationDAO;
import com.internportal.dao.CompanyDAO;
import com.internportal.dao.ConfigDAO;
import com.internportal.dao.InternshipDAO;
import com.internportal.dao.StudentDAO;
import com.internportal.dao.UserDAO;

import java.util.HashMap;
import java.util.Map;

/**
 * Service providing statistical reporting and placement analytics for Admin dashboard.
 */
public class ReportService {

    private final StudentDAO studentDAO;
    private final CompanyDAO companyDAO;
    private final InternshipDAO internshipDAO;
    private final ApplicationDAO applicationDAO;
    private final UserDAO userDAO;
    private final ConfigDAO configDAO;

    public ReportService() {
        this.studentDAO = new StudentDAO();
        this.companyDAO = new CompanyDAO();
        this.internshipDAO = new InternshipDAO();
        this.applicationDAO = new ApplicationDAO();
        this.userDAO = new UserDAO();
        this.configDAO = new ConfigDAO();
    }

    public Map<String, Object> getSummaryReport() {
        Map<String, Object> report = new HashMap<>();

        report.put("totalUsers", userDAO.getTotalCount());
        report.put("totalStudents", studentDAO.getTotalCount());
        report.put("totalCompanies", companyDAO.getTotalCount());
        report.put("pendingCompanies", companyDAO.getPendingCount());
        report.put("totalInternships", internshipDAO.getTotalCount());
        report.put("openInternships", internshipDAO.getOpenCount());
        report.put("totalApplications", applicationDAO.getTotalCount());
        report.put("averageMatchScore", Math.round(applicationDAO.getAverageMatchScore() * 100.0) / 100.0);
        report.put("statusCounts", applicationDAO.getStatusCounts());
        report.put("weights", configDAO.getMatchingWeights());

        return report;
    }
}
