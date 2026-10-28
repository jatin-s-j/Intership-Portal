package com.internportal.servlet;

import com.internportal.dao.ConfigDAO;
import com.internportal.model.Company;
import com.internportal.model.Internship;
import com.internportal.model.Student;
import com.internportal.model.User;
import com.internportal.service.CompanyService;
import com.internportal.service.InternshipService;
import com.internportal.service.ReportService;
import com.internportal.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Handles Placement Cell Administrator Web UI endpoints:
 * Institutional Analytics, Recruiter Verification, and Dynamic Weight Tuning.
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/dashboard", "/admin/company/approve", "/admin/config"})
public class AdminServlet extends HttpServlet {

    private ReportService reportService;
    private CompanyService companyService;
    private InternshipService internshipService;
    private StudentService studentService;
    private ConfigDAO configDAO;

    @Override
    public void init() throws ServletException {
        this.reportService = new ReportService();
        this.companyService = new CompanyService();
        this.internshipService = new InternshipService();
        this.studentService = new StudentService();
        this.configDAO = new ConfigDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, Object> summary = reportService.getSummaryReport();
        List<Company> pendingCompanies = companyService.getPendingCompanies();
        List<Company> allCompanies = companyService.getAllCompanies();
        List<Internship> allInternships = internshipService.getAllInternships();
        List<Student> allStudents = studentService.getAllStudents();

        req.setAttribute("summary", summary);
        req.setAttribute("pendingCompanies", pendingCompanies);
        req.setAttribute("allCompanies", allCompanies);
        req.setAttribute("allInternships", allInternships);
        req.setAttribute("allStudents", allStudents);

        req.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (path.equals("/admin/company/approve")) {
            try {
                int companyId = Integer.parseInt(req.getParameter("companyId"));
                String action = req.getParameter("action");
                boolean approve = "approve".equalsIgnoreCase(action);

                companyService.approveCompany(companyId, approve);
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?msg=" + (approve ? "approved" : "rejected"));
                return;
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?error=approval_failed");
                return;
            }
        } else if (path.equals("/admin/config")) {
            try {
                double wSkill = Double.parseDouble(req.getParameter("weight_skill"));
                double wCgpa = Double.parseDouble(req.getParameter("weight_cgpa"));
                double wProject = Double.parseDouble(req.getParameter("weight_project"));
                double wLocation = Double.parseDouble(req.getParameter("weight_location"));

                configDAO.updateMatchingWeights(wSkill, wCgpa, wProject, wLocation);
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?msg=weights_updated");
                return;
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?error=weights_update_failed");
                return;
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

