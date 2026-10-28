package com.internportal.servlet;

import com.internportal.model.Company;
import com.internportal.model.Student;
import com.internportal.model.User;
import com.internportal.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Handles Web UI authentication, registration, and session management.
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/auth/*"})
public class AuthServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (path.equals("/login")) {
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        } else if (path.equals("/register")) {
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        } else if (path.equals("/auth/logout")) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login?msg=logged_out");
        } else {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (path.equals("/auth/login") || path.equals("/login")) {
            handleLogin(req, resp);
        } else if (path.equals("/auth/register") || path.equals("/register")) {
            handleRegister(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Email and password are required.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        try {
            Optional<User> userOpt = authService.authenticate(email, password);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("userRole", user.getRole());
                session.setAttribute("userEmail", user.getEmail());

                resp.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                req.setAttribute("errorMessage", "Invalid email or password.");
                req.setAttribute("enteredEmail", email);
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Database error: Cannot connect to MySQL on port 3306. Please ensure MySQL server is running and internship_portal database is imported.");
            req.setAttribute("enteredEmail", email);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = req.getParameter("role");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (role == null || email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            req.setAttribute("errorMessage", "All core fields are required.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        try {
            boolean success = false;
            if ("STUDENT".equalsIgnoreCase(role)) {
                Student student = new Student();
                student.setFullName(req.getParameter("fullName"));
                student.setRollNumber(req.getParameter("rollNumber"));
                student.setCourse(req.getParameter("course"));
                student.setSemester(Integer.parseInt(req.getParameter("semester") != null ? req.getParameter("semester") : "1"));
                student.setCgpa(new BigDecimal(req.getParameter("cgpa") != null ? req.getParameter("cgpa") : "0.0"));
                student.setActiveBacklogs(Integer.parseInt(req.getParameter("activeBacklogs") != null ? req.getParameter("activeBacklogs") : "0"));
                student.setPreferredLocation(req.getParameter("preferredLocation"));
                student.setProjects(req.getParameter("projects"));
                student.setResumeUrl(req.getParameter("resumeUrl"));

                success = authService.registerStudent(email, password, student);
            } else if ("COMPANY".equalsIgnoreCase(role)) {
                Company company = new Company();
                company.setCompanyName(req.getParameter("companyName"));
                company.setIndustry(req.getParameter("industry"));
                company.setWebsite(req.getParameter("website"));
                company.setLocation(req.getParameter("location"));
                company.setDescription(req.getParameter("description"));

                success = authService.registerCompany(email, password, company);
            }

            if (success) {
                resp.sendRedirect(req.getContextPath() + "/login?msg=reg_success");
            } else {
                req.setAttribute("errorMessage", "Registration failed. Email or Roll Number may already exist.");
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage() + ". Please verify MySQL server is running.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }
}
