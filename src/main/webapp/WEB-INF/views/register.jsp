<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Smart Internship Eligibility & Matching Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="container">
    <div class="auth-wrapper" style="max-width: 640px;">
        <div class="auth-card">
            <div class="auth-header">
                <h1 class="auth-title">Create an Account</h1>
                <p class="page-subtitle">Join as a Post-Graduate Student or Corporate Recruiter</p>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="post">
                <div class="form-group">
                    <label class="form-label">I am registering as:</label>
                    <div style="display: flex; gap: 1.5rem; margin-top: 0.4rem;">
                        <label style="cursor: pointer; display: flex; align-items: center; gap: 0.4rem; font-weight: 600;">
                            <input type="radio" name="role" value="STUDENT" checked onchange="handleRoleChange('STUDENT')"> Student Candidate
                        </label>
                        <label style="cursor: pointer; display: flex; align-items: center; gap: 0.4rem; font-weight: 600;">
                            <input type="radio" name="role" value="COMPANY" onchange="handleRoleChange('COMPANY')"> Corporate Recruiter
                        </label>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="email" class="form-label">Email Address *</label>
                        <input type="email" id="email" name="email" class="form-control" placeholder="user@domain.com" required>
                    </div>
                    <div class="form-group">
                        <label for="password" class="form-label">Password *</label>
                        <input type="password" id="password" name="password" class="form-control" placeholder="Minimum 6 characters" required>
                    </div>
                </div>

                <!-- Student Specific Section -->
                <div id="studentFields">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="fullName" class="form-label">Full Name *</label>
                            <input type="text" id="fullName" name="fullName" class="form-control" placeholder="e.g. Rahul Sharma">
                        </div>
                        <div class="form-group">
                            <label for="rollNumber" class="form-label">Roll Number *</label>
                            <input type="text" id="rollNumber" name="rollNumber" class="form-control" placeholder="e.g. MCA202401">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="course" class="form-label">Course *</label>
                            <select id="course" name="course" class="form-control">
                                <option value="MCA">MCA</option>
                                <option value="M.Sc. IT">M.Sc. IT</option>
                                <option value="M.Tech">M.Tech</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="semester" class="form-label">Current Semester *</label>
                            <input type="number" id="semester" name="semester" min="1" max="6" value="3" class="form-control">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="cgpa" class="form-label">CGPA (0.0 to 10.0) *</label>
                            <input type="number" id="cgpa" name="cgpa" step="0.01" min="0.0" max="10.0" value="8.0" class="form-control">
                        </div>
                        <div class="form-group">
                            <label for="activeBacklogs" class="form-label">Active Backlogs *</label>
                            <input type="number" id="activeBacklogs" name="activeBacklogs" min="0" max="10" value="0" class="form-control">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="preferredLocation" class="form-label">Preferred Location</label>
                            <input type="text" id="preferredLocation" name="preferredLocation" class="form-control" placeholder="e.g. Bangalore, Pune, Remote">
                        </div>
                        <div class="form-group">
                            <label for="resumeUrl" class="form-label">Resume Link (URL)</label>
                            <input type="url" id="resumeUrl" name="resumeUrl" class="form-control" placeholder="https://drive.google.com/...">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="projects" class="form-label">Projects & Tech Stack Description</label>
                        <textarea id="projects" name="projects" class="form-control" rows="3" placeholder="Describe key academic or personal software projects, technologies used (Java, Spring, React, ML, etc.)."></textarea>
                    </div>
                </div>

                <!-- Company Specific Section -->
                <div id="companyFields" style="display: none;">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="companyName" class="form-label">Company Name *</label>
                            <input type="text" id="companyName" name="companyName" class="form-control" placeholder="e.g. TechCorp Solutions">
                        </div>
                        <div class="form-group">
                            <label for="industry" class="form-label">Industry</label>
                            <input type="text" id="industry" name="industry" class="form-control" placeholder="e.g. Cloud Computing, FinTech">
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="location" class="form-label">Headquarters / Office Location</label>
                            <input type="text" id="location" name="location" class="form-control" placeholder="e.g. Bangalore">
                        </div>
                        <div class="form-group">
                            <label for="website" class="form-label">Website</label>
                            <input type="url" id="website" name="website" class="form-control" placeholder="https://example.com">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="description" class="form-label">About Company</label>
                        <textarea id="description" name="description" class="form-control" rows="3" placeholder="Brief description of company domains and engineering environment."></textarea>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 1rem;">Complete Registration</button>
            </form>

            <p style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem; color: var(--muted);">
                Already have an account? <a href="${pageContext.request.contextPath}/login" style="color: var(--primary); font-weight: 600;">Sign In</a>
            </p>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />

</body>
</html>

