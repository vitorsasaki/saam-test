package org.example.util;

import org.example.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLTransientConnectionException;
import java.sql.SQLTransientException;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * Utilitário para executar operações de banco de dados com mecanismo de retry.
 * Automaticamente tenta novamente operações que falham devido a erros transitórios.
 */
public class DatabaseRetryHandler {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseRetryHandler.class);
    
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_INITIAL_BACKOFF_MS = 1000; // 1 segundo
    private static final double DEFAULT_BACKOFF_MULTIPLIER = 2.0; // Multiplicador para backoff exponencial
    
    /**
     * Executa uma operação com retry automático para exceções transitórias.
     * Usa backoff exponencial entre as tentativas.
     *
     * @param operation operação a ser executada
     * @param description descrição da operação para logs
     * @param <T> tipo de retorno da operação
     * @return resultado da operação
     * @throws DatabaseException se todas as tentativas falharem
     */
    public static <T> T executeWithRetry(Callable<T> operation, String description) throws DatabaseException {
        return executeWithRetry(operation, description, DEFAULT_MAX_RETRIES, 
                                DEFAULT_INITIAL_BACKOFF_MS, DEFAULT_BACKOFF_MULTIPLIER,
                                e -> e instanceof SQLTransientException || e instanceof SQLTransientConnectionException);
    }
    
    /**
     * Executa uma operação com retry automático com parâmetros personalizados.
     *
     * @param operation operação a ser executada
     * @param description descrição da operação para logs
     * @param maxRetries número máximo de tentativas
     * @param initialBackoffMs tempo de espera inicial em milissegundos
     * @param backoffMultiplier multiplicador para cálculo do próximo tempo de espera
     * @param retryableExceptions predicate que determina se uma exceção deve ser tentada novamente
     * @param <T> tipo de retorno da operação
     * @return resultado da operação
     * @throws DatabaseException se todas as tentativas falharem
     */
    public static <T> T executeWithRetry(
            Callable<T> operation,
            String description,
            int maxRetries,
            long initialBackoffMs,
            double backoffMultiplier,
            Predicate<Throwable> retryableExceptions) throws DatabaseException {
        
        int attempt = 0;
        long backoffTime = initialBackoffMs;
        Throwable lastException = null;
        
        while (attempt <= maxRetries) {
            try {
                if (attempt > 0) {
                    logger.info("Tentativa {} de {} para {}", attempt, maxRetries, description);
                }
                
                return operation.call();
                
            } catch (Exception e) {
                lastException = e;
                
                if (retryableExceptions.test(e) && attempt < maxRetries) {
                    logger.warn("Falha transitória na tentativa {} de {} para {}: {}. Tentando novamente em {} ms",
                            attempt + 1, maxRetries, description, e.getMessage(), backoffTime);
                    
                    try {
                        Thread.sleep(backoffTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new DatabaseException("Operação interrompida durante espera para retry", ie);
                    }
                    
                    // Aumenta o tempo de espera para a próxima tentativa (backoff exponencial)
                    backoffTime = (long) (backoffTime * backoffMultiplier);
                    
                } else {
                    // Se não é uma exceção que deve ser tentada novamente ou já esgotamos as tentativas
                    logger.error("Falha definitiva na tentativa {} de {} para {}: {}",
                            attempt + 1, maxRetries, description, e.getMessage(), e);
                    
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    } else {
                        throw new DatabaseException("Falha ao executar " + description, e);
                    }
                }
            }
            
            attempt++;
        }
        
        // Se chegamos aqui, significa que esgotamos as tentativas
        throw new DatabaseException("Falha ao executar " + description + " após " + maxRetries + " tentativas", lastException);
    }
    
} 