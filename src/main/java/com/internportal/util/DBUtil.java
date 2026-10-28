package com.internportal.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * High-performance database connection pool manager using HikariCP.
 * Features thread-safe lazy initialization and resilient auto-reconnect.
 */
public class DBUtil {

    private static final Logger LOGGER = Logger.getLogger(DBUtil.class.getName());
    private static volatile HikariDataSource dataSource;

    private DBUtil() {
        // Utility class, prevent instantiation
    }

    private static synchronized void initDataSource() throws SQLException {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        try {
            Properties props = new Properties();
            try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (is != null) {
                    props.load(is);
                } else {
                    LOGGER.warning("db.properties not found on classpath, applying fallback properties");
                }
            }

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            config.setJdbcUrl(props.getProperty("db.url", "jdbc:mysql://localhost:3306/internship_portal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8"));
            config.setUsername(props.getProperty("db.username", "root"));
            config.setPassword(props.getProperty("db.password", "root"));

            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("pool.maximumPoolSize", "10")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("pool.minimumIdle", "2")));
            config.setIdleTimeout(Long.parseLong(props.getProperty("pool.idleTimeout", "30000")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("pool.connectionTimeout", "10000")));
            config.setMaxLifetime(Long.parseLong(props.getProperty("pool.maxLifetime", "1800000")));

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");

            dataSource = new HikariDataSource(config);
            LOGGER.info("HikariCP connection pool successfully initialized for internship_portal");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to MySQL database", e);
            throw new SQLException("Database connection failure. Please verify MySQL service is running on port 3306: " + e.getMessage(), e);
        }
    }

    /**
     * Obtains an active connection from the pool.
     * Must be closed by caller via try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    /**
     * Closes the connection pool during application shutdown.
     */
    public static synchronized void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
            LOGGER.info("HikariCP connection pool closed successfully");
        }
    }
}
