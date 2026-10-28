package com.internportal.dao;

import com.internportal.model.Application;
import com.internportal.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationDAO {

    private static final Logger LOGGER = Logger.getLogger(ApplicationDAO.class.getName());

    public Optional<Application> findById(int id) {
        String sql = "SELECT a.id, a.internship_id, a.student_id, a.match_score, a.status, a.applied_at, " +
                     "s.full_name AS student_name, s.roll_number, s.course, s.semester, s.cgpa, s.resume_url, u.email AS student_email, " +
                     "i.title AS internship_title, c.company_name, i.location AS internship_location, i.stipend " +
                     "FROM applications a " +
                     "INNER JOIN students s ON a.student_id = s.id " +
                     "INNER JOIN users u ON s.user_id = u.id " +
                     "INNER JOIN internships i ON a.internship_id = i.id " +
                     "INNER JOIN companies c ON i.company_id = c.id " +
                     "WHERE a.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToApplication(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding application by id: " + id, e);
        }
        return Optional.empty();
    }

    public boolean hasStudentApplied(int internshipId, int studentId) {
        String sql = "SELECT id FROM applications WHERE internship_id = ? AND student_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, internshipId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking existing application", e);
        }
        return false;
    }

    public int createApplication(Application app) {
        String sql = "INSERT INTO applications (internship_id, student_id, match_score, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, app.getInternshipId());
            ps.setInt(2, app.getStudentId());
            ps.setBigDecimal(3, app.getMatchScore());
            ps.setString(4, app.getStatus() != null ? app.getStatus() : "APPLIED");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    app.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating application", e);
        }
        return 0;
    }

    public boolean updateStatus(int applicationId, String status) {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating status for application: " + applicationId, e);
        }
        return false;
    }

    public List<Application> findByStudentId(int studentId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.id, a.internship_id, a.student_id, a.match_score, a.status, a.applied_at, " +
                     "s.full_name AS student_name, s.roll_number, s.course, s.semester, s.cgpa, s.resume_url, u.email AS student_email, " +
                     "i.title AS internship_title, c.company_name, i.location AS internship_location, i.stipend " +
                     "FROM applications a " +
                     "INNER JOIN students s ON a.student_id = s.id " +
                     "INNER JOIN users u ON s.user_id = u.id " +
                     "INNER JOIN internships i ON a.internship_id = i.id " +
                     "INNER JOIN companies c ON i.company_id = c.id " +
                     "WHERE a.student_id = ? " +
                     "ORDER BY a.applied_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToApplication(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching applications for student: " + studentId, e);
        }
        return list;
    }

    public List<Application> findByInternshipIdRanked(int internshipId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.id, a.internship_id, a.student_id, a.match_score, a.status, a.applied_at, " +
                     "s.full_name AS student_name, s.roll_number, s.course, s.semester, s.cgpa, s.resume_url, u.email AS student_email, " +
                     "i.title AS internship_title, c.company_name, i.location AS internship_location, i.stipend " +
                     "FROM applications a " +
                     "INNER JOIN students s ON a.student_id = s.id " +
                     "INNER JOIN users u ON s.user_id = u.id " +
                     "INNER JOIN internships i ON a.internship_id = i.id " +
                     "INNER JOIN companies c ON i.company_id = c.id " +
                     "WHERE a.internship_id = ? " +
                     "ORDER BY a.match_score DESC, s.cgpa DESC, a.applied_at ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, internshipId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToApplication(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching ranked applicants for internship: " + internshipId, e);
        }
        return list;
    }

    public List<Application> findAll() {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.id, a.internship_id, a.student_id, a.match_score, a.status, a.applied_at, " +
                     "s.full_name AS student_name, s.roll_number, s.course, s.semester, s.cgpa, s.resume_url, u.email AS student_email, " +
                     "i.title AS internship_title, c.company_name, i.location AS internship_location, i.stipend " +
                     "FROM applications a " +
                     "INNER JOIN students s ON a.student_id = s.id " +
                     "INNER JOIN users u ON s.user_id = u.id " +
                     "INNER JOIN internships i ON a.internship_id = i.id " +
                     "INNER JOIN companies c ON i.company_id = c.id " +
                     "ORDER BY a.applied_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all applications", e);
        }
        return list;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM applications";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting applications", e);
        }
        return 0;
    }

    public Map<String, Integer> getStatusCounts() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("APPLIED", 0);
        counts.put("SHORTLISTED", 0);
        counts.put("SELECTED", 0);
        counts.put("REJECTED", 0);

        String sql = "SELECT status, COUNT(*) AS cnt FROM applications GROUP BY status";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("status"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting application status counts", e);
        }
        return counts;
    }

    public double getAverageMatchScore() {
        String sql = "SELECT AVG(match_score) FROM applications";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating average match score", e);
        }
        return 0.0;
    }

    private Application mapResultSetToApplication(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setId(rs.getInt("id"));
        a.setInternshipId(rs.getInt("internship_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setMatchScore(rs.getBigDecimal("match_score"));
        a.setStatus(rs.getString("status"));
        a.setAppliedAt(rs.getTimestamp("applied_at"));

        a.setStudentFullName(rs.getString("student_name"));
        a.setStudentRollNumber(rs.getString("roll_number"));
        a.setStudentCourse(rs.getString("course"));
        a.setStudentSemester(rs.getInt("semester"));
        a.setStudentCgpa(rs.getBigDecimal("cgpa"));
        a.setStudentResumeUrl(rs.getString("resume_url"));
        a.setStudentEmail(rs.getString("student_email"));

        a.setInternshipTitle(rs.getString("internship_title"));
        a.setCompanyName(rs.getString("company_name"));
        a.setInternshipLocation(rs.getString("internship_location"));
        a.setInternshipStipend(rs.getBigDecimal("stipend"));
        return a;
    }
}

