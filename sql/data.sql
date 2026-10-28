-- =====================================================================
-- Smart Internship Eligibility & Matching Portal
-- Seed Data for MySQL 8.0+
-- Default Password for all demo accounts: Password@123
-- BCrypt Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- =====================================================================

USE internship_portal;

-- 1. Configuration Settings
INSERT INTO config (config_key, config_value, description) VALUES
('weight_skill', '0.50', 'Weight factor for skill matching (0.0 to 1.0)'),
('weight_cgpa', '0.25', 'Weight factor for CGPA score (0.0 to 1.0)'),
('weight_project', '0.15', 'Weight factor for project relevance (0.0 to 1.0)'),
('weight_location', '0.10', 'Weight factor for location preference (0.0 to 1.0)'),
('portal_name', 'Smart Internship Matching Portal', 'Application Portal Name'),
('academic_year', '2026-2027', 'Current Academic Placement Cycle');

-- 2. Skills (15 Diverse Technical Skills)
INSERT INTO skills (id, skill_name, category) VALUES
(1, 'Java', 'Programming'),
(2, 'Python', 'Programming'),
(3, 'C++', 'Programming'),
(4, 'JavaScript', 'Web Development'),
(5, 'React', 'Web Development'),
(6, 'Node.js', 'Backend Development'),
(7, 'MySQL', 'Database'),
(8, 'MongoDB', 'Database'),
(9, 'Spring Boot', 'Backend Development'),
(10, 'Docker', 'DevOps & Cloud'),
(11, 'Kubernetes', 'DevOps & Cloud'),
(12, 'AWS', 'DevOps & Cloud'),
(13, 'Machine Learning', 'Data Science & AI'),
(14, 'HTML/CSS', 'Web Development'),
(15, 'RESTful APIs', 'Backend Development');

-- 3. Users (1 Admin, 3 Companies, 5 Students)
-- Password for all is Password@123
INSERT INTO users (id, email, password_hash, role) VALUES
(1, 'admin@portal.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
(2, 'techcorp@portal.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'COMPANY'),
(3, 'innovatech@portal.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'COMPANY'),
(4, 'nextgen@portal.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'COMPANY'),
(5, 'rahul@student.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STUDENT'),
(6, 'priya@student.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STUDENT'),
(7, 'amit@student.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STUDENT'),
(8, 'sneha@student.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STUDENT'),
(9, 'vikram@student.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STUDENT');

-- 4. Companies (3 Companies)
INSERT INTO companies (id, user_id, company_name, industry, website, location, description, is_approved) VALUES
(1, 2, 'TechCorp Solutions', 'Enterprise Software & Cloud', 'https://techcorp-example.com', 'Bangalore', 'Leading global provider of cloud-native enterprise platforms and financial technology services.', TRUE),
(2, 3, 'InnovaTech Labs', 'AI & Digital Products', 'https://innovatech-example.com', 'Pune', 'Specializing in generative AI, data engineering, and modern scalable web applications.', TRUE),
(3, 4, 'NextGen AI & Cloud', 'Cloud Systems & Automation', 'https://nextgen-example.com', 'Hyderabad', 'Innovative startup building edge cloud orchestrations and microservices automation systems.', FALSE);

-- 5. Students (5 Post-Graduate Students)
INSERT INTO students (id, user_id, full_name, roll_number, course, semester, cgpa, active_backlogs, preferred_location, projects, resume_url) VALUES
(1, 5, 'Rahul Sharma', 'MCA202401', 'MCA', 3, 8.85, 0, 'Bangalore', 'E-Commerce Microservices platform built with Spring Boot, React, MySQL and Docker. Cloud-native distributed caching system.', 'https://example.com/resumes/rahul_sharma.pdf'),
(2, 6, 'Priya Patel', 'MSC202405', 'M.Sc. IT', 3, 7.90, 0, 'Pune', 'Predictive Analytics for Customer Churn using Python, Scikit-Learn, and MySQL. Interactive dashboard in HTML/CSS/JavaScript with RESTful APIs.', 'https://example.com/resumes/priya_patel.pdf'),
(3, 7, 'Amit Verma', 'MTECH202409', 'M.Tech', 4, 9.20, 0, 'Hyderabad', 'High-Throughput Distributed Message Queue in Java with Kubernetes clustering and AWS deployment. Microservices REST API Gateway.', 'https://example.com/resumes/amit_verma.pdf'),
(4, 8, 'Sneha Kulkarni', 'MCA202412', 'MCA', 2, 6.80, 1, 'Mumbai', 'Real-time Chat Application using Node.js, Express, MongoDB and React. Task Manager Progressive Web Application in HTML/CSS.', 'https://example.com/resumes/sneha_kulkarni.pdf'),
(5, 9, 'Vikram Singh', 'MCA202420', 'MCA', 4, 5.80, 2, 'Delhi', 'Inventory Management Desktop App in Java Swing with MySQL database backend. Console-based C++ algorithms suite.', 'https://example.com/resumes/vikram_singh.pdf');

-- 6. Student Skills Mapping
-- Rahul (Java, Spring Boot, MySQL, React, Docker, RESTful APIs)
INSERT INTO student_skills (student_id, skill_id, proficiency_level) VALUES
(1, 1, 'Advanced'),      -- Java
(1, 9, 'Advanced'),      -- Spring Boot
(1, 7, 'Advanced'),      -- MySQL
(1, 5, 'Intermediate'),  -- React
(1, 10, 'Intermediate'), -- Docker
(1, 15, 'Advanced');     -- RESTful APIs

-- Priya (Python, Machine Learning, MySQL, HTML/CSS, RESTful APIs, JavaScript)
INSERT INTO student_skills (student_id, skill_id, proficiency_level) VALUES
(2, 2, 'Advanced'),      -- Python
(2, 13, 'Advanced'),     -- Machine Learning
(2, 7, 'Intermediate'),  -- MySQL
(2, 14, 'Advanced'),     -- HTML/CSS
(2, 4, 'Intermediate'),  -- JavaScript
(2, 15, 'Intermediate'); -- RESTful APIs

-- Amit (Java, Spring Boot, Kubernetes, AWS, Docker, RESTful APIs)
INSERT INTO student_skills (student_id, skill_id, proficiency_level) VALUES
(3, 1, 'Advanced'),      -- Java
(3, 9, 'Advanced'),      -- Spring Boot
(3, 11, 'Advanced'),     -- Kubernetes
(3, 12, 'Advanced'),     -- AWS
(3, 10, 'Advanced'),     -- Docker
(3, 15, 'Advanced');     -- RESTful APIs

-- Sneha (JavaScript, React, Node.js, MongoDB, HTML/CSS)
INSERT INTO student_skills (student_id, skill_id, proficiency_level) VALUES
(4, 4, 'Advanced'),      -- JavaScript
(4, 5, 'Intermediate'),  -- React
(4, 6, 'Intermediate'),  -- Node.js
(4, 8, 'Intermediate'),  -- MongoDB
(4, 14, 'Advanced');     -- HTML/CSS

-- Vikram (C++, Java, MySQL)
INSERT INTO student_skills (student_id, skill_id, proficiency_level) VALUES
(5, 3, 'Intermediate'),  -- C++
(5, 1, 'Beginner'),      -- Java
(5, 7, 'Beginner');      -- MySQL

-- 7. Internships (8 Diverse Opportunities)
INSERT INTO internships (id, company_id, title, description, location, stipend, duration_months, min_cgpa, min_semester, max_backlogs, allowed_courses, deadline, status) VALUES
(1, 1, 'Full Stack Java Developer Intern', 'Build high-performance RESTful microservices with Spring Boot and dynamic web frontends in React. Participate in agile sprints and CI/CD automation.', 'Bangalore', 35000.00, 6, 7.50, 3, 0, 'MCA,M.Tech', '2026-12-31', 'OPEN'),
(2, 1, 'Cloud & DevOps Platform Intern', 'Automate multi-cluster Kubernetes deployments, configure AWS resources via Terraform, and maintain container pipelines with Docker.', 'Hyderabad', 40000.00, 6, 8.00, 3, 0, 'MCA,M.Tech,M.Sc. IT', '2026-12-15', 'OPEN'),
(3, 2, 'Machine Learning & Data Science Intern', 'Research predictive algorithms, train deep neural models, and build intelligent analytical APIs using Python and modern data pipelines.', 'Pune', 30000.00, 6, 7.00, 2, 0, 'M.Sc. IT,M.Tech,MCA', '2026-11-30', 'OPEN'),
(4, 2, 'Frontend Web Developer Intern', 'Develop highly responsive modern user interfaces using React, JavaScript, and HTML/CSS. Collaborate with UX designers on component libraries.', 'Mumbai', 25000.00, 4, 6.50, 2, 1, 'MCA,M.Sc. IT', '2026-12-20', 'OPEN'),
(5, 2, 'Backend Node.js & API Intern', 'Design and scale event-driven backend services with Node.js, Express, and MongoDB. Write unit tests and API documentation.', 'Pune', 28000.00, 6, 6.50, 2, 0, 'MCA,M.Sc. IT', '2026-11-25', 'OPEN'),
(6, 3, 'Cloud Infrastructure Engineer Intern', 'Build edge computing services and manage cloud orchestrations using AWS, Docker, and Kubernetes.', 'Hyderabad', 45000.00, 6, 8.50, 3, 0, 'M.Tech,MCA', '2026-12-31', 'OPEN'),
(7, 1, 'Junior Software Engineering Trainee', 'Foundational software engineering role focusing on bug fixes, unit testing, and relational database queries with Java and MySQL.', 'Delhi', 20000.00, 3, 5.50, 2, 2, 'MCA,M.Sc. IT,M.Tech', '2026-12-10', 'OPEN'),
(8, 2, 'Enterprise Java & REST Services Intern', 'Develop secure enterprise integration points, batch processing jobs, and microservices endpoints using Java and Spring.', 'Bangalore', 32000.00, 6, 7.00, 3, 0, 'MCA,M.Tech', '2026-12-28', 'OPEN');

-- 8. Internship Skills Mapping (Mandatory and Preferred Skills)
-- Internship 1: Java (Mandatory), Spring Boot (Mandatory), MySQL (Preferred), React (Preferred)
INSERT INTO internship_skills (internship_id, skill_id, is_mandatory) VALUES
(1, 1, TRUE),   -- Java
(1, 9, TRUE),   -- Spring Boot
(1, 7, FALSE),  -- MySQL
(1, 5, FALSE);  -- React

-- Internship 2: Docker (Mandatory), AWS (Mandatory), Kubernetes (Mandatory), RESTful APIs (Preferred)
INSERT INTO internship_skills (internship_id, skill_id, is_mandatory) VALUES
(2, 10, TRUE),  -- Docker
(2, 12, TRUE),  -- AWS
(2, 11, TRUE),  -- Kubernetes
(2, 15, FALSE); -- RESTful APIs

-- Internship 3: Python (Mandatory), Machine Learning (Mandatory), MySQL (Preferred), RESTful APIs (Preferred)
INSERT INTO internship_skills (internship_id, skill_id, is_mandatory) VALUES
(3, 2, TRUE),   -- Python
(3, 13, TRUE),  -- Machine Learning
(3, 7, FALSE),  -- MySQL
(3, 15, FALSE); -- RESTful APIs

-- Internship 4: JavaScript (Mandatory), React (Mandatory), HTML/CSS (Preferred)
INSERT INTO internship_skills (internship_id, skill_id, is_mandatory) VALUES
(4, 4, TRUE),   -- JavaScript
(4, 5, TRUE),   -- React
(4, 14, FALSE); -- HTML/CSS

-- Internship 5: Node.js (Mandatory), MongoDB (Mandatory), RESTful APIs (Preferred)
INSERT INTO internship_skills (internship_id, skill_id, is_mandatory) VALUES
(5, 6, TRUE),   -- Node.js
(5, 8, TRUE),   -- MongoDB
(5, 15, FALSE); -- RESTful APIs

-- Internship 6: AWS (Mandatory), Kubernetes (Mandatory), Docker (Mandatory)
INSERT INTO internship_skills (internship_id, skill_id, is_mandatory) VALUES
(6, 12, TRUE),  -- AWS
(6, 11, TRUE),  -- Kubernetes
(6, 10, TRUE);  -- Docker

-- Internship 7: Java (Mandatory), MySQL (Preferred)
INSERT INTO internship_skills (internship_id, skill_id, is_mandatory) VALUES
(7, 1, TRUE),   -- Java
(7, 7, FALSE);  -- MySQL

-- Internship 8: Java (Mandatory), RESTful APIs (Mandatory), Spring Boot (Preferred)
INSERT INTO internship_skills (internship_id, skill_id, is_mandatory) VALUES
(8, 1, TRUE),   -- Java
(8, 15, TRUE),  -- RESTful APIs
(8, 9, FALSE);  -- Spring Boot

-- 9. Sample Applications (With realistic match scores computed by formula)
INSERT INTO applications (id, internship_id, student_id, match_score, status, applied_at) VALUES
(1, 1, 1, 93.50, 'APPLIED', '2026-10-15 10:30:00'),
(2, 3, 2, 91.25, 'SHORTLISTED', '2026-10-16 11:45:00'),
(3, 2, 3, 98.00, 'SELECTED', '2026-10-17 09:15:00'),
(4, 4, 4, 82.50, 'APPLIED', '2026-10-18 14:20:00'),
(5, 7, 5, 64.50, 'REJECTED', '2026-10-19 16:00:00');

-- 10. Sample Notifications
INSERT INTO notifications (id, user_id, title, message, is_read) VALUES
(1, 5, 'Application Submitted', 'Your application for Full Stack Java Developer Intern at TechCorp Solutions was submitted successfully.', TRUE),
(2, 6, 'Shortlisted for Interview', 'Congratulations! You have been shortlisted by InnovaTech Labs for the Machine Learning Intern position.', FALSE),
(3, 7, 'Selection Confirmation', 'Congratulations! You have been selected by TechCorp Solutions for the Cloud & DevOps Platform Intern role.', FALSE),
(4, 1, 'Company Registration Pending', 'Company "NextGen AI & Cloud" has registered and is awaiting administrator verification.', FALSE),
(5, 2, 'New Applicant Received', 'Rahul Sharma (MCA) has applied for Full Stack Java Developer Intern.', TRUE);

