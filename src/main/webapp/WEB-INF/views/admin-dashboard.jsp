<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Placement Cell Administration - Smart Internship Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">Placement Cell Administrative Portal</h1>
            <p class="page-subtitle">University Post-Graduate Internship Matching, Analytics, and Recruiter Verification</p>
        </div>
    </div>

    <c:if test="${param.msg == 'approved'}">
        <div class="alert alert-success">Recruiter account approved! The company has been granted internship posting rights.</div>
    </c:if>
    <c:if test="${param.msg == 'rejected'}">
        <div class="alert alert-warning">Recruiter verification status updated.</div>
    </c:if>
    <c:if test="${param.msg == 'weights_updated'}">
        <div class="alert alert-success">Matching algorithm weights updated successfully across all student computations!</div>
    </c:if>

    <!-- Placement KPI Metrics Grid -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Total PG Candidates</div>
            <div class="stat-value">${summary.totalStudents}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Corporate Recruiters</div>
            <div class="stat-value">${summary.totalCompanies}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Active Internships</div>
            <div class="stat-value" style="color: var(--success);">${summary.openInternships}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Total Applications</div>
            <div class="stat-value" style="color: var(--secondary);">${summary.totalApplications}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Avg Compatibility Score</div>
            <div class="stat-value" style="color: #8b5cf6;">${summary.averageMatchScore}%</div>
        </div>
    </div>

    <!-- Algorithm Weight Tuning Card -->
    <div class="card" style="margin-bottom: 2rem;">
        <div class="card-title">
            <span>⚙️ Intelligent Matching Algorithm Weights</span>
            <span style="font-size: 0.85rem; color: var(--muted); font-weight: normal;">Adjust criteria balance for ranking candidates (Total = 1.0)</span>
        </div>
        <form action="${pageContext.request.contextPath}/admin/config" method="post">
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Skill Competency Weight (0.0 - 1.0)</label>
                    <input type="number" step="0.05" min="0" max="1" name="weight_skill" class="form-control" value="${summary.weights.weight_skill}" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Academic CGPA Weight (0.0 - 1.0)</label>
                    <input type="number" step="0.05" min="0" max="1" name="weight_cgpa" class="form-control" value="${summary.weights.weight_cgpa}" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Project Relevance Weight (0.0 - 1.0)</label>
                    <input type="number" step="0.05" min="0" max="1" name="weight_project" class="form-control" value="${summary.weights.weight_project}" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Location Preference Weight (0.0 - 1.0)</label>
                    <input type="number" step="0.05" min="0" max="1" name="weight_location" class="form-control" value="${summary.weights.weight_location}" required>
                </div>
            </div>
            <div style="text-align: right; margin-top: 0.5rem;">
                <button type="submit" class="btn btn-primary btn-sm">Update Scoring Engine</button>
            </div>
        </form>
    </div>

    <!-- Pending Recruiter Verifications -->
    <div class="card" style="margin-bottom: 2rem;">
        <div class="card-title">
            <span>🛡️ Recruiter Verification Queue</span>
            <span class="badge ${not empty pendingCompanies ? 'badge-warning' : 'badge-secondary'}">
                ${not empty pendingCompanies ? pendingCompanies.size() : 0} Pending
            </span>
        </div>
        <c:choose>
            <c:when test="${not empty pendingCompanies}">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Company Name</th>
                                <th>Industry</th>
                                <th>Location</th>
                                <th>Official Website</th>
                                <th>Recruiter Email</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="comp" items="${pendingCompanies}">
                                <tr>
                                    <td><strong>${comp.companyName}</strong></td>
                                    <td>${comp.industry}</td>
                                    <td>📍 ${comp.location}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty comp.website}">
                                                <a href="${comp.website}" target="_blank" style="color: var(--primary);">${comp.website}</a>
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${comp.email}</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/company/approve" method="post" style="display: inline-flex; gap: 0.4rem;">
                                            <input type="hidden" name="companyId" value="${comp.id}">
                                            <button type="submit" name="action" value="approve" class="btn btn-success btn-sm">Approve ✅</button>
                                            <button type="submit" name="action" value="reject" class="btn btn-danger btn-sm">Reject ❌</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <p style="color: var(--muted); font-size: 0.95rem; padding: 1rem 0;">All corporate recruiter registrations have been verified. No pending approvals.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- All Active Internships -->
    <div class="card" style="margin-bottom: 2rem;">
        <div class="card-title">Active Internship Postings (${not empty allInternships ? allInternships.size() : 0})</div>
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Role Title</th>
                        <th>Company</th>
                        <th>Location</th>
                        <th>Stipend</th>
                        <th>Min CGPA</th>
                        <th>Deadline</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="i" items="${allInternships}">
                        <tr>
                            <td><strong>${i.title}</strong></td>
                            <td>${i.companyName}</td>
                            <td>📍 ${i.location}</td>
                            <td>₹<fmt:formatNumber value="${i.stipend}" type="number"/></td>
                            <td>${i.minCgpa}</td>
                            <td><fmt:formatDate value="${i.deadline}" pattern="dd MMM yyyy"/></td>
                            <td><span class="badge ${i.status == 'OPEN' ? 'badge-success' : 'badge-secondary'}">${i.status}</span></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- All Enrolled Students -->
    <div class="card">
        <div class="card-title">Enrolled Post-Graduate Candidates (${not empty allStudents ? allStudents.size() : 0})</div>
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Roll Number</th>
                        <th>Student Name</th>
                        <th>Degree Course</th>
                        <th>Semester</th>
                        <th>CGPA</th>
                        <th>Active Backlogs</th>
                        <th>Email</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${allStudents}">
                        <tr>
                            <td><strong>${s.rollNumber}</strong></td>
                            <td>${s.fullName}</td>
                            <td>${s.course}</td>
                            <td>Sem ${s.semester}</td>
                            <td><strong><fmt:formatNumber value="${s.cgpa}" minFractionDigits="2"/></strong></td>
                            <td>
                                <span class="badge ${s.activeBacklogs == 0 ? 'badge-success' : 'badge-danger'}">
                                    ${s.activeBacklogs}
                                </span>
                            </td>
                            <td>${s.email}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />

</body>
</html>

