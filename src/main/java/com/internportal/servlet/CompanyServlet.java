package com.internportal.servlet;

import com.internportal.dao.SkillDAO;
import com.internportal.model.Application;
import com.internportal.model.Company;
import com.internportal.model.Internship;
import com.internportal.model.Skill;
import com.internportal.model.User;
import com.internportal.service.ApplicationService;
import com.internportal.service.CompanyService;
import com.internportal.service.InternshipService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles Corporate Recruiter Web UI endpoints:
 * Dashboard, Posting New Internships, and Managing Ranked Applicants.
 */
@WebServlet(name = "CompanyServlet", urlPatterns = {"/company/dashboard", "/company/internship/new", "/company/application/status"})
public class CompanyServlet extends HttpServlet {

    private CompanyService companyService;
    private InternshipService internshipService;
    private ApplicationService applicationService;
    private SkillDAO skillDAO;

    @Override
    public void init() throws ServletException {
        this.companyService = new CompanyService();
        this.internshipService = new InternshipService();
        this.applicationService = new ApplicationService();
        this.skillDAO = new SkillDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"COMPANY".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Optional<Company> companyOpt = companyService.getCompanyByUserId(user.getId());
        if (companyOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login?error=company_not_found");
            return;
        }

        Company company = companyOpt.get();
        req.setAttribute("company", company);

        List<Internship> internships = companyService.getInternshipsByCompany(company.getId());
        Map<Integer, List<Application>> applicantsMap = new HashMap<>();
        for (Internship i : internships) {
            applicantsMap.put(i.getId(), companyService.getRankedApplicantsForInternship(i.getId()));
        }

        List<Skill> allSkills = skillDAO.findAll();

        req.setAttribute("internships", internships);
        req.setAttribute("applicantsMap", applicantsMap);
        req.setAttribute("allSkills", allSkills);

        req.getRequestDispatcher("/WEB-INF/views/company-dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"COMPANY".equalsIgnoreCase(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Optional<Company> companyOpt = companyService.getCompanyByUserId(user.getId());
        if (companyOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Company company = companyOpt.get();

        if (path.equals("/company/internship/new")) {
            if (!company.isApproved()) {
                resp.sendRedirect(req.getContextPath() + "/company/dashboard?error=account_not_approved");
                return;
            }

            Internship internship = new Internship();
            internship.setCompanyId(company.getId());
            internship.setTitle(req.getParameter("title"));
            internship.setDescription(req.getParameter("description"));
            internship.setLocation(req.getParameter("location"));

            String stipendStr = req.getParameter("stipend");
            internship.setStipend(new BigDecimal(stipendStr != null && !stipendStr.trim().isEmpty() ? stipendStr : "0.00"));

            String durationStr = req.getParameter("durationMonths");
            internship.setDurationMonths(Integer.parseInt(durationStr != null && !durationStr.trim().isEmpty() ? durationStr : "6"));

            String minCgpaStr = req.getParameter("minCgpa");
            internship.setMinCgpa(new BigDecimal(minCgpaStr != null && !minCgpaStr.trim().isEmpty() ? minCgpaStr : "6.00"));

            String minSemStr = req.getParameter("minSemester");
            internship.setMinSemester(Integer.parseInt(minSemStr != null && !minSemStr.trim().isEmpty() ? minSemStr : "1"));

            String maxBacklogsStr = req.getParameter("maxBacklogs");
            internship.setMaxBacklogs(Integer.parseInt(maxBacklogsStr != null && !maxBacklogsStr.trim().isEmpty() ? maxBacklogsStr : "0"));

            String[] courses = req.getParameterValues("allowedCourses");
            internship.setAllowedCourses(courses != null ? String.join(",", courses) : "MCA,M.Sc. IT,M.Tech");

            String deadlineStr = req.getParameter("deadline");
            if (deadlineStr != null && !deadlineStr.trim().isEmpty()) {
                internship.setDeadline(Date.valueOf(deadlineStr));
            } else {
                internship.setDeadline(Date.valueOf(java.time.LocalDate.now().plusMonths(2)));
            }
            internship.setStatus("OPEN");

            // Handle required skills
            String[] skillIds = req.getParameterValues("skills");
            if (skillIds != null) {
                List<Skill> skillList = new ArrayList<>();
                for (String sId : skillIds) {
                    try {
                        int id = Integer.parseInt(sId);
                        boolean isMandatory = req.getParameter("mandatory_" + id) != null;
                        Skill sk = new Skill();
                        sk.setId(id);
                        sk.setMandatory(isMandatory);
                        skillList.add(sk);
                    } catch (NumberFormatException ignored) {}
                }
                internship.setSkills(skillList);
            }

            int id = internshipService.createInternship(internship);
            if (id > 0) {
                resp.sendRedirect(req.getContextPath() + "/company/dashboard?msg=internship_created");
            } else {
                resp.sendRedirect(req.getContextPath() + "/company/dashboard?error=create_failed");
            }
        } else if (path.equals("/company/application/status")) {
            try {
                int applicationId = Integer.parseInt(req.getParameter("applicationId"));
                String newStatus = req.getParameter("status");

                if (newStatus != null && (newStatus.equals("SHORTLISTED") || newStatus.equals("SELECTED") || newStatus.equals("REJECTED"))) {
                    applicationService.updateStatus(applicationId, newStatus);
                    resp.sendRedirect(req.getContextPath() + "/company/dashboard?msg=status_updated");
                    return;
                }
            } catch (Exception e) {
                // fall through
            }
            resp.sendRedirect(req.getContextPath() + "/company/dashboard?error=status_update_failed");
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

