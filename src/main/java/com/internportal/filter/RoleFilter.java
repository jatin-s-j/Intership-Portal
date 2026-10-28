package com.internportal.filter;

import com.internportal.model.User;
import com.internportal.util.JsonUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter to enforce Role-Based Access Control (RBAC) across Admin, Company, and Student portals.
 */
@WebFilter("/*")
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user != null) {
            String role = user.getRole();

            // Admin only paths
            if (path.startsWith("/admin/") && !"ADMIN".equalsIgnoreCase(role)) {
                denyAccess(req, resp, path);
                return;
            }

            // Company only paths
            if (path.startsWith("/company/") && !"COMPANY".equalsIgnoreCase(role)) {
                denyAccess(req, resp, path);
                return;
            }

            // Student only paths
            if (path.startsWith("/student/") && !"STUDENT".equalsIgnoreCase(role)) {
                denyAccess(req, resp, path);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void denyAccess(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        if (path.startsWith("/api/")) {
            JsonUtil.sendErrorResponse(resp, HttpServletResponse.SC_FORBIDDEN, "Access forbidden: insufficient role permissions.");
        } else {
            resp.sendRedirect(req.getContextPath() + "/dashboard?error=access_denied");
        }
    }

    @Override
    public void destroy() {}
}
