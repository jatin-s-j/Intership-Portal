package com.internportal.servlet;

import com.internportal.dao.SkillDAO;
import com.internportal.model.Application;
import com.internportal.model.InternshipMatch;
import com.internportal.model.Skill;
import com.internportal.model.Student;
import com.internportal.model.User;
import com.internportal.service.ApplicationService;
import com.internportal.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles Student Web UI endpoints: Dashboard, Applications Tracker, and Profile Updates.
 */
@WebServlet(name = "StudentServlet", urlPatterns = {"/student/dashboard", "/student/applications", "/student/profile", "/student/apply"})
public class StudentServlet extends HttpServlet {

    private StudentService studentService;
    private ApplicationService applicationService;
    private SkillDAO skillDAO;

    @Override
    public void init() throws ServletException {
        this.studentService = new StudentService();
        this.applicationService = new ApplicationService();
        this.skillDAO = new SkillDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Optional<Student> studentOpt = studentService.getStudentByUserId(user.getId());
        if (studentOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login?error=student_not_found");
            return;
        }

        Student student = studentOpt.get();
        req.setAttribute("student", student);

        if (path.equals("/student/dashboard")) {
            List<InternshipMatch> rankedMatches = studentService.getRankedInternshipsForStudent(student.getId());
            List<Skill> allSkills = skillDAO.findAll();

            req.setAttribute("rankedMatches", rankedMatches);
            req.setAttribute("allSkills", allSkills);
            req.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp").forward(req, resp);
        } else if (path.equals("/student/applications")) {
            List<Application> applications = applicationService.getApplicationsForStudent(student.getId());
            req.setAttribute("applications", applications);
            req.getRequestDispatcher("/WEB-INF/views/my-applications.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/student/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Optional<Student> studentOpt = studentService.getStudentByUserId(user.getId());
        if (studentOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Student student = studentOpt.get();

        if (path.equals("/student/profile")) {
            student.setFullName(req.getParameter("fullName"));
            student.setCourse(req.getParameter("course"));
            student.setSemester(Integer.parseInt(req.getParameter("semester") != null ? req.getParameter("semester") : "1"));
            student.setCgpa(new BigDecimal(req.getParameter("cgpa") != null ? req.getParameter("cgpa") : "0.0"));
            student.setActiveBacklogs(Integer.parseInt(req.getParameter("activeBacklogs") != null ? req.getParameter("activeBacklogs") : "0"));
            student.setPreferredLocation(req.getParameter("preferredLocation"));
            student.setProjects(req.getParameter("projects"));
            student.setResumeUrl(req.getParameter("resumeUrl"));

            studentService.updateProfile(student);

            // Handle skill updates
            String[] skillIds = req.getParameterValues("skills");
            if (skillIds != null) {
                List<Skill> updatedSkills = new ArrayList<>();
                for (String sId : skillIds) {
                    int id = Integer.parseInt(sId);
                    String prof = req.getParameter("prof_" + id);
                    Skill sk = new Skill();
                    sk.setId(id);
                    sk.setProficiencyLevel(prof != null ? prof : "Intermediate");
                    updatedSkills.add(sk);
                }
                studentService.syncSkills(student.getId(), updatedSkills);
            }

            resp.sendRedirect(req.getContextPath() + "/student/dashboard?msg=profile_updated");
        } else if (path.equals("/student/apply")) {
            String internshipIdParam = req.getParameter("internshipId");
            if (internshipIdParam != null && !internshipIdParam.trim().isEmpty()) {
                try {
                    int internshipId = Integer.parseInt(internshipIdParam.trim());
                    ApplicationService.ApplicationResult result = applicationService.apply(student.getId(), internshipId);
                    if (result.isSuccess()) {
                        resp.sendRedirect(req.getContextPath() + "/student/applications?msg=applied_success");
                    } else {
                        String encodedError = java.net.URLEncoder.encode(result.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
                        resp.sendRedirect(req.getContextPath() + "/student/dashboard?error=" + encodedError);
                    }
                    return;
                } catch (NumberFormatException e) {
                    resp.sendRedirect(req.getContextPath() + "/student/dashboard?error=invalid_internship_id");
                    return;
                }
            }
            resp.sendRedirect(req.getContextPath() + "/student/dashboard");
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
