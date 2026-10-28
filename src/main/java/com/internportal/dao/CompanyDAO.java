package com.internportal.dao;

import com.internportal.model.Company;
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

public class CompanyDAO {

    private static final Logger LOGGER = Logger.getLogger(CompanyDAO.class.getName());

    public Optional<Company> findById(int id) {
        String sql = "SELECT c.id, c.user_id, c.company_name, c.industry, c.website, c.location, " +
                     "c.description, c.is_approved, c.created_at, u.email " +
                     "FROM companies c " +
                     "INNER JOIN users u ON c.user_id = u.id " +
                     "WHERE c.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding company by id: " + id, e);
        }
        return Optional.empty();
    }

    public Optional<Company> findByUserId(int userId) {
        String sql = "SELECT c.id, c.user_id, c.company_name, c.industry, c.website, c.location, " +
                     "c.description, c.is_approved, c.created_at, u.email " +
                     "FROM companies c " +
                     "INNER JOIN users u ON c.user_id = u.id " +
                     "WHERE c.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding company by user_id: " + userId, e);
        }
        return Optional.empty();
    }

    public int createCompany(Company company, Connection conn) throws SQLException {
        String sql = "INSERT INTO companies (user_id, company_name, industry, website, location, description, is_approved) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, company.getUserId());
            ps.setString(2, company.getCompanyName());
            ps.setString(3, company.getIndustry());
            ps.setString(4, company.getWebsite());
            ps.setString(5, company.getLocation());
            ps.setString(6, company.getDescription());
            ps.setBoolean(7, company.isApproved());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    company.setId(id);
                    return id;
                }
            }
        }
        return 0;
    }

    public boolean updateCompany(Company company) {
        String sql = "UPDATE companies SET company_name = ?, industry = ?, website = ?, location = ?, description = ? " +
                     "WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, company.getCompanyName());
            ps.setString(2, company.getIndustry());
            ps.setString(3, company.getWebsite());
            ps.setString(4, company.getLocation());
            ps.setString(5, company.getDescription());
            ps.setInt(6, company.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating company: " + company.getId(), e);
        }
        return false;
    }

    public boolean setApprovalStatus(int companyId, boolean approved) {
        String sql = "UPDATE companies SET is_approved = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, approved);
            ps.setInt(2, companyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating approval status for company: " + companyId, e);
        }
        return false;
    }

    public List<Company> findAll() {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT c.id, c.user_id, c.company_name, c.industry, c.website, c.location, " +
                     "c.description, c.is_approved, c.created_at, u.email " +
                     "FROM companies c " +
                     "INNER JOIN users u ON c.user_id = u.id " +
                     "ORDER BY c.company_name ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToCompany(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all companies", e);
        }
        return list;
    }

    public List<Company> findPending() {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT c.id, c.user_id, c.company_name, c.industry, c.website, c.location, " +
                     "c.description, c.is_approved, c.created_at, u.email " +
                     "FROM companies c " +
                     "INNER JOIN users u ON c.user_id = u.id " +
                     "WHERE c.is_approved = FALSE " +
                     "ORDER BY c.created_at ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToCompany(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching pending companies", e);
        }
        return list;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM companies";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting companies", e);
        }
        return 0;
    }

    public int getPendingCount() {
        String sql = "SELECT COUNT(*) FROM companies WHERE is_approved = FALSE";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting pending companies", e);
        }
        return 0;
    }

    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setCompanyName(rs.getString("company_name"));
        c.setIndustry(rs.getString("industry"));
        c.setWebsite(rs.getString("website"));
        c.setLocation(rs.getString("location"));
        c.setDescription(rs.getString("description"));
        c.setApproved(rs.getBoolean("is_approved"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setEmail(rs.getString("email"));
        return c;
    }
}

