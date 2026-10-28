package com.internportal.dao;

import com.internportal.model.SystemConfig;
import com.internportal.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigDAO {

    private static final Logger LOGGER = Logger.getLogger(ConfigDAO.class.getName());

    public List<SystemConfig> findAll() {
        List<SystemConfig> list = new ArrayList<>();
        String sql = "SELECT config_key, config_value, description FROM config ORDER BY config_key ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new SystemConfig(
                        rs.getString("config_key"),
                        rs.getString("config_value"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching configs", e);
        }
        return list;
    }

    public String getValue(String key, String defaultValue) {
        String sql = "SELECT config_value FROM config WHERE config_key = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("config_value");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching config for key: " + key, e);
        }
        return defaultValue;
    }

    public double getDoubleValue(String key, double defaultValue) {
        String val = getValue(key, null);
        if (val != null) {
            try {
                return Double.parseDouble(val.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    public boolean updateValue(String key, String value) {
        String sql = "INSERT INTO config (config_key, config_value) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE config_value = VALUES(config_value)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating config for key: " + key, e);
        }
        return false;
    }

    public Map<String, Double> getMatchingWeights() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("weight_skill", getDoubleValue("weight_skill", 0.50));
        weights.put("weight_cgpa", getDoubleValue("weight_cgpa", 0.25));
        weights.put("weight_project", getDoubleValue("weight_project", 0.15));
        weights.put("weight_location", getDoubleValue("weight_location", 0.10));
        return weights;
    }

    public boolean updateMatchingWeights(double skillWeight, double cgpaWeight,
                                         double projectWeight, double locationWeight) {
        String sql = "UPDATE config SET config_value = ? WHERE config_key = ?";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, String.format(java.util.Locale.US, "%.2f", skillWeight));
                ps.setString(2, "weight_skill");
                ps.addBatch();

                ps.setString(1, String.format(java.util.Locale.US, "%.2f", cgpaWeight));
                ps.setString(2, "weight_cgpa");
                ps.addBatch();

                ps.setString(1, String.format(java.util.Locale.US, "%.2f", projectWeight));
                ps.setString(2, "weight_project");
                ps.addBatch();

                ps.setString(1, String.format(java.util.Locale.US, "%.2f", locationWeight));
                ps.setString(2, "weight_location");
                ps.addBatch();

                ps.executeBatch();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Failed to update matching weights, rolled back", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error updating weights", e);
        }
        return false;
    }
}

