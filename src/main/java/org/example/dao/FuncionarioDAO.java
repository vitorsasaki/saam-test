package org.example.dao;

import org.example.config.DatabaseConfig;
import org.example.exception.DatabaseException;
import org.example.model.Funcionario;
import org.example.util.DatabaseExceptionHandler;
import org.example.util.DatabaseRetryHandler;
import org.example.util.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FuncionarioDAO {
    private static final Logger logger = LoggerFactory.getLogger(FuncionarioDAO.class);

    private static final String SQL_INSERT = 
            "INSERT INTO funcionarios (nome, data_admissao, salario, status) VALUES (?, ?, ?, ?) RETURNING id";
    private static final String SQL_UPDATE = 
            "UPDATE funcionarios SET nome = ?, data_admissao = ?, salario = ?, status = ? WHERE id = ?";
    private static final String SQL_DELETE = 
            "DELETE FROM funcionarios WHERE id = ?";
    private static final String SQL_FIND_BY_ID = 
            "SELECT * FROM funcionarios WHERE id = ?";
    private static final String SQL_FIND_ALL = 
            "SELECT * FROM funcionarios ORDER BY nome";

    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS funcionarios (" +
                "id SERIAL PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "data_admissao DATE NOT NULL, " +
                "salario NUMERIC(10,2) NOT NULL, " +
                "status BOOLEAN NOT NULL)";
        
        try {
            DatabaseRetryHandler.executeWithRetry(() -> {
                try (Connection conn = getConnection();
                     Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    logger.info("Tabela 'funcionarios' verificada/criada com sucesso");
                    return true;
                } catch (SQLException e) {
                    throw DatabaseExceptionHandler.handleSQLException(e, "criar tabela 'funcionarios'");
                }
            }, "criar tabela funcionarios");
        } catch (DatabaseException e) {
            logger.error("Falha definitiva ao criar tabela 'funcionarios'", e);
        }
    }

    public Optional<Funcionario> inserir(Funcionario funcionario) {
        try {
            return DatabaseRetryHandler.executeWithRetry(() -> {
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
                    
                    stmt.setString(1, funcionario.getNome());
                    stmt.setDate(2, java.sql.Date.valueOf(funcionario.getDataAdmissao()));
                    stmt.setBigDecimal(3, funcionario.getSalario());
                    stmt.setBoolean(4, funcionario.isStatus());
                    
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        funcionario.setId(rs.getLong("id"));
                        logger.info("Funcionário inserido com sucesso: ID = {}", funcionario.getId());
                        return Optional.of(funcionario);
                    }
                    
                    return Optional.empty();
                } catch (SQLException e) {
                    throw DatabaseExceptionHandler.handleSQLException(e, "inserir funcionário");
                }
            }, "inserir funcionário");
        } catch (DatabaseException.IntegrityConstraintViolationException e) {
            logger.warn("Violação de restrição ao inserir funcionário: {}", e.getMessage());
            throw e;
        } catch (DatabaseException e) {
            logger.error("Erro ao inserir funcionário", e);
            return Optional.empty();
        }
    }

    public boolean atualizar(Funcionario funcionario) {
        try {
            return DatabaseRetryHandler.executeWithRetry(() -> {
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
                    
                    stmt.setString(1, funcionario.getNome());
                    stmt.setDate(2, java.sql.Date.valueOf(funcionario.getDataAdmissao()));
                    stmt.setBigDecimal(3, funcionario.getSalario());
                    stmt.setBoolean(4, funcionario.isStatus());
                    stmt.setLong(5, funcionario.getId());
                    
                    int linhasAfetadas = stmt.executeUpdate();
                    logger.info("Funcionário atualizado: {} linha(s) afetada(s)", linhasAfetadas);
                    return linhasAfetadas > 0;
                } catch (SQLException e) {
                    throw DatabaseExceptionHandler.handleSQLException(e, "atualizar funcionário");
                }
            }, "atualizar funcionário");
        } catch (DatabaseException.IntegrityConstraintViolationException e) {
            logger.warn("Violação de restrição ao atualizar funcionário: {}", e.getMessage());
            throw e;
        } catch (DatabaseException e) {
            logger.error("Erro ao atualizar funcionário", e);
            return false;
        }
    }

    public boolean excluir(Long id) {
        try {
            return DatabaseRetryHandler.executeWithRetry(() -> {
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
                    
                    stmt.setLong(1, id);
                    
                    int linhasAfetadas = stmt.executeUpdate();
                    logger.info("Funcionário excluído: {} linha(s) afetada(s)", linhasAfetadas);
                    return linhasAfetadas > 0;
                } catch (SQLException e) {
                    throw DatabaseExceptionHandler.handleSQLException(e, "excluir funcionário");
                }
            }, "excluir funcionário");
        } catch (DatabaseException.IntegrityConstraintViolationException e) {
            logger.warn("Violação de restrição ao excluir funcionário: {}", e.getMessage());
            throw e;
        } catch (DatabaseException e) {
            logger.error("Erro ao excluir funcionário", e);
            return false;
        }
    }

    public Optional<Funcionario> buscarPorId(Long id) {
        try {
            return DatabaseRetryHandler.executeWithRetry(() -> {
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
                    
                    stmt.setLong(1, id);
                    
                    ResultSet rs = stmt.executeQuery();
                    
                    if (rs.next()) {
                        return Optional.of(montarFuncionarioDoResultSet(rs));
                    }
                    
                    return Optional.empty();
                } catch (SQLException e) {
                    throw DatabaseExceptionHandler.handleSQLException(e, "buscar funcionário por ID");
                }
            }, "buscar funcionário por ID " + id);
        } catch (DatabaseException e) {
            logger.error("Erro ao buscar funcionário por ID: {}", id, e);
            return Optional.empty();
        }
    }

    public List<Funcionario> listarTodos() {
        try {
            return DatabaseRetryHandler.executeWithRetry(() -> {
                List<Funcionario> funcionarios = new ArrayList<>();
                
                try (Connection conn = getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(SQL_FIND_ALL)) {
                    
                    while (rs.next()) {
                        Funcionario funcionario = montarFuncionarioDoResultSet(rs);
                        funcionarios.add(funcionario);
                    }
                    
                    logger.info("Busca de funcionários realizada: {} registros encontrados", funcionarios.size());
                    return funcionarios;
                } catch (SQLException e) {
                    throw DatabaseExceptionHandler.handleSQLException(e, "listar funcionários");
                }
            }, "listar todos os funcionários");
        } catch (DatabaseException e) {
            logger.error("Erro ao listar funcionários", e);
            return new ArrayList<>();
        }
    }


    private Funcionario montarFuncionarioDoResultSet(ResultSet rs) throws SQLException {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(rs.getLong("id"));
        funcionario.setNome(rs.getString("nome"));
        
        Date dataSql = rs.getDate("data_admissao");
        if (dataSql != null) {
            funcionario.setDataAdmissao(dataSql.toLocalDate());
        }
        
        funcionario.setSalario(rs.getBigDecimal("salario"));
        funcionario.setStatus(rs.getBoolean("status"));
        
        return funcionario;
    }
} 