package com.auth.config;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConfig {
    public static DataSource getDataSource() {
        HikariConfig config = new HikariConfig();

        // Fetch values from environment variables with defaults
        String jdbcUrl = System.getenv().getOrDefault("DB_JDBC_URL", "jdbc:postgresql://localhost:5432/java");
        String username = System.getenv().getOrDefault("DB_USERNAME", "postgres");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "postgres");
        int maxPoolSize = Integer.parseInt(System.getenv().getOrDefault("DB_MAX_POOL_SIZE", "10"));
        int minIdle = Integer.parseInt(System.getenv().getOrDefault("DB_MIN_IDLE", "2"));
        long idleTimeout = Long.parseLong(System.getenv().getOrDefault("DB_IDLE_TIMEOUT", "30000"));

        // Apply values to HikariConfig
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setIdleTimeout(idleTimeout);

        return new HikariDataSource(config);
    }
}
