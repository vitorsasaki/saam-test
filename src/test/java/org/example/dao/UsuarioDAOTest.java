package org.example.dao;

import org.example.config.TestDatabaseConfig;
import org.example.model.Usuario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioDAOTest {
    
    private UsuarioDAO usuarioDAO;
    
    @BeforeEach
    void setUp() throws SQLException {
        // Limpa o banco de dados H2 antes de cada teste
        limparTabela();
        
        // Cria uma instância do DAO que usa o banco H2 para testes
        usuarioDAO = new UsuarioDAOTest.UsuarioDAOTestImpl();
        
        // Cria a tabela para os testes
        usuarioDAO.criarTabela();
    }
    
    @AfterAll
    static void tearDown() {
        // Encerra o pool de conexões após todos os testes
        TestDatabaseConfig.shutdown();
    }
    
    private void limparTabela() throws SQLException {
        try (Connection conn = TestDatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            // Apaga a tabela se ela já existir
            stmt.execute("DROP TABLE IF EXISTS usuarios");
        }
    }
    
    @Test
    @DisplayName("Deve criar a tabela usuarios com sucesso")
    void criarTabela() {
        // Não deve lançar exceção ao criar a tabela
        usuarioDAO.criarTabela();
    }
    
    @Test
    @DisplayName("Deve inserir um novo usuário com sucesso")
    void inserir() {
        // Cria um usuário de teste
        Usuario usuario = new Usuario("João Silva", "joao@email.com", "senha123");
        
        // Insere o usuário
        Optional<Usuario> resultado = usuarioDAO.inserir(usuario);
        
        // Verifica se foi inserido com sucesso
        assertTrue(resultado.isPresent());
        assertNotNull(resultado.get().getId());
        assertEquals("João Silva", resultado.get().getNome());
        assertEquals("joao@email.com", resultado.get().getEmail());
        assertEquals("senha123", resultado.get().getSenha());
    }
    
    @Test
    @DisplayName("Deve atualizar um usuário existente")
    void atualizar() {
        // Primeiro, insere um usuário
        Usuario usuario = new Usuario("Maria Santos", "maria@email.com", "senha456");
        Optional<Usuario> resultadoInsercao = usuarioDAO.inserir(usuario);
        assertTrue(resultadoInsercao.isPresent());
        
        // Pega o usuário inserido e modifica seus dados
        Usuario usuarioInserido = resultadoInsercao.get();
        usuarioInserido.setNome("Maria Silva Santos");
        usuarioInserido.setEmail("maria.silva@email.com");
        
        // Atualiza o usuário
        boolean resultadoAtualizacao = usuarioDAO.atualizar(usuarioInserido);
        
        // Verifica se foi atualizado com sucesso
        assertTrue(resultadoAtualizacao);
        
        // Busca o usuário novamente para verificar as alterações
        Optional<Usuario> usuarioAtualizado = usuarioDAO.buscarPorId(usuarioInserido.getId());
        assertTrue(usuarioAtualizado.isPresent());
        assertEquals("Maria Silva Santos", usuarioAtualizado.get().getNome());
        assertEquals("maria.silva@email.com", usuarioAtualizado.get().getEmail());
    }
    
    @Test
    @DisplayName("Deve excluir um usuário pelo ID")
    void excluir() {
        // Primeiro, insere um usuário
        Usuario usuario = new Usuario("Carlos Oliveira", "carlos@email.com", "senha789");
        Optional<Usuario> resultadoInsercao = usuarioDAO.inserir(usuario);
        assertTrue(resultadoInsercao.isPresent());
        
        Long id = resultadoInsercao.get().getId();
        
        // Exclui o usuário
        boolean resultadoExclusao = usuarioDAO.excluir(id);
        
        // Verifica se foi excluído com sucesso
        assertTrue(resultadoExclusao);
        
        // Verifica se o usuário não pode mais ser encontrado
        Optional<Usuario> usuarioExcluido = usuarioDAO.buscarPorId(id);
        assertFalse(usuarioExcluido.isPresent());
    }
    
    @Test
    @DisplayName("Deve buscar um usuário pelo ID")
    void buscarPorId() {
        // Primeiro, insere um usuário
        Usuario usuario = new Usuario("Ana Souza", "ana@email.com", "senha101112");
        Optional<Usuario> resultadoInsercao = usuarioDAO.inserir(usuario);
        assertTrue(resultadoInsercao.isPresent());
        
        Long id = resultadoInsercao.get().getId();
        
        // Busca o usuário pelo ID
        Optional<Usuario> usuarioEncontrado = usuarioDAO.buscarPorId(id);
        
        // Verifica se foi encontrado com sucesso
        assertTrue(usuarioEncontrado.isPresent());
        assertEquals("Ana Souza", usuarioEncontrado.get().getNome());
        assertEquals("ana@email.com", usuarioEncontrado.get().getEmail());
    }
    
    @Test
    @DisplayName("Deve buscar um usuário pelo email")
    void buscarPorEmail() {
        // Primeiro, insere um usuário
        Usuario usuario = new Usuario("Pedro Costa", "pedro@email.com", "senha131415");
        usuarioDAO.inserir(usuario);
        
        // Busca o usuário pelo email
        Optional<Usuario> usuarioEncontrado = usuarioDAO.buscarPorEmail("pedro@email.com");
        
        // Verifica se foi encontrado com sucesso
        assertTrue(usuarioEncontrado.isPresent());
        assertEquals("Pedro Costa", usuarioEncontrado.get().getNome());
        assertEquals("pedro@email.com", usuarioEncontrado.get().getEmail());
    }
    
    @Test
    @DisplayName("Deve retornar vazio ao buscar usuário com email inexistente")
    void buscarPorEmail_ComEmailInexistente() {
        // Busca um usuário com email que não existe
        Optional<Usuario> usuarioEncontrado = usuarioDAO.buscarPorEmail("naoexiste@email.com");
        
        // Verifica que não foi encontrado
        assertFalse(usuarioEncontrado.isPresent());
    }
    
    @Test
    @DisplayName("Deve listar todos os usuários")
    void listarTodos() {
        // Insere vários usuários
        usuarioDAO.inserir(new Usuario("Usuário 1", "usuario1@email.com", "senha1"));
        usuarioDAO.inserir(new Usuario("Usuário 2", "usuario2@email.com", "senha2"));
        usuarioDAO.inserir(new Usuario("Usuário 3", "usuario3@email.com", "senha3"));
        
        // Lista todos os usuários
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        
        // Verifica se todos foram listados
        assertEquals(3, usuarios.size());
        
        // Verifica se contém os três emails
        boolean temUsuario1 = usuarios.stream().anyMatch(u -> "usuario1@email.com".equals(u.getEmail()));
        boolean temUsuario2 = usuarios.stream().anyMatch(u -> "usuario2@email.com".equals(u.getEmail()));
        boolean temUsuario3 = usuarios.stream().anyMatch(u -> "usuario3@email.com".equals(u.getEmail()));
        
        assertTrue(temUsuario1);
        assertTrue(temUsuario2);
        assertTrue(temUsuario3);
    }
    
    // Uma classe que estende UsuarioDAO para usar o banco H2 em vez do PostgreSQL
    private static class UsuarioDAOTestImpl extends UsuarioDAO {
        @Override
        protected Connection getConnection() throws SQLException {
            return TestDatabaseConfig.getConnection();
        }
        
        @Override
        public void criarTabela() {
            String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "senha VARCHAR(100) NOT NULL)";
            
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public Optional<Usuario> inserir(Usuario usuario) {
            // No H2, não podemos usar RETURNING id como no PostgreSQL
            String sql = "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, usuario.getNome());
                stmt.setString(2, usuario.getEmail());
                stmt.setString(3, usuario.getSenha());
                
                int linhasAfetadas = stmt.executeUpdate();
                
                if (linhasAfetadas > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            usuario.setId(generatedKeys.getLong(1));
                            return Optional.of(usuario);
                        }
                    }
                }
                
                return Optional.empty();
            } catch (SQLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
    }
} 