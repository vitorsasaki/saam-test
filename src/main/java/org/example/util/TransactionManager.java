package org.example.util;

import org.example.config.DatabaseConfig;
import org.example.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Gerenciador de transações para operações de banco de dados.
 * Fornece métodos para executar operações em transações atômicas.
 */
public class TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
    
    /**
     * Executa uma operação dentro de uma transação.
     * A transação será confirmada (commit) se a operação for bem-sucedida,
     * ou revertida (rollback) se ocorrer uma exceção.
     *
     * @param operation operação a ser executada dentro da transação
     * @param description descrição da operação para logs
     * @param <T> tipo de retorno da operação
     * @return resultado da operação
     * @throws DatabaseException se ocorrer um erro durante a transação
     */
    public static <T> T executeInTransaction(TransactionCallable<T> operation, String description)
            throws DatabaseException {
        Connection connection = null;
        boolean originalAutoCommit = false;
        
        try {
            connection = DatabaseConfig.getConnection();
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            
            logger.debug("Iniciando transação para: {}", description);
            T result = operation.call(connection);
            
            connection.commit();
            logger.debug("Transação concluída com sucesso: {}", description);
            
            return result;
            
        } catch (SQLException e) {
            // Tenta fazer rollback da transação em caso de erro
            if (connection != null) {
                try {
                    connection.rollback();
                    logger.warn("Transação revertida após erro: {}", description);
                } catch (SQLException rollbackEx) {
                    logger.error("Erro ao reverter transação: {}", description, rollbackEx);
                }
            }
            
            throw DatabaseExceptionHandler.handleSQLException(e, "executar transação para " + description);
            
        } catch (Exception e) {
            // Tenta fazer rollback da transação em caso de erro
            if (connection != null) {
                try {
                    connection.rollback();
                    logger.warn("Transação revertida após erro: {}", description);
                } catch (SQLException rollbackEx) {
                    logger.error("Erro ao reverter transação: {}", description, rollbackEx);
                }
            }
            
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new DatabaseException.TransactionException("Erro durante transação: " + description, e);
            
        } finally {
            // Restaura o estado original do autoCommit e fecha a conexão
            if (connection != null) {
                try {
                    connection.setAutoCommit(originalAutoCommit);
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Erro ao fechar conexão após transação: {}", description, e);
                }
            }
        }
    }
    
    /**
     * Executa uma operação sem retorno dentro de uma transação.
     *
     * @param operation operação a ser executada dentro da transação
     * @param description descrição da operação para logs
     * @throws DatabaseException se ocorrer um erro durante a transação
     */
    public static void executeInTransaction(TransactionRunnable operation, String description)
            throws DatabaseException {
        executeInTransaction(conn -> {
            operation.run(conn);
            return null;
        }, description);
    }
    
    /**
     * Interface funcional para operações que retornam um resultado e podem ser executadas em uma transação.
     *
     * @param <T> tipo de retorno da operação
     */
    @FunctionalInterface
    public interface TransactionCallable<T> {
        /**
         * Executa a operação usando a conexão fornecida.
         *
         * @param connection conexão com transação ativa
         * @return resultado da operação
         * @throws Exception se ocorrer um erro
         */
        T call(Connection connection) throws Exception;
    }
    
    /**
     * Interface funcional para operações sem retorno que podem ser executadas em uma transação.
     */
    @FunctionalInterface
    public interface TransactionRunnable {
        /**
         * Executa a operação usando a conexão fornecida.
         *
         * @param connection conexão com transação ativa
         * @throws Exception se ocorrer um erro
         */
        void run(Connection connection) throws Exception;
    }
} 