package org.example.dao;

import org.example.config.DatabaseConfig;
import org.example.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioDAO.class);

    private static final String SQL_INSERT = 
            "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?) RETURNING id";
    private static final String SQL_UPDATE = 
            "UPDATE usuarios SET nome = ?, email = ?, senha = ? WHERE id = ?";
    private static final String SQL_DELETE = 
            "DELETE FROM usuarios WHERE id = ?";
    private static final String SQL_FIND_BY_ID = 
            "SELECT * FROM usuarios WHERE id = ?";
    private static final String SQL_FIND_BY_EMAIL = 
            "SELECT * FROM usuarios WHERE email = ?";
    private static final String SQL_FIND_ALL = 
            "SELECT * FROM usuarios ORDER BY nome";

    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id SERIAL PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) NOT NULL UNIQUE, " +
                "senha VARCHAR(100) NOT NULL)";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Tabela 'usuarios' verificada/criada com sucesso");
        } catch (SQLException e) {
            logger.error("Erro ao criar tabela 'usuarios'", e);
        }
    }

    public Optional<Usuario> inserir(Usuario usuario) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usuario.setId(rs.getLong("id"));
                logger.info("Usuário inserido com sucesso: ID = {}", usuario.getId());
                return Optional.of(usuario);
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Erro ao inserir usuário", e);
            return Optional.empty();
        }
    }

    public boolean atualizar(Usuario usuario) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setLong(4, usuario.getId());
            
            int linhasAfetadas = stmt.executeUpdate();
            logger.info("Usuário atualizado: {} linha(s) afetada(s)", linhasAfetadas);
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            logger.error("Erro ao atualizar usuário", e);
            return false;
        }
    }

    public boolean excluir(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            
            stmt.setLong(1, id);
            
            int linhasAfetadas = stmt.executeUpdate();
            logger.info("Usuário excluído: {} linha(s) afetada(s)", linhasAfetadas);
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            logger.error("Erro ao excluir usuário", e);
            return false;
        }
    }

    public Optional<Usuario> buscarPorId(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            
            stmt.setLong(1, id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(montarUsuarioDoResultSet(rs));
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Erro ao buscar usuário por ID", e);
            return Optional.empty();
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {
            
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(montarUsuarioDoResultSet(rs));
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Erro ao buscar usuário por email", e);
            return Optional.empty();
        }
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_FIND_ALL)) {
            
            while (rs.next()) {
                Usuario usuario = montarUsuarioDoResultSet(rs);
                usuarios.add(usuario);
            }
            
            logger.info("Busca de usuários realizada: {} registros encontrados", usuarios.size());
        } catch (SQLException e) {
            logger.error("Erro ao listar usuários", e);
        }
        
        return usuarios;
    }

    private Usuario montarUsuarioDoResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        
        return usuario;
    }
} 