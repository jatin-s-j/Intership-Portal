package com.internportal.service;

import com.internportal.dao.ApplicationDAO;
import com.internportal.dao.CompanyDAO;
import com.internportal.dao.InternshipDAO;
import com.internportal.dao.NotificationDAO;
import com.internportal.model.Application;
import com.internportal.model.Company;
import com.internportal.model.Internship;
import com.internportal.model.Notification;

import java.util.List;
import java.util.Optional;

/**
 * Service managing corporate recruiter operations, approvals, and candidate tracking.
 */
public class CompanyService {

    private final CompanyDAO companyDAO;
    private final InternshipDAO internshipDAO;
    private final ApplicationDAO applicationDAO;
    private final NotificationDAO notificationDAO;

    public CompanyService() {
        this.companyDAO = new CompanyDAO();
        this.internshipDAO = new InternshipDAO();
        this.applicationDAO = new ApplicationDAO();
        this.notificationDAO = new NotificationDAO();
    }

    public CompanyService(CompanyDAO companyDAO, InternshipDAO internshipDAO,
                          ApplicationDAO applicationDAO, NotificationDAO notificationDAO) {
        this.companyDAO = companyDAO;
        this.internshipDAO = internshipDAO;
        this.applicationDAO = applicationDAO;
        this.notificationDAO = notificationDAO;
    }

    public Optional<Company> getCompanyById(int id) {
        return companyDAO.findById(id);
    }

    public Optional<Company> getCompanyByUserId(int userId) {
        return companyDAO.findByUserId(userId);
    }

    public boolean updateProfile(Company company) {
        return companyDAO.updateCompany(company);
    }

    public boolean approveCompany(int companyId, boolean approve) {
        boolean ok = companyDAO.setApprovalStatus(companyId, approve);
        if (ok && approve) {
            Optional<Company> comp = companyDAO.findById(companyId);
            comp.ifPresent(c -> notificationDAO.createNotification(new Notification(
                    c.getUserId(),
                    "Company Account Approved",
                    "Your company profile has been verified and approved by the Placement Cell. You can now post internships."
            )));
        }
        return ok;
    }

    public List<Company> getPendingCompanies() {
        return companyDAO.findPending();
    }

    public List<Company> getAllCompanies() {
        return companyDAO.findAll();
    }

    public List<Internship> getInternshipsByCompany(int companyId) {
        return internshipDAO.findByCompanyId(companyId);
    }

    public List<Application> getRankedApplicantsForInternship(int internshipId) {
        return applicationDAO.findByInternshipIdRanked(internshipId);
    }
}
