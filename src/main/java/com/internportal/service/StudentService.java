package com.internportal.service;

import com.internportal.dao.ApplicationDAO;
import com.internportal.dao.InternshipDAO;
import com.internportal.dao.StudentDAO;
import com.internportal.model.Application;
import com.internportal.model.Internship;
import com.internportal.model.InternshipMatch;
import com.internportal.model.Skill;
import com.internportal.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service managing student profiles, skills, and ranked eligibility recommendations.
 */
public class StudentService {

    private final StudentDAO studentDAO;
    private final InternshipDAO internshipDAO;
    private final ApplicationDAO applicationDAO;
    private final MatchingService matchingService;

    public StudentService() {
        this.studentDAO = new StudentDAO();
        this.internshipDAO = new InternshipDAO();
        this.applicationDAO = new ApplicationDAO();
        this.matchingService = new MatchingService();
    }

    public StudentService(StudentDAO studentDAO, InternshipDAO internshipDAO,
                          ApplicationDAO applicationDAO, MatchingService matchingService) {
        this.studentDAO = studentDAO;
        this.internshipDAO = internshipDAO;
        this.applicationDAO = applicationDAO;
        this.matchingService = matchingService;
    }

    public Optional<Student> getStudentById(int id) {
        return studentDAO.findById(id);
    }

    public Optional<Student> getStudentByUserId(int userId) {
        return studentDAO.findByUserId(userId);
    }

    public boolean updateProfile(Student student) {
        return studentDAO.updateStudent(student);
    }

    public boolean syncSkills(int studentId, List<Skill> skills) {
        return studentDAO.syncStudentSkills(studentId, skills);
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    /**
     * Retrieves all open internships ranked for this student, with eligibility details and applied status.
     */
    public List<InternshipMatch> getRankedInternshipsForStudent(int studentId) {
        Optional<Student> studentOpt = studentDAO.findById(studentId);
        if (studentOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Student student = studentOpt.get();
        List<Internship> openInternships = internshipDAO.findOpenInternships();

        // Check already applied internships
        List<Application> appliedList = applicationDAO.findByStudentId(studentId);
        Map<Integer, String> appliedMap = new HashMap<>();
        for (Application app : appliedList) {
            appliedMap.put(app.getInternshipId(), app.getStatus());
        }

        List<InternshipMatch> ranked = matchingService.rankInternshipsForStudent(student, openInternships);
        for (InternshipMatch match : ranked) {
            if (match.getInternship() != null && appliedMap.containsKey(match.getInternship().getId())) {
                match.setApplicationStatus(appliedMap.get(match.getInternship().getId()));
            }
        }

        return ranked;
    }
}
