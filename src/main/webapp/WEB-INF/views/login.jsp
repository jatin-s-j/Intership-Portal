<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Smart Internship Eligibility & Matching Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="container">
    <div class="auth-wrapper">
        <div class="auth-card">
            <div class="auth-header">
                <h1 class="auth-title">Welcome Back</h1>
                <p class="page-subtitle">Log in to your student, recruiter, or admin account</p>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>
            <c:if test="${param.msg == 'reg_success'}">
                <div class="alert alert-success">Registration successful! Please sign in with your credentials.</div>
            </c:if>
            <c:if test="${param.msg == 'logged_out'}">
                <div class="alert alert-warning">You have been logged out securely.</div>
            </c:if>
            <c:if test="${param.error == 'auth_required'}">
                <div class="alert alert-danger">Please log in to access this portal page.</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label for="email" class="form-label">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" placeholder="name@domain.com" value="${enteredEmail}" required autofocus>
                </div>

                <div class="form-group">
                    <label for="password" class="form-label">Password</label>
                    <input type="password" id="password" name="password" class="form-control" placeholder="••••••••" required>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%;">Sign In</button>
            </form>

            <div class="demo-credentials-box">
                <strong>Quick Demo Logins (Click to autofill):</strong>
                <div class="demo-btn-group">
                    <button type="button" class="demo-btn" onclick="fillCredentials('rahul@student.edu', 'Password@123')">🎓 Student (Rahul)</button>
                    <button type="button" class="demo-btn" onclick="fillCredentials('techcorp@portal.com', 'Password@123')">🏢 Recruiter (TechCorp)</button>
                    <button type="button" class="demo-btn" onclick="fillCredentials('admin@portal.edu', 'Password@123')">🛡️ Admin</button>
                </div>
            </div>

            <p style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem; color: var(--muted);">
                Don't have an account? <a href="${pageContext.request.contextPath}/register" style="color: var(--primary); font-weight: 600;">Register here</a>
            </p>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />

</body>
</html>

