package com.internportal.service;

import com.internportal.dao.ApplicationDAO;
import com.internportal.dao.InternshipDAO;
import com.internportal.dao.NotificationDAO;
import com.internportal.dao.StudentDAO;
import com.internportal.model.Application;
import com.internportal.model.EligibilityResult;
import com.internportal.model.Internship;
import com.internportal.model.InternshipMatch;
import com.internportal.model.Notification;
import com.internportal.model.Student;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service managing applications, status updates, and notifications.
 */
public class ApplicationService {

    private final ApplicationDAO applicationDAO;
    private final StudentDAO studentDAO;
    private final InternshipDAO internshipDAO;
    private final NotificationDAO notificationDAO;
    private final EligibilityEngine eligibilityEngine;
    private final MatchingService matchingService;

    public ApplicationService() {
        this.applicationDAO = new ApplicationDAO();
        this.studentDAO = new StudentDAO();
        this.internshipDAO = new InternshipDAO();
        this.notificationDAO = new NotificationDAO();
        this.eligibilityEngine = new EligibilityEngine(applicationDAO);
        this.matchingService = new MatchingService();
    }

    public ApplicationService(ApplicationDAO applicationDAO, StudentDAO studentDAO,
                              InternshipDAO internshipDAO, NotificationDAO notificationDAO,
                              EligibilityEngine eligibilityEngine, MatchingService matchingService) {
        this.applicationDAO = applicationDAO;
        this.studentDAO = studentDAO;
        this.internshipDAO = internshipDAO;
        this.notificationDAO = notificationDAO;
        this.eligibilityEngine = eligibilityEngine;
        this.matchingService = matchingService;
    }

    /**
     * Submits an application after validating eligibility and computing match score.
     */
    public ApplicationResult apply(int studentId, int internshipId) {
        Optional<Student> studentOpt = studentDAO.findById(studentId);
        Optional<Internship> internshipOpt = internshipDAO.findById(internshipId);

        if (studentOpt.isEmpty() || internshipOpt.isEmpty()) {
            return new ApplicationResult(false, "Invalid student or internship ID.", null);
        }

        Student student = studentOpt.get();
        Internship internship = internshipOpt.get();

        // 1. Eligibility Check
        EligibilityResult eligibility = eligibilityEngine.checkEligibility(student, internship);
        if (!eligibility.isEligible()) {
            return new ApplicationResult(false, "Student is not eligible: " + String.join("; ", eligibility.getReasons()), null);
        }

        // 2. Compute Match Score
        InternshipMatch match = matchingService.calculateMatch(student, internship);
        BigDecimal score = BigDecimal.valueOf(match.getMatchScore());

        // 3. Create Application
        Application app = new Application();
        app.setStudentId(studentId);
        app.setInternshipId(internshipId);
        app.setMatchScore(score);
        app.setStatus("APPLIED");

        int appId = applicationDAO.createApplication(app);
        if (appId <= 0) {
            return new ApplicationResult(false, "Failed to submit application. Please try again.", null);
        }

        app.setId(appId);

        // 4. Send Notifications
        notificationDAO.createNotification(new Notification(
                student.getUserId(),
                "Application Submitted",
                "Your application for " + internship.getTitle() + " has been submitted with a Match Score of " + score + "%."
        ));

        return new ApplicationResult(true, "Application submitted successfully! Match Score: " + score + "%", app);
    }

    public boolean updateStatus(int applicationId, String newStatus) {
        boolean ok = applicationDAO.updateStatus(applicationId, newStatus);
        if (ok) {
            Optional<Application> appOpt = applicationDAO.findById(applicationId);
            if (appOpt.isPresent()) {
                Application app = appOpt.get();
                Optional<Student> studentOpt = studentDAO.findById(app.getStudentId());
                if (studentOpt.isPresent()) {
                    String title = "Application Status Update";
                    String msg = "Your application for " + app.getInternshipTitle() + " has been updated to: " + newStatus + ".";
                    notificationDAO.createNotification(new Notification(studentOpt.get().getUserId(), title, msg));
                }
            }
        }
        return ok;
    }

    public List<Application> getApplicationsForStudent(int studentId) {
        return applicationDAO.findByStudentId(studentId);
    }

    public List<Application> getRankedApplicants(int internshipId) {
        return applicationDAO.findByInternshipIdRanked(internshipId);
    }

    public Optional<Application> getApplicationById(int id) {
        return applicationDAO.findById(id);
    }

    public static class ApplicationResult {
        private final boolean success;
        private final String message;
        private final Application application;

        public ApplicationResult(boolean success, String message, Application application) {
            this.success = success;
            this.message = message;
            this.application = application;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Application getApplication() {
            return application;
        }
    }
}
