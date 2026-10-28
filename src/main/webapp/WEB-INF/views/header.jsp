<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="navbar">
    <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 10v6M2 10l10-5 10 5-10 5z"/>
            <path d="M6 12v5c3 3 9 3 12 0v-5"/>
        </svg>
        SmartIntern<span>Portal</span>
    </a>
    <nav>
        <ul class="navbar-nav">
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <c:if test="${sessionScope.user.role == 'STUDENT'}">
                        <li><a href="${pageContext.request.contextPath}/student/dashboard" class="nav-link">Recommended Internships</a></li>
                        <li><a href="${pageContext.request.contextPath}/student/applications" class="nav-link">My Applications</a></li>
                    </c:if>
                    <c:if test="${sessionScope.user.role == 'COMPANY'}">
                        <li><a href="${pageContext.request.contextPath}/company/dashboard" class="nav-link">Recruiter Dashboard</a></li>
                    </c:if>
                    <c:if test="${sessionScope.user.role == 'ADMIN'}">
                        <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link">Placement Analytics</a></li>
                    </c:if>
                    <li>
                        <span class="user-badge">${sessionScope.user.role}: ${sessionScope.user.email}</span>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-secondary btn-sm">Logout</a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li><a href="${pageContext.request.contextPath}/login" class="nav-link">Login</a></li>
                    <li><a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Register</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </nav>
</header>

