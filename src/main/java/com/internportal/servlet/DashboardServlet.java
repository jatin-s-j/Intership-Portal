package com.internportal.servlet;

import com.internportal.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Routes the authenticated user to their role-specific dashboard.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = user.getRole();
        if ("ADMIN".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else if ("COMPANY".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/company/dashboard");
        } else if ("STUDENT".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/student/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
