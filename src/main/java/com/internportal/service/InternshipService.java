package com.internportal.service;

import com.internportal.dao.InternshipDAO;
import com.internportal.model.Internship;

import java.util.List;
import java.util.Optional;

/**
 * Service managing internship postings and lifecycle.
 */
public class InternshipService {

    private final InternshipDAO internshipDAO;

    public InternshipService() {
        this.internshipDAO = new InternshipDAO();
    }

    public InternshipService(InternshipDAO internshipDAO) {
        this.internshipDAO = internshipDAO;
    }

    public int createInternship(Internship internship) {
        return internshipDAO.createInternship(internship);
    }

    public boolean updateInternship(Internship internship) {
        return internshipDAO.updateInternship(internship);
    }

    public Optional<Internship> getInternshipById(int id) {
        return internshipDAO.findById(id);
    }

    public List<Internship> getAllInternships() {
        return internshipDAO.findAll();
    }

    public List<Internship> getOpenInternships() {
        return internshipDAO.findOpenInternships();
    }

    public List<Internship> getInternshipsByCompany(int companyId) {
        return internshipDAO.findByCompanyId(companyId);
    }

    public boolean updateStatus(int internshipId, String status) {
        return internshipDAO.updateStatus(internshipId, status);
    }

    public boolean deleteInternship(int id) {
        return internshipDAO.deleteInternship(id);
    }
}
