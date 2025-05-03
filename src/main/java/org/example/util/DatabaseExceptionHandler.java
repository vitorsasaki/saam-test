package org.example.util;

import org.example.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTimeoutException;

/**
 * Classe utilitária para tratamento de exceções relacionadas ao banco de dados.
 * Converte exceções SQL específicas em exceções personalizadas da aplicação.
 */
public class DatabaseExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseExceptionHandler.class);
    
    // Códigos de erro comuns em bancos de dados
    private static final int POSTGRES_DUPLICATE_KEY_ERROR = 23505;  // Código para violação de chave única no PostgreSQL
    private static final int POSTGRES_FOREIGN_KEY_VIOLATION = 23503;  // Código para violação de chave estrangeira no PostgreSQL
    private static final String POSTGRES_CONNECTION_FAILURE = "08006";  // Código para falha de conexão no PostgreSQL
    
    /**
     * Converte uma SQLException em uma exceção personalizada da aplicação.
     * Identifica o tipo específico de exceção com base na classe ou no código de erro.
     *
     * @param e a exceção SQL original
     * @param operationDescription descrição da operação que falhou
     * @return uma exceção personalizada mais específica
     */
    public static DatabaseException handleSQLException(SQLException e, String operationDescription) {
        logger.error("Erro de banco de dados durante {}: {}", operationDescription, e.getMessage(), e);
        
        // Primeiro, verificamos o tipo específico da exceção
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return new DatabaseException.IntegrityConstraintViolationException(
                    "Violação de restrição de integridade: " + e.getMessage(), e);
        }
        
        if (e instanceof SQLTimeoutException) {
            return new DatabaseException.DatabaseTimeoutException(
                    "Timeout durante operação no banco de dados: " + e.getMessage(), e);
        }
        
        // Em seguida, verificamos códigos de erro específicos
        String sqlState = e.getSQLState();
        if (sqlState != null) {
            // Violações de chave única/restrição de integridade
            if (sqlState.equals(String.valueOf(POSTGRES_DUPLICATE_KEY_ERROR)) || 
                    e.getMessage().toLowerCase().contains("duplicate") || 
                    e.getMessage().toLowerCase().contains("unique")) {
                return new DatabaseException.IntegrityConstraintViolationException(
                        "Registro duplicado: " + e.getMessage(), e);
            }
            
            // Violações de chave estrangeira
            if (sqlState.equals(String.valueOf(POSTGRES_FOREIGN_KEY_VIOLATION))) {
                return new DatabaseException.IntegrityConstraintViolationException(
                        "Operação viola referência a outro registro: " + e.getMessage(), e);
            }
            
            // Problemas de conexão
            if (sqlState.equals(POSTGRES_CONNECTION_FAILURE) || 
                    e.getMessage().toLowerCase().contains("connection") ||
                    e.getMessage().toLowerCase().contains("timeout")) {
                return new DatabaseException.ConnectionException(
                        "Erro de conexão com o banco de dados: " + e.getMessage(), e);
            }
        }
        
        // Para qualquer outro tipo de exceção SQL, retornamos uma exceção genérica
        return new DatabaseException("Erro no banco de dados: " + e.getMessage(), e);
    }
    
    /**
     * Método utilitário para tratamento de exceções em operações de leitura.
     * Registra a exceção e retorna null ou valor padrão.
     *
     * @param e a exceção SQL original
     * @param operationDescription descrição da operação que falhou
     * @param <T> tipo de retorno
     * @param defaultValue valor padrão a ser retornado em caso de erro
     * @return valor padrão
     */
    public static <T> T handleReadException(SQLException e, String operationDescription, T defaultValue) {
        handleSQLException(e, operationDescription);
        return defaultValue;
    }
} 