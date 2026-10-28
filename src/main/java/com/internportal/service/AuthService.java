package com.internportal.service;

import com.internportal.dao.CompanyDAO;
import com.internportal.dao.StudentDAO;
import com.internportal.dao.UserDAO;
import com.internportal.model.Company;
import com.internportal.model.Student;
import com.internportal.model.User;
import com.internportal.util.DBUtil;
import com.internportal.util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service managing user authentication and registration workflows.
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    private final UserDAO userDAO;
    private final StudentDAO studentDAO;
    private final CompanyDAO companyDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.studentDAO = new StudentDAO();
        this.companyDAO = new CompanyDAO();
    }

    public AuthService(UserDAO userDAO, StudentDAO studentDAO, CompanyDAO companyDAO) {
        this.userDAO = userDAO;
        this.studentDAO = studentDAO;
        this.companyDAO = companyDAO;
    }

    /**
     * Authenticates a user given email and plaintext password.
     */
    public Optional<User> authenticate(String email, String plainPassword) {
        if (email == null || plainPassword == null) {
            return Optional.empty();
        }

        Optional<User> userOpt = userDAO.findByEmail(email.trim().toLowerCase());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtil.checkPassword(plainPassword, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Registers a new student account and profile transactionally.
     */
    public boolean registerStudent(String email, String plainPassword, Student student) {
        if (email == null || plainPassword == null || student == null) {
            return false;
        }

        if (userDAO.findByEmail(email.trim().toLowerCase()).isPresent()) {
            LOGGER.warning("Registration failed: Email already registered: " + email);
            return false;
        }

        String passwordHash = PasswordUtil.hashPassword(plainPassword);
        User user = new User(email.trim().toLowerCase(), passwordHash, "STUDENT");

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = userDAO.createUser(user, conn);
                if (userId <= 0) {
                    conn.rollback();
                    return false;
                }

                student.setUserId(userId);
                int studentId = studentDAO.createStudent(student, conn);
                if (studentId <= 0) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Transaction error during student registration", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection error during student registration", e);
        }
        return false;
    }

    /**
     * Registers a new company account and profile (pending admin approval).
     */
    public boolean registerCompany(String email, String plainPassword, Company company) {
        if (email == null || plainPassword == null || company == null) {
            return false;
        }

        if (userDAO.findByEmail(email.trim().toLowerCase()).isPresent()) {
            LOGGER.warning("Registration failed: Email already registered: " + email);
            return false;
        }

        String passwordHash = PasswordUtil.hashPassword(plainPassword);
        User user = new User(email.trim().toLowerCase(), passwordHash, "COMPANY");

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = userDAO.createUser(user, conn);
                if (userId <= 0) {
                    conn.rollback();
                    return false;
                }

                company.setUserId(userId);
                company.setApproved(false); // Requires admin approval
                int companyId = companyDAO.createCompany(company, conn);
                if (companyId <= 0) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Transaction error during company registration", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection error during company registration", e);
        }
        return false;
    }
}
