<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard - Smart Internship Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">Welcome, ${student.fullName}</h1>
            <p class="page-subtitle">${student.course} | Semester ${student.semester} | Roll No: ${student.rollNumber}</p>
        </div>
        <div>
            <button class="btn btn-secondary" onclick="openModal('profileModal')">✏️ Edit Profile & Skills</button>
        </div>
    </div>

    <c:if test="${param.msg == 'profile_updated'}">
        <div class="alert alert-success">Profile and skill competencies updated successfully!</div>
    </c:if>
    <c:if test="${param.msg == 'applied_success'}">
        <div class="alert alert-success">Application submitted successfully! Your compatibility match has been logged.</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger">${param.error}</div>
    </c:if>

    <!-- Student Stats / Profile Summary Card -->
    <div class="card" style="margin-bottom: 2rem;">
        <div class="card-title">Academic & Professional Profile</div>
        <div class="stats-grid" style="margin-bottom: 1rem;">
            <div class="stat-card">
                <div class="stat-label">Current CGPA</div>
                <div class="stat-value"><fmt:formatNumber value="${student.cgpa}" minFractionDigits="2" maxFractionDigits="2"/></div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Active Backlogs</div>
                <div class="stat-value" style="color: ${student.activeBacklogs == 0 ? 'var(--success)' : 'var(--danger)'};">
                    ${student.activeBacklogs}
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Preferred City</div>
                <div class="stat-value" style="font-size: 1.5rem; color: var(--dark);">${not empty student.preferredLocation ? student.preferredLocation : 'Any'}</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Resume Link</div>
                <div class="stat-value" style="font-size: 1.1rem; margin-top: 0.5rem;">
                    <c:choose>
                        <c:when test="${not empty student.resumeUrl}">
                            <a href="${student.resumeUrl}" target="_blank" style="color: var(--primary); text-decoration: underline;">View Resume ↗</a>
                        </c:when>
                        <c:otherwise><span style="color: var(--muted); font-size: 0.95rem;">Not uploaded</span></c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div>
            <strong style="font-size: 0.9rem; color: var(--muted);">Registered Skill Competencies:</strong>
            <div style="margin-top: 0.5rem;">
                <c:choose>
                    <c:when test="${not empty student.skills}">
                        <c:forEach var="sk" items="${student.skills}">
                            <span class="tag" style="background-color: var(--primary-light); color: var(--primary); border-color: #c7d2fe;">
                                <strong>${sk.skillName}</strong> (${sk.proficiencyLevel})
                            </span>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <span style="color: var(--muted); font-size: 0.9rem;">No skills registered yet. Click 'Edit Profile & Skills' above to add skills.</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Ranked Internship Opportunities -->
    <div style="margin-bottom: 1rem; display: flex; justify-content: space-between; align-items: flex-end;">
        <div>
            <h2 style="font-size: 1.4rem; font-weight: 700;">Ranked Internship Opportunities</h2>
            <p style="color: var(--muted); font-size: 0.9rem;">Ranked by multi-factor algorithmic compatibility (Skills, CGPA, Projects, Location)</p>
        </div>
    </div>

    <c:choose>
        <c:when test="${not empty rankedMatches}">
            <div style="display: flex; flex-direction: column; gap: 1.25rem;">
                <c:forEach var="match" items="${rankedMatches}">
                    <div class="card" style="border-left: 5px solid ${match.eligible ? (match.matchScore >= 80.0 ? 'var(--success)' : 'var(--warning)') : 'var(--danger)'};">
                        <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem;">
                            <div>
                                <h3 style="font-size: 1.25rem; font-weight: 700; color: var(--dark);">
                                    ${match.internship.title}
                                </h3>
                                <p style="color: var(--muted); font-size: 0.95rem; margin-top: 0.2rem;">
                                    🏢 <strong>${match.internship.companyName}</strong> &bull; 📍 ${match.internship.location} &bull; 💰 ₹<fmt:formatNumber value="${match.internship.stipend}" type="number"/>/mo &bull; ⏳ ${match.internship.durationMonths} Mos
                                </p>
                            </div>
                            <div style="text-align: right; display: flex; align-items: center; gap: 0.75rem;">
                                <div>
                                    <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--muted); font-weight: 700;">Compatibility</div>
                                    <div class="match-score-badge ${match.matchScore >= 80.0 ? 'match-high' : (match.matchScore >= 60.0 ? 'match-medium' : 'match-low')}">
                                        <fmt:formatNumber value="${match.matchScore}" maxFractionDigits="1"/>%
                                    </div>
                                </div>
                                <div>
                                    <c:choose>
                                        <c:when test="${match.eligible}">
                                            <span class="badge badge-success">Eligible</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-danger">Not Eligible</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>

                        <p style="margin: 1rem 0; font-size: 0.95rem; color: #334155;">
                            ${match.internship.description}
                        </p>

                        <!-- Required Skills Tags -->
                        <div style="margin-bottom: 1rem;">
                            <span style="font-size: 0.85rem; color: var(--muted); font-weight: 600;">Required Skills:</span>
                            <c:forEach var="rSkill" items="${match.internship.skills}">
                                <span class="tag ${rSkill.mandatory ? 'tag-mandatory' : ''}">
                                    ${rSkill.skillName} ${rSkill.mandatory ? '(Mandatory)' : ''}
                                </span>
                            </c:forEach>
                        </div>

                        <!-- Compatibility Breakdown Accordion/Grid -->
                        <div style="background-color: #f8fafc; border: 1px solid var(--border); border-radius: var(--radius); padding: 1rem; margin-bottom: 1.25rem;">
                            <div style="font-weight: 600; font-size: 0.85rem; color: var(--muted); text-transform: uppercase; margin-bottom: 0.6rem;">Match Score Breakdown</div>
                            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 0.75rem;">
                                <div class="breakdown-row">
                                    <span>Skills:</span>
                                    <div class="progress-bar-bg"><div class="progress-bar-fill" style="width: ${match.skillMatchScore}%;"></div></div>
                                    <strong><fmt:formatNumber value="${match.skillMatchScore}" maxFractionDigits="0"/>%</strong>
                                </div>
                                <div class="breakdown-row">
                                    <span>CGPA:</span>
                                    <div class="progress-bar-bg"><div class="progress-bar-fill" style="width: ${match.cgpaScore}%;"></div></div>
                                    <strong><fmt:formatNumber value="${match.cgpaScore}" maxFractionDigits="0"/>%</strong>
                                </div>
                                <div class="breakdown-row">
                                    <span>Projects:</span>
                                    <div class="progress-bar-bg"><div class="progress-bar-fill" style="width: ${match.projectRelevanceScore}%;"></div></div>
                                    <strong><fmt:formatNumber value="${match.projectRelevanceScore}" maxFractionDigits="0"/>%</strong>
                                </div>
                                <div class="breakdown-row">
                                    <span>Location:</span>
                                    <div class="progress-bar-bg"><div class="progress-bar-fill" style="width: ${match.locationScore}%;"></div></div>
                                    <strong><fmt:formatNumber value="${match.locationScore}" maxFractionDigits="0"/>%</strong>
                                </div>
                            </div>
                        </div>

                        <!-- Disqualification details if ineligible -->
                        <c:if test="${not match.eligible and not empty match.reasons}">
                            <div style="background-color: var(--danger-light); border: 1px solid #fca5a5; border-radius: var(--radius); padding: 0.85rem 1rem; margin-bottom: 1.25rem; font-size: 0.85rem; color: #991b1b;">
                                <strong>Disqualification Criteria Not Met:</strong>
                                <ul style="margin-left: 1.25rem; margin-top: 0.35rem;">
                                    <c:forEach var="reason" items="${match.reasons}">
                                        <li>${reason}</li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>

                        <!-- Action Button / Application Status -->
                        <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--border); padding-top: 1rem;">
                            <div style="font-size: 0.85rem; color: var(--muted);">
                                📅 Deadline: <strong><fmt:formatDate value="${match.internship.deadline}" pattern="dd MMM yyyy"/></strong> &bull; Allowed: ${match.internship.allowedCourses} &bull; Min CGPA: ${match.internship.minCgpa}
                            </div>
                            <div>
                                <c:choose>
                                    <c:when test="${not empty match.applicationStatus}">
                                        <span class="badge ${match.applicationStatus == 'SELECTED' ? 'badge-success' : (match.applicationStatus == 'SHORTLISTED' ? 'badge-primary' : (match.applicationStatus == 'REJECTED' ? 'badge-danger' : 'badge-warning'))}" style="padding: 0.5rem 1rem; font-size: 0.85rem;">
                                            Status: ${match.applicationStatus}
                                        </span>
                                    </c:when>
                                    <c:when test="${match.eligible}">
                                        <form action="${pageContext.request.contextPath}/student/apply" method="post" style="display: inline;">
                                            <input type="hidden" name="internshipId" value="${match.internship.id}">
                                            <button type="submit" class="btn btn-primary btn-sm">Apply Now 🚀</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-secondary btn-sm" disabled title="You do not meet all corporate and academic criteria for this role.">Ineligible to Apply</button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card" style="text-align: center; padding: 3rem;">
                <p style="color: var(--muted); font-size: 1.1rem;">No open internship listings available at this moment.</p>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Edit Profile Modal -->
<div id="profileModal" class="modal-backdrop">
    <div class="modal-content">
        <div class="modal-header">
            <h3 style="font-size: 1.25rem; font-weight: 700;">Update Profile & Competencies</h3>
            <button class="modal-close" onclick="closeModal('profileModal')">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/student/profile" method="post">
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Full Name</label>
                    <input type="text" name="fullName" class="form-control" value="${student.fullName}" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Course</label>
                    <select name="course" class="form-control">
                        <option value="MCA" ${student.course == 'MCA' ? 'selected' : ''}>MCA</option>
                        <option value="M.Sc. IT" ${student.course == 'M.Sc. IT' ? 'selected' : ''}>M.Sc. IT</option>
                        <option value="M.Tech" ${student.course == 'M.Tech' ? 'selected' : ''}>M.Tech</option>
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Semester</label>
                    <input type="number" name="semester" min="1" max="6" value="${student.semester}" class="form-control" required>
                </div>
                <div class="form-group">
                    <label class="form-label">CGPA</label>
                    <input type="number" name="cgpa" step="0.01" min="0" max="10" value="${student.cgpa}" class="form-control" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Active Backlogs</label>
                    <input type="number" name="activeBacklogs" min="0" max="10" value="${student.activeBacklogs}" class="form-control" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Preferred Location</label>
                    <input type="text" name="preferredLocation" value="${student.preferredLocation}" class="form-control">
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Resume URL</label>
                <input type="url" name="resumeUrl" value="${student.resumeUrl}" class="form-control">
            </div>

            <div class="form-group">
                <label class="form-label">Software Projects & Technologies</label>
                <textarea name="projects" class="form-control" rows="3">${student.projects}</textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Select Skills & Proficiency Levels</label>
                <div style="max-height: 180px; overflow-y: auto; border: 1px solid var(--border); border-radius: var(--radius); padding: 0.75rem;">
                    <c:forEach var="sk" items="${allSkills}">
                        <c:set var="isOwned" value="false"/>
                        <c:set var="ownedProf" value="Intermediate"/>
                        <c:forEach var="sOwned" items="${student.skills}">
                            <c:if test="${sOwned.id == sk.id}">
                                <c:set var="isOwned" value="true"/>
                                <c:set var="ownedProf" value="${sOwned.proficiencyLevel}"/>
                            </c:if>
                        </c:forEach>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                            <label style="cursor: pointer; display: flex; align-items: center; gap: 0.4rem; font-size: 0.9rem;">
                                <input type="checkbox" name="skills" value="${sk.id}" ${isOwned ? 'checked' : ''}>
                                ${sk.skillName} (${sk.category})
                            </label>
                            <select name="prof_${sk.id}" class="form-control" style="width: auto; padding: 0.2rem 0.5rem; font-size: 0.8rem;">
                                <option value="Beginner" ${ownedProf == 'Beginner' ? 'selected' : ''}>Beginner</option>
                                <option value="Intermediate" ${ownedProf == 'Intermediate' ? 'selected' : ''}>Intermediate</option>
                                <option value="Advanced" ${ownedProf == 'Advanced' ? 'selected' : ''}>Advanced</option>
                            </select>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                <button type="button" class="btn btn-secondary" onclick="closeModal('profileModal')">Cancel</button>
                <button type="submit" class="btn btn-primary">Save Changes</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="footer.jsp" />

</body>
</html>

