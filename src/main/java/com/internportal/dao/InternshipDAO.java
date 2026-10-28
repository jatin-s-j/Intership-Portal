package com.internportal.dao;

import com.internportal.model.Internship;
import com.internportal.model.Skill;
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

public class InternshipDAO {

    private static final Logger LOGGER = Logger.getLogger(InternshipDAO.class.getName());

    public Optional<Internship> findById(int id) {
        String sql = "SELECT i.id, i.company_id, i.title, i.description, i.location, i.stipend, " +
                     "i.duration_months, i.min_cgpa, i.min_semester, i.max_backlogs, i.allowed_courses, " +
                     "i.deadline, i.status, i.created_at, c.company_name, c.location AS comp_location " +
                     "FROM internships i " +
                     "INNER JOIN companies c ON i.company_id = c.id " +
                     "WHERE i.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Internship internship = mapResultSetToInternship(rs);
                    internship.setSkills(getSkillsForInternship(conn, internship.getId()));
                    return Optional.of(internship);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding internship by id: " + id, e);
        }
        return Optional.empty();
    }

    public List<Internship> findAll() {
        List<Internship> list = new ArrayList<>();
        String sql = "SELECT i.id, i.company_id, i.title, i.description, i.location, i.stipend, " +
                     "i.duration_months, i.min_cgpa, i.min_semester, i.max_backlogs, i.allowed_courses, " +
                     "i.deadline, i.status, i.created_at, c.company_name, c.location AS comp_location " +
                     "FROM internships i " +
                     "INNER JOIN companies c ON i.company_id = c.id " +
                     "ORDER BY i.created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Internship internship = mapResultSetToInternship(rs);
                internship.setSkills(getSkillsForInternship(conn, internship.getId()));
                list.add(internship);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all internships", e);
        }
        return list;
    }

    public List<Internship> findOpenInternships() {
        List<Internship> list = new ArrayList<>();
        String sql = "SELECT i.id, i.company_id, i.title, i.description, i.location, i.stipend, " +
                     "i.duration_months, i.min_cgpa, i.min_semester, i.max_backlogs, i.allowed_courses, " +
                     "i.deadline, i.status, i.created_at, c.company_name, c.location AS comp_location " +
                     "FROM internships i " +
                     "INNER JOIN companies c ON i.company_id = c.id " +
                     "WHERE i.status = 'OPEN' AND c.is_approved = TRUE " +
                     "ORDER BY i.deadline ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Internship internship = mapResultSetToInternship(rs);
                internship.setSkills(getSkillsForInternship(conn, internship.getId()));
                list.add(internship);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching open internships", e);
        }
        return list;
    }

    public List<Internship> findByCompanyId(int companyId) {
        List<Internship> list = new ArrayList<>();
        String sql = "SELECT i.id, i.company_id, i.title, i.description, i.location, i.stipend, " +
                     "i.duration_months, i.min_cgpa, i.min_semester, i.max_backlogs, i.allowed_courses, " +
                     "i.deadline, i.status, i.created_at, c.company_name, c.location AS comp_location " +
                     "FROM internships i " +
                     "INNER JOIN companies c ON i.company_id = c.id " +
                     "WHERE i.company_id = ? " +
                     "ORDER BY i.created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Internship internship = mapResultSetToInternship(rs);
                    internship.setSkills(getSkillsForInternship(conn, internship.getId()));
                    list.add(internship);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching internships for company: " + companyId, e);
        }
        return list;
    }

    public int createInternship(Internship internship) {
        String sql = "INSERT INTO internships (company_id, title, description, location, stipend, " +
                     "duration_months, min_cgpa, min_semester, max_backlogs, allowed_courses, deadline, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int internshipId = 0;
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, internship.getCompanyId());
                    ps.setString(2, internship.getTitle());
                    ps.setString(3, internship.getDescription());
                    ps.setString(4, internship.getLocation());
                    ps.setBigDecimal(5, internship.getStipend());
                    ps.setInt(6, internship.getDurationMonths());
                    ps.setBigDecimal(7, internship.getMinCgpa());
                    ps.setInt(8, internship.getMinSemester());
                    ps.setInt(9, internship.getMaxBacklogs());
                    ps.setString(10, internship.getAllowedCourses());
                    ps.setDate(11, internship.getDeadline());
                    ps.setString(12, internship.getStatus() != null ? internship.getStatus() : "OPEN");
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            internshipId = rs.getInt(1);
                            internship.setId(internshipId);
                        }
                    }
                }

                if (internshipId > 0 && internship.getSkills() != null && !internship.getSkills().isEmpty()) {
                    String skillSql = "INSERT INTO internship_skills (internship_id, skill_id, is_mandatory) VALUES (?, ?, ?)";
                    try (PreparedStatement psSkill = conn.prepareStatement(skillSql)) {
                        for (Skill s : internship.getSkills()) {
                            psSkill.setInt(1, internshipId);
                            psSkill.setInt(2, s.getId());
                            psSkill.setBoolean(3, s.isMandatory());
                            psSkill.addBatch();
                        }
                        psSkill.executeBatch();
                    }
                }

                conn.commit();
                return internshipId;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Failed to create internship, transaction rolled back", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error creating internship", e);
        }
        return 0;
    }

    public boolean updateInternship(Internship internship) {
        String sql = "UPDATE internships SET title = ?, description = ?, location = ?, stipend = ?, " +
                     "duration_months = ?, min_cgpa = ?, min_semester = ?, max_backlogs = ?, " +
                     "allowed_courses = ?, deadline = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, internship.getTitle());
                    ps.setString(2, internship.getDescription());
                    ps.setString(3, internship.getLocation());
                    ps.setBigDecimal(4, internship.getStipend());
                    ps.setInt(5, internship.getDurationMonths());
                    ps.setBigDecimal(6, internship.getMinCgpa());
                    ps.setInt(7, internship.getMinSemester());
                    ps.setInt(8, internship.getMaxBacklogs());
                    ps.setString(9, internship.getAllowedCourses());
                    ps.setDate(10, internship.getDeadline());
                    ps.setString(11, internship.getStatus());
                    ps.setInt(12, internship.getId());
                    ps.executeUpdate();
                }

                // Sync skills
                if (internship.getSkills() != null) {
                    try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM internship_skills WHERE internship_id = ?")) {
                        psDel.setInt(1, internship.getId());
                        psDel.executeUpdate();
                    }
                    if (!internship.getSkills().isEmpty()) {
                        String skillSql = "INSERT INTO internship_skills (internship_id, skill_id, is_mandatory) VALUES (?, ?, ?)";
                        try (PreparedStatement psIns = conn.prepareStatement(skillSql)) {
                            for (Skill s : internship.getSkills()) {
                                psIns.setInt(1, internship.getId());
                                psIns.setInt(2, s.getId());
                                psIns.setBoolean(3, s.isMandatory());
                                psIns.addBatch();
                            }
                            psIns.executeBatch();
                        }
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Failed to update internship, transaction rolled back", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error updating internship", e);
        }
        return false;
    }

    public boolean updateStatus(int internshipId, String status) {
        String sql = "UPDATE internships SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, internshipId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating status for internship: " + internshipId, e);
        }
        return false;
    }

    public boolean deleteInternship(int id) {
        String sql = "DELETE FROM internships WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting internship: " + id, e);
        }
        return false;
    }

    public List<Skill> getSkillsForInternship(Connection conn, int internshipId) throws SQLException {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT sk.id, sk.skill_name, sk.category, ik.is_mandatory " +
                     "FROM internship_skills ik " +
                     "INNER JOIN skills sk ON ik.skill_id = sk.id " +
                     "WHERE ik.internship_id = ? " +
                     "ORDER BY ik.is_mandatory DESC, sk.skill_name ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, internshipId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Skill s = new Skill();
                    s.setId(rs.getInt("id"));
                    s.setSkillName(rs.getString("skill_name"));
                    s.setCategory(rs.getString("category"));
                    s.setMandatory(rs.getBoolean("is_mandatory"));
                    skills.add(s);
                }
            }
        }
        return skills;
    }

    public List<Skill> getSkillsForInternship(int internshipId) {
        try (Connection conn = DBUtil.getConnection()) {
            return getSkillsForInternship(conn, internshipId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching skills for internship: " + internshipId, e);
        }
        return new ArrayList<>();
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM internships";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting internships", e);
        }
        return 0;
    }

    public int getOpenCount() {
        String sql = "SELECT COUNT(*) FROM internships WHERE status = 'OPEN'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting open internships", e);
        }
        return 0;
    }

    private Internship mapResultSetToInternship(ResultSet rs) throws SQLException {
        Internship i = new Internship();
        i.setId(rs.getInt("id"));
        i.setCompanyId(rs.getInt("company_id"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setLocation(rs.getString("location"));
        i.setStipend(rs.getBigDecimal("stipend"));
        i.setDurationMonths(rs.getInt("duration_months"));
        i.setMinCgpa(rs.getBigDecimal("min_cgpa"));
        i.setMinSemester(rs.getInt("min_semester"));
        i.setMaxBacklogs(rs.getInt("max_backlogs"));
        i.setAllowedCourses(rs.getString("allowed_courses"));
        i.setDeadline(rs.getDate("deadline"));
        i.setStatus(rs.getString("status"));
        i.setCreatedAt(rs.getTimestamp("created_at"));
        i.setCompanyName(rs.getString("company_name"));
        i.setCompanyLocation(rs.getString("comp_location"));
        return i;
    }
}

