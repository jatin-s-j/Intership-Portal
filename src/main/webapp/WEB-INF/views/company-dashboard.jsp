<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recruiter Dashboard - Smart Internship Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">${company.companyName}</h1>
            <p class="page-subtitle">
                🏢 ${company.industry} &bull; 📍 ${company.location}
                <c:choose>
                    <c:when test="${company.approved}">
                        <span class="badge badge-success" style="margin-left: 0.5rem;">Verified Recruiter</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-warning" style="margin-left: 0.5rem;">Pending Admin Approval</span>
                    </c:otherwise>
                </c:choose>
            </p>
        </div>
        <div>
            <c:choose>
                <c:when test="${company.approved}">
                    <button class="btn btn-primary" onclick="openModal('postInternshipModal')">+ Post New Internship</button>
                </c:when>
                <c:otherwise>
                    <button class="btn btn-secondary" disabled title="Account pending approval by Placement Cell">Approval Pending</button>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <c:if test="${param.msg == 'internship_created'}">
        <div class="alert alert-success">Internship opportunity posted successfully! Students can now be matched.</div>
    </c:if>
    <c:if test="${param.msg == 'status_updated'}">
        <div class="alert alert-success">Candidate application status updated and notification sent!</div>
    </c:if>
    <c:if test="${not company.approved}">
        <div class="alert alert-warning">
            Your corporate recruiter account is currently awaiting administrative approval from the University Placement Cell. Once approved, you can post internship roles and review applicants.
        </div>
    </c:if>

    <!-- Company Internship Postings & Ranked Candidates -->
    <h2 style="font-size: 1.35rem; font-weight: 700; margin-bottom: 1rem;">Posted Internships & Ranked Candidates</h2>

    <c:choose>
        <c:when test="${not empty internships}">
            <c:forEach var="internship" items="${internships}">
                <div class="card" style="margin-bottom: 2rem;">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem; border-bottom: 1px solid var(--border); padding-bottom: 1rem; margin-bottom: 1.25rem;">
                        <div>
                            <h3 style="font-size: 1.25rem; font-weight: 700;">${internship.title}</h3>
                            <p style="color: var(--muted); font-size: 0.9rem; margin-top: 0.2rem;">
                                📍 ${internship.location} &bull; 💰 ₹<fmt:formatNumber value="${internship.stipend}" type="number"/>/month &bull; ⏳ ${internship.durationMonths} Mos &bull; Status: <span class="badge ${internship.status == 'OPEN' ? 'badge-success' : 'badge-secondary'}">${internship.status}</span>
                            </p>
                        </div>
                        <div style="font-size: 0.85rem; color: var(--muted);">
                            Min CGPA: <strong>${internship.minCgpa}</strong> &bull; Max Backlogs: <strong>${internship.maxBacklogs}</strong> &bull; Allowed: ${internship.allowedCourses}
                        </div>
                    </div>

                    <!-- Applicants Table -->
                    <c:set var="applicants" value="${applicantsMap[internship.id]}"/>
                    <div style="margin-bottom: 0.5rem; display: flex; justify-content: space-between; align-items: center;">
                        <h4 style="font-size: 1.05rem; font-weight: 600; color: var(--dark);">
                            Ranked Applicants (${not empty applicants ? applicants.size() : 0})
                        </h4>
                        <span style="font-size: 0.8rem; color: var(--muted);">Ranked by Compatibility Match Score</span>
                    </div>

                    <c:choose>
                        <c:when test="${not empty applicants}">
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Rank / Score</th>
                                            <th>Student Candidate</th>
                                            <th>Course & Sem</th>
                                            <th>CGPA</th>
                                            <th>Resume</th>
                                            <th>Status</th>
                                            <th>Update Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="app" items="${applicants}" varStatus="status">
                                            <tr>
                                                <td>
                                                    <div style="display: flex; align-items: center; gap: 0.5rem;">
                                                        <span style="font-weight: 700; color: var(--muted);">#${status.count}</span>
                                                        <div class="match-score-badge ${app.matchScore >= 80 ? 'match-high' : (app.matchScore >= 60 ? 'match-medium' : 'match-low')}" style="height: 28px; font-size: 0.85rem;">
                                                            <fmt:formatNumber value="${app.matchScore}" maxFractionDigits="1"/>%
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <strong>${app.studentFullName}</strong><br>
                                                    <span style="font-size: 0.8rem; color: var(--muted);">${app.studentEmail}</span>
                                                </td>
                                                <td>${app.studentCourse} (Sem ${app.studentSemester})</td>
                                                <td><strong><fmt:formatNumber value="${app.studentCgpa}" minFractionDigits="2"/></strong></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty app.studentResumeUrl}">
                                                            <a href="${app.studentResumeUrl}" target="_blank" class="btn btn-secondary btn-sm" style="font-size: 0.8rem; padding: 0.25rem 0.5rem;">Resume ↗</a>
                                                        </c:when>
                                                        <c:otherwise><span style="color: var(--muted); font-size: 0.8rem;">None</span></c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <span class="badge ${app.status == 'SELECTED' ? 'badge-success' : (app.status == 'SHORTLISTED' ? 'badge-primary' : (app.status == 'REJECTED' ? 'badge-danger' : 'badge-warning'))}">
                                                        ${app.status}
                                                    </span>
                                                </td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/company/application/status" method="post" style="display: inline-flex; gap: 0.35rem;">
                                                        <input type="hidden" name="applicationId" value="${app.id}">
                                                        <button type="submit" name="status" value="SHORTLISTED" class="btn btn-primary btn-sm" style="font-size: 0.75rem; padding: 0.25rem 0.5rem;" ${app.status == 'SHORTLISTED' ? 'disabled' : ''}>Shortlist</button>
                                                        <button type="submit" name="status" value="SELECTED" class="btn btn-success btn-sm" style="font-size: 0.75rem; padding: 0.25rem 0.5rem;" ${app.status == 'SELECTED' ? 'disabled' : ''}>Select</button>
                                                        <button type="submit" name="status" value="REJECTED" class="btn btn-danger btn-sm" style="font-size: 0.75rem; padding: 0.25rem 0.5rem;" ${app.status == 'REJECTED' ? 'disabled' : ''}>Reject</button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p style="color: var(--muted); font-size: 0.9rem; padding: 1rem 0;">No applications received for this position yet.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div class="card" style="text-align: center; padding: 3rem;">
                <p style="color: var(--muted); font-size: 1.1rem; margin-bottom: 1rem;">You have not posted any internship positions yet.</p>
                <c:if test="${company.approved}">
                    <button class="btn btn-primary" onclick="openModal('postInternshipModal')">+ Post Your First Internship</button>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Post Internship Modal -->
<div id="postInternshipModal" class="modal-backdrop">
    <div class="modal-content" style="max-width: 680px;">
        <div class="modal-header">
            <h3 style="font-size: 1.25rem; font-weight: 700;">Post New Internship Opportunity</h3>
            <button class="modal-close" onclick="closeModal('postInternshipModal')">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/company/internship/new" method="post">
            <div class="form-group">
                <label class="form-label">Internship Title *</label>
                <input type="text" name="title" class="form-control" placeholder="e.g. Cloud & DevOps Engineer Intern" required>
            </div>

            <div class="form-group">
                <label class="form-label">Job Description & Responsibilities *</label>
                <textarea name="description" class="form-control" rows="3" placeholder="Overview of tasks, technologies, and projects candidate will handle." required></textarea>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Work Location *</label>
                    <input type="text" name="location" class="form-control" placeholder="e.g. Bangalore, Pune, Remote" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Monthly Stipend (₹) *</label>
                    <input type="number" name="stipend" step="1000" min="0" value="30000" class="form-control" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Duration (Months)</label>
                    <input type="number" name="durationMonths" min="1" max="12" value="6" class="form-control" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Application Deadline *</label>
                    <input type="date" name="deadline" class="form-control" required>
                </div>
            </div>

            <div style="margin: 1.25rem 0 0.5rem; font-weight: 700; color: var(--dark); font-size: 0.95rem;">Eligibility Criteria Filtering</div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Minimum CGPA (0.0 - 10.0)</label>
                    <input type="number" name="minCgpa" step="0.1" min="0" max="10" value="7.0" class="form-control" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Minimum Semester</label>
                    <input type="number" name="minSemester" min="1" max="6" value="2" class="form-control" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Max Allowed Backlogs</label>
                    <input type="number" name="maxBacklogs" min="0" max="5" value="0" class="form-control" required>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Allowed Degree Courses</label>
                <div style="display: flex; gap: 1.5rem;">
                    <label style="cursor: pointer;"><input type="checkbox" name="allowedCourses" value="MCA" checked> MCA</label>
                    <label style="cursor: pointer;"><input type="checkbox" name="allowedCourses" value="M.Sc. IT" checked> M.Sc. IT</label>
                    <label style="cursor: pointer;"><input type="checkbox" name="allowedCourses" value="M.Tech" checked> M.Tech</label>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Required Skills (Check to add, mark Mandatory if required for eligibility)</label>
                <div style="max-height: 180px; overflow-y: auto; border: 1px solid var(--border); border-radius: var(--radius); padding: 0.75rem;">
                    <c:forEach var="sk" items="${allSkills}">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                            <label style="cursor: pointer; font-size: 0.9rem;">
                                <input type="checkbox" name="skills" value="${sk.id}">
                                ${sk.skillName} (${sk.category})
                            </label>
                            <label style="cursor: pointer; font-size: 0.8rem; color: var(--danger); font-weight: 600;">
                                <input type="checkbox" name="mandatory_${sk.id}" value="true"> Mandatory
                            </label>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                <button type="button" class="btn btn-secondary" onclick="closeModal('postInternshipModal')">Cancel</button>
                <button type="submit" class="btn btn-primary">Publish Internship</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="footer.jsp" />

</body>
</html>

