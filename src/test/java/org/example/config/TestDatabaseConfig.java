package org.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Configuração de banco de dados para testes unitários usando H2 em memória.
 */
public class TestDatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestDatabaseConfig.class);
    private static final String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";
    private static final String DRIVER_CLASS = "org.h2.Driver";
    
    private static HikariDataSource dataSource;
    
    /**
     * Inicializa o pool de conexões
     */
    public static synchronized void initializeDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            try {
                configureDataSource();
            } catch (Exception e) {
                logger.error("Erro ao configurar pool de conexões para testes", e);
            }
        }
    }
    
    private static void configureDataSource() {
        HikariConfig config = new HikariConfig();
        
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setDriverClassName(DRIVER_CLASS);
        
        // Configurações do pool de conexões
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setConnectionTimeout(30000);
        
        // Configuração para testes
        config.setAutoCommit(true);
        
        // Evitar que conexões sejam fechadas entre os testes
        config.setConnectionTestQuery("SELECT 1");
        config.setLeakDetectionThreshold(60000);
        
        dataSource = new HikariDataSource(config);
        
        logger.info("Pool de conexões H2 para testes configurado com sucesso");
    }
    
    /**
     * Obtém uma conexão do pool de conexões H2 para testes.
     * 
     * @return Connection objeto de conexão
     * @throws SQLException em caso de erro na conexão
     */
    public static Connection getConnection() throws SQLException {
        initializeDataSource();
        
        return dataSource.getConnection();
    }
    
    /**
     * Fecha o pool de conexões.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
            logger.info("Pool de conexões H2 para testes encerrado");
        }
    }
} 