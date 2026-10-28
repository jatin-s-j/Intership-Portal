# Smart Internship Eligibility & Matching Portal

An enterprise-grade web portal designed for Post-Graduate Academic Institutions (MCA, M.Sc. IT, M.Tech) to automate internship eligibility verification and intelligent student-opportunity matching using a multi-attribute scoring engine.

---

## 🌟 Key Features

- **🎓 Student Portal**:
  - View personalized, ranked internship opportunities sorted by algorithmic compatibility.
  - Multi-criteria eligibility verification (CGPA, active backlogs, allowed degree course, semester, and mandatory skill checks).
  - Detailed compatibility score breakdown (Skill match, CGPA score, Project relevance, and Location preference).
  - Complete application lifecycle tracker with status badges (`APPLIED`, `SHORTLISTED`, `SELECTED`, `REJECTED`).
  - Interactive profile and skill competencies editor with proficiency tagging (*Beginner*, *Intermediate*, *Advanced*).

- **🏢 Corporate Recruiter Portal**:
  - Post internship opportunities with custom qualification filters (min CGPA, semester, max backlogs, allowed courses, deadline, and mandatory/preferred technical skills).
  - View real-time ranked applicant leaderboards for every posted position based on compatibility match scores.
  - One-click applicant status updates (*Shortlist*, *Select*, *Reject*) with automated student notifications.

- **🛡️ Placement Cell Administration**:
  - Institutional analytics dashboard with KPI metrics (total candidates, recruiters, open roles, total applications, and average match scores).
  - Recruiter verification queue to approve or reject new company registrations.
  - Dynamic algorithm weight tuning interface (tune Skill, CGPA, Project, and Location weights on the fly).
  - Comprehensive candidate and job opportunity directory views.

---

## 🛠️ Technology Stack

- **Backend**: Java 17, Jakarta Servlet API 6.0 (Tomcat 10.1+ / 11.0+)
- **Frontend / Views**: Jakarta Server Pages (JSP), JSTL 3.0, Modern Responsive CSS3 & JavaScript
- **Database & Pooling**: MySQL 8.0+, HikariCP 5.1.0 Connection Pool
- **Security**: jBCrypt for salted password hashing, Role-Based Access Control Filters (RBAC)
- **JSON REST Utilities**: Google Gson 2.10.1
- **Testing**: JUnit 5, Mockito

---

## 🚀 Quick Setup & Installation

### 1. Database Setup (MySQL 8.0+)
Ensure MySQL Server is running on port `3306`:
1. Execute `sql/schema.sql` to initialize the `internship_portal` database and tables.
2. Execute `sql/data.sql` to load seed skills, sample companies, student profiles, and demo internships.
3. Verify or adjust database credentials in `src/main/resources/db.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/internship_portal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
   db.username=root
   db.password=root
   ```

### 2. Running in Eclipse IDE
1. Open **Eclipse IDE for Enterprise Java and Web Developers**.
2. Go to **File** &rarr; **Import...** &rarr; **Maven** &rarr; **Existing Maven Projects**.
3. Select this folder as the root directory and click **Finish**.
4. In the **Servers** tab, configure **Apache Tomcat v10.1 / v11.0**.
5. Right-click the project &rarr; **Run As** &rarr; **Run on Server**.
6. Access the application in your browser at:
   ```
   http://localhost:8080/internship-portal/
   ```

### 3. Running Standalone on Apache Tomcat
1. Build the WAR package using Maven:
   ```cmd
   mvn clean package
   ```
2. Copy `target/internship-portal.war` into Tomcat's `webapps/` folder (`apache-tomcat/webapps/`).
3. Start Tomcat using `bin/startup.bat` (Windows) or `bin/startup.sh` (Linux/Mac).
4. Access at `http://localhost:8080/internship-portal/`.

---

## 👥 Demo Accounts for Testing

All demo accounts use the default password: **`Password@123`**

| Role | Email | Profile / Capabilities |
| :--- | :--- | :--- |
| **Student** | `rahul@student.edu` | MCA, CGPA: 8.85, Skills: Java, Spring Boot, MySQL, React, Docker |
| **Student** | `priya@student.edu` | M.Sc. IT, CGPA: 7.90, Skills: Python, Machine Learning, MySQL |
| **Student** | `amit@student.edu` | M.Tech, CGPA: 9.20, Skills: Java, Kubernetes, AWS, Docker |
| **Corporate Recruiter** | `techcorp@portal.com` | TechCorp Solutions (Approved Recruiter with 4 posted internships) |
| **Corporate Recruiter** | `innovatech@portal.com` | InnovaTech Labs (Approved Recruiter with AI & Frontend roles) |
| **Administrator** | `admin@portal.edu` | Placement Cell Admin (Analytics, Approvals, Weights Tuning) |

---

## 📜 License
Developed for Academic Post-Graduate Curriculum.

