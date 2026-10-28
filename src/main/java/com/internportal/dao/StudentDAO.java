package com.internportal.dao;

import com.internportal.model.Skill;
import com.internportal.model.Student;
import com.internportal.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentDAO {

    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());

    public Optional<Student> findById(int id) {
        String sql = "SELECT s.id, s.user_id, s.full_name, s.roll_number, s.course, s.semester, " +
                     "s.cgpa, s.active_backlogs, s.preferred_location, s.projects, s.resume_url, " +
                     "s.created_at, u.email " +
                     "FROM students s " +
                     "INNER JOIN users u ON s.user_id = u.id " +
                     "WHERE s.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student student = mapResultSetToStudent(rs);
                    student.setSkills(getSkillsForStudent(conn, student.getId()));
                    return Optional.of(student);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding student by id: " + id, e);
        }
        return Optional.empty();
    }

    public Optional<Student> findByUserId(int userId) {
        String sql = "SELECT s.id, s.user_id, s.full_name, s.roll_number, s.course, s.semester, " +
                     "s.cgpa, s.active_backlogs, s.preferred_location, s.projects, s.resume_url, " +
                     "s.created_at, u.email " +
                     "FROM students s " +
                     "INNER JOIN users u ON s.user_id = u.id " +
                     "WHERE s.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student student = mapResultSetToStudent(rs);
                    student.setSkills(getSkillsForStudent(conn, student.getId()));
                    return Optional.of(student);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding student by user_id: " + userId, e);
        }
        return Optional.empty();
    }

    public int createStudent(Student student, Connection conn) throws SQLException {
        String sql = "INSERT INTO students (user_id, full_name, roll_number, course, semester, " +
                     "cgpa, active_backlogs, preferred_location, projects, resume_url) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, student.getUserId());
            ps.setString(2, student.getFullName());
            ps.setString(3, student.getRollNumber());
            ps.setString(4, student.getCourse());
            ps.setInt(5, student.getSemester());
            ps.setBigDecimal(6, student.getCgpa());
            ps.setInt(7, student.getActiveBacklogs());
            ps.setString(8, student.getPreferredLocation());
            ps.setString(9, student.getProjects());
            ps.setString(10, student.getResumeUrl());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    student.setId(id);
                    return id;
                }
            }
        }
        return 0;
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET full_name = ?, roll_number = ?, course = ?, semester = ?, " +
                     "cgpa = ?, active_backlogs = ?, preferred_location = ?, projects = ?, resume_url = ? " +
                     "WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getFullName());
            ps.setString(2, student.getRollNumber());
            ps.setString(3, student.getCourse());
            ps.setInt(4, student.getSemester());
            ps.setBigDecimal(5, student.getCgpa());
            ps.setInt(6, student.getActiveBacklogs());
            ps.setString(7, student.getPreferredLocation());
            ps.setString(8, student.getProjects());
            ps.setString(9, student.getResumeUrl());
            ps.setInt(10, student.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating student: " + student.getId(), e);
        }
        return false;
    }

    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.id, s.user_id, s.full_name, s.roll_number, s.course, s.semester, " +
                     "s.cgpa, s.active_backlogs, s.preferred_location, s.projects, s.resume_url, " +
                     "s.created_at, u.email " +
                     "FROM students s " +
                     "INNER JOIN users u ON s.user_id = u.id " +
                     "ORDER BY s.id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Student student = mapResultSetToStudent(rs);
                student.setSkills(getSkillsForStudent(conn, student.getId()));
                list.add(student);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all students", e);
        }
        return list;
    }

    public List<Skill> getSkillsForStudent(Connection conn, int studentId) throws SQLException {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT sk.id, sk.skill_name, sk.category, ss.proficiency_level " +
                     "FROM student_skills ss " +
                     "INNER JOIN skills sk ON ss.skill_id = sk.id " +
                     "WHERE ss.student_id = ? " +
                     "ORDER BY sk.skill_name ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Skill s = new Skill();
                    s.setId(rs.getInt("id"));
                    s.setSkillName(rs.getString("skill_name"));
                    s.setCategory(rs.getString("category"));
                    s.setProficiencyLevel(rs.getString("proficiency_level"));
                    skills.add(s);
                }
            }
        }
        return skills;
    }

    public List<Skill> getSkillsForStudent(int studentId) {
        try (Connection conn = DBUtil.getConnection()) {
            return getSkillsForStudent(conn, studentId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching skills for student: " + studentId, e);
        }
        return new ArrayList<>();
    }

    public boolean addSkillToStudent(int studentId, int skillId, String proficiency) {
        String sql = "INSERT INTO student_skills (student_id, skill_id, proficiency_level) " +
                     "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE proficiency_level = VALUES(proficiency_level)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, skillId);
            ps.setString(3, proficiency != null ? proficiency : "Intermediate");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding skill to student", e);
        }
        return false;
    }

    public boolean removeSkillFromStudent(int studentId, int skillId) {
        String sql = "DELETE FROM student_skills WHERE student_id = ? AND skill_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, skillId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing skill from student", e);
        }
        return false;
    }

    public boolean syncStudentSkills(int studentId, List<Skill> skills) {
        String deleteSql = "DELETE FROM student_skills WHERE student_id = ?";
        String insertSql = "INSERT INTO student_skills (student_id, skill_id, proficiency_level) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                    psDel.setInt(1, studentId);
                    psDel.executeUpdate();
                }
                if (skills != null && !skills.isEmpty()) {
                    try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                        for (Skill s : skills) {
                            psIns.setInt(1, studentId);
                            psIns.setInt(2, s.getId());
                            psIns.setString(3, s.getProficiencyLevel() != null ? s.getProficiencyLevel() : "Intermediate");
                            psIns.addBatch();
                        }
                        psIns.executeBatch();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error syncing student skills, rolled back", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error during skill sync", e);
        }
        return false;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting students", e);
        }
        return 0;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setUserId(rs.getInt("user_id"));
        s.setFullName(rs.getString("full_name"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setCourse(rs.getString("course"));
        s.setSemester(rs.getInt("semester"));
        s.setCgpa(rs.getBigDecimal("cgpa"));
        s.setActiveBacklogs(rs.getInt("active_backlogs"));
        s.setPreferredLocation(rs.getString("preferred_location"));
        s.setProjects(rs.getString("projects"));
        s.setResumeUrl(rs.getString("resume_url"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setEmail(rs.getString("email"));
        return s;
    }
}

