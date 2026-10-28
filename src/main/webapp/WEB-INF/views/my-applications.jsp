<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Applications - Smart Internship Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">My Applications Tracker</h1>
            <p class="page-subtitle">Track the status of all your submitted internship applications and match scores</p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-secondary btn-sm">&larr; Back to Listings</a>
        </div>
    </div>

    <c:if test="${param.msg == 'applied_success'}">
        <div class="alert alert-success">Application submitted successfully! It has been recorded below.</div>
    </c:if>

    <div class="card">
        <c:choose>
            <c:when test="${not empty applications}">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Internship Role</th>
                                <th>Company</th>
                                <th>Location</th>
                                <th>Monthly Stipend</th>
                                <th>Match Score</th>
                                <th>Status</th>
                                <th>Date Applied</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="app" items="${applications}">
                                <tr>
                                    <td><strong>${app.internshipTitle}</strong></td>
                                    <td>${app.companyName}</td>
                                    <td>📍 ${app.internshipLocation}</td>
                                    <td>₹<fmt:formatNumber value="${app.internshipStipend}" type="number"/></td>
                                    <td>
                                        <div class="match-score-badge ${app.matchScore >= 80 ? 'match-high' : (app.matchScore >= 60 ? 'match-medium' : 'match-low')}" style="height: 28px; font-size: 0.85rem;">
                                            <fmt:formatNumber value="${app.matchScore}" maxFractionDigits="1"/>%
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.status == 'SELECTED'}">
                                                <span class="badge badge-success">Selected 🎉</span>
                                            </c:when>
                                            <c:when test="${app.status == 'SHORTLISTED'}">
                                                <span class="badge badge-primary">Shortlisted ⭐</span>
                                            </c:when>
                                            <c:when test="${app.status == 'REJECTED'}">
                                                <span class="badge badge-danger">Not Selected</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-warning">Under Review</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><fmt:formatDate value="${app.appliedAt}" pattern="dd MMM yyyy, HH:mm"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div style="text-align: center; padding: 2.5rem;">
                    <p style="color: var(--muted); margin-bottom: 1rem;">You have not applied to any internships yet.</p>
                    <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-primary btn-sm">Explore Matching Internships</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="footer.jsp" />

</body>
</html>

