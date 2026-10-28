package com.internportal.dao;

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

public class SkillDAO {

    private static final Logger LOGGER = Logger.getLogger(SkillDAO.class.getName());

    public List<Skill> findAll() {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT id, skill_name, category FROM skills ORDER BY skill_name ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                skills.add(mapResultSetToSkill(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching skills", e);
        }
        return skills;
    }

    public Optional<Skill> findById(int id) {
        String sql = "SELECT id, skill_name, category FROM skills WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSkill(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding skill by id: " + id, e);
        }
        return Optional.empty();
    }

    public Optional<Skill> findByName(String name) {
        String sql = "SELECT id, skill_name, category FROM skills WHERE LOWER(skill_name) = LOWER(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name != null ? name.trim() : "");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSkill(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding skill by name: " + name, e);
        }
        return Optional.empty();
    }

    public int createSkill(Skill skill) {
        String sql = "INSERT INTO skills (skill_name, category) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, skill.getSkillName().trim());
            ps.setString(2, skill.getCategory() != null ? skill.getCategory().trim() : "General");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    skill.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating skill: " + skill.getSkillName(), e);
        }
        return 0;
    }

    public boolean updateSkill(Skill skill) {
        String sql = "UPDATE skills SET skill_name = ?, category = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, skill.getSkillName().trim());
            ps.setString(2, skill.getCategory().trim());
            ps.setInt(3, skill.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating skill: " + skill.getId(), e);
        }
        return false;
    }

    public boolean deleteSkill(int id) {
        String sql = "DELETE FROM skills WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting skill: " + id, e);
        }
        return false;
    }

    private Skill mapResultSetToSkill(ResultSet rs) throws SQLException {
        Skill skill = new Skill();
        skill.setId(rs.getInt("id"));
        skill.setSkillName(rs.getString("skill_name"));
        skill.setCategory(rs.getString("category"));
        return skill;
    }
}

