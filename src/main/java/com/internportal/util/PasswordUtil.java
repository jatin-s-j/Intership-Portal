package com.internportal.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for hashing and verifying user passwords using BCrypt.
 */
public class PasswordUtil {

    private static final int LOG_ROUNDS = 10;

    private PasswordUtil() {
        // Utility class, prevent instantiation
    }

    /**
     * Hashes a plaintext password using BCrypt with salt.
     *
     * @param plainPassword Raw password
     * @return Salted and hashed password string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Verifies if a plaintext password matches the stored BCrypt hash.
     *
     * @param plainPassword  Raw password attempt
     * @param hashedPassword Stored BCrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.trim().isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}

