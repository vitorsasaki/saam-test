package org.example.util;

import org.example.config.TestDatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utilitário para operações com o banco H2 durante os testes.
 */
public class H2DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(H2DatabaseUtil.class);
    
    /**
     * Inicializa o banco de dados para testes.
     */
    public static void inicializarBancoDados() {
        TestDatabaseConfig.initializeDataSource();
        limparBancoDados();
        criarTabelaUsuarios();
        criarTabelaFuncionarios();
        logger.info("Banco de dados de teste inicializado com sucesso");
    }
    
    /**
     * Cria a tabela de usuários para testes.
     */
    public static void criarTabelaUsuarios() {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id SERIAL PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) NOT NULL UNIQUE, " +
                "senha VARCHAR(100) NOT NULL)";
        
        executarSQL(sql, "Tabela 'usuarios' criada para testes");
    }
    
    /**
     * Cria a tabela de funcionários para testes.
     */
    public static void criarTabelaFuncionarios() {
        String sql = "CREATE TABLE IF NOT EXISTS funcionarios (" +
                "id SERIAL PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "data_admissao DATE NOT NULL, " +
                "salario NUMERIC(10,2) NOT NULL, " +
                "status BOOLEAN NOT NULL, " +
                "departamento_id BIGINT)";
        
        executarSQL(sql, "Tabela 'funcionarios' criada para testes");
    }
    
    /**
     * Limpa as tabelas do banco de dados.
     */
    public static void limparBancoDados() {
        String sql1 = "DROP TABLE IF EXISTS usuarios CASCADE";
        executarSQL(sql1, "Tabela 'usuarios' apagada para testes");
        
        String sql2 = "DROP TABLE IF EXISTS funcionarios CASCADE";
        executarSQL(sql2, "Tabela 'funcionarios' apagada para testes");
        
        String sql3 = "DROP TABLE IF EXISTS logs CASCADE";
        executarSQL(sql3, "Tabela 'logs' apagada para testes");
    }
    
    /**
     * Executa um comando SQL.
     * 
     * @param sql Comando SQL a ser executado
     * @param mensagemSucesso Mensagem a ser logada em caso de sucesso
     */
    private static void executarSQL(String sql, String mensagemSucesso) {
        try (Connection conn = TestDatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            logger.info(mensagemSucesso);
            
        } catch (SQLException e) {
            logger.error("Erro ao executar SQL: {}", sql, e);
        }
    }
    
    /**
     * Fecha o pool de conexões do banco de dados de teste.
     */
    public static void fecharConexoes() {
        TestDatabaseConfig.shutdown();
        logger.info("Conexões de teste encerradas");
    }
} 