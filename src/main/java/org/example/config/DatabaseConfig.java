package org.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    static {
        try {
            initDataSource();
        } catch (IOException e) {
            logger.error("Erro ao inicializar a conexão com o banco de dados", e);
        }
    }

    private static void initDataSource() throws IOException {
        Properties props = new Properties();

        try {
            props.load(DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties"));
        } catch (Exception e) {
            logger.warn("Arquivo database.properties não encontrado, usando valores padrão", e);
            props.setProperty("db.url", "jdbc:postgresql://localhost:5432/saam_db");
            props.setProperty("db.user", "postgres");
            props.setProperty("db.password", "postgres");
            props.setProperty("db.poolSize", "10");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.user"));
        config.setPassword(props.getProperty("db.password"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.poolSize", "10")));

        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        dataSource = new HikariDataSource(config);
        logger.info("Pool de conexões inicializado com sucesso");
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

} 