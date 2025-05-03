package org.example.dao;

import org.example.config.TestDatabaseConfig;
import org.example.model.Funcionario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioDAOTest {
    
    private FuncionarioDAO funcionarioDAO;
    private final LocalDate dataAdmissao = LocalDate.of(2023, 1, 15);
    private final BigDecimal salario = new BigDecimal("5000.00");
    
    @BeforeEach
    void setUp() throws SQLException {
        // Limpa o banco de dados H2 antes de cada teste
        limparTabela();
        
        // Cria uma instância do DAO que usa o banco H2 para testes
        funcionarioDAO = new FuncionarioDAOTest.FuncionarioDAOTestImpl();
        
        // Cria a tabela para os testes
        funcionarioDAO.criarTabela();
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
            stmt.execute("DROP TABLE IF EXISTS funcionarios");
        }
    }
    
    @Test
    @DisplayName("Deve criar a tabela funcionarios com sucesso")
    void criarTabela() {
        // Não deve lançar exceção ao criar a tabela
        funcionarioDAO.criarTabela();
    }
    
    @Test
    @DisplayName("Deve inserir um novo funcionário com sucesso")
    void inserir() {
        // Cria um funcionário de teste
        Funcionario funcionario = new Funcionario("João Silva", dataAdmissao, salario, true);
        
        // Insere o funcionário
        Optional<Funcionario> resultado = funcionarioDAO.inserir(funcionario);
        
        // Verifica se foi inserido com sucesso
        assertTrue(resultado.isPresent());
        assertNotNull(resultado.get().getId());
        assertEquals("João Silva", resultado.get().getNome());
        assertEquals(dataAdmissao, resultado.get().getDataAdmissao());
        assertEquals(salario, resultado.get().getSalario());
        assertTrue(resultado.get().isStatus());
    }
    
    @Test
    @DisplayName("Deve atualizar um funcionário existente")
    void atualizar() {
        // Primeiro, insere um funcionário
        Funcionario funcionario = new Funcionario("Maria Santos", dataAdmissao, salario, true);
        Optional<Funcionario> resultadoInsercao = funcionarioDAO.inserir(funcionario);
        assertTrue(resultadoInsercao.isPresent());
        
        // Pega o funcionário inserido e modifica seus dados
        Funcionario funcionarioInserido = resultadoInsercao.get();
        funcionarioInserido.setNome("Maria Silva Santos");
        funcionarioInserido.setSalario(new BigDecimal("6000.00"));
        funcionarioInserido.setStatus(false);
        
        // Atualiza o funcionário
        boolean resultadoAtualizacao = funcionarioDAO.atualizar(funcionarioInserido);
        
        // Verifica se foi atualizado com sucesso
        assertTrue(resultadoAtualizacao);
        
        // Busca o funcionário novamente para verificar as alterações
        Optional<Funcionario> funcionarioAtualizado = funcionarioDAO.buscarPorId(funcionarioInserido.getId());
        assertTrue(funcionarioAtualizado.isPresent());
        assertEquals("Maria Silva Santos", funcionarioAtualizado.get().getNome());
        assertEquals(new BigDecimal("6000.00"), funcionarioAtualizado.get().getSalario());
        assertFalse(funcionarioAtualizado.get().isStatus());
    }
    
    @Test
    @DisplayName("Deve excluir um funcionário pelo ID")
    void excluir() {
        // Primeiro, insere um funcionário
        Funcionario funcionario = new Funcionario("Carlos Oliveira", dataAdmissao, salario, true);
        Optional<Funcionario> resultadoInsercao = funcionarioDAO.inserir(funcionario);
        assertTrue(resultadoInsercao.isPresent());
        
        Long id = resultadoInsercao.get().getId();
        
        // Exclui o funcionário
        boolean resultadoExclusao = funcionarioDAO.excluir(id);
        
        // Verifica se foi excluído com sucesso
        assertTrue(resultadoExclusao);
        
        // Verifica se o funcionário não pode mais ser encontrado
        Optional<Funcionario> funcionarioExcluido = funcionarioDAO.buscarPorId(id);
        assertFalse(funcionarioExcluido.isPresent());
    }
    
    @Test
    @DisplayName("Deve buscar um funcionário pelo ID")
    void buscarPorId() {
        // Primeiro, insere um funcionário
        Funcionario funcionario = new Funcionario("Ana Souza", dataAdmissao, salario, true);
        Optional<Funcionario> resultadoInsercao = funcionarioDAO.inserir(funcionario);
        assertTrue(resultadoInsercao.isPresent());
        
        Long id = resultadoInsercao.get().getId();
        
        // Busca o funcionário pelo ID
        Optional<Funcionario> funcionarioEncontrado = funcionarioDAO.buscarPorId(id);
        
        // Verifica se foi encontrado com sucesso
        assertTrue(funcionarioEncontrado.isPresent());
        assertEquals("Ana Souza", funcionarioEncontrado.get().getNome());
        assertEquals(dataAdmissao, funcionarioEncontrado.get().getDataAdmissao());
        assertEquals(salario, funcionarioEncontrado.get().getSalario());
        assertTrue(funcionarioEncontrado.get().isStatus());
    }
    
    @Test
    @DisplayName("Deve listar todos os funcionários")
    void listarTodos() {
        // Insere vários funcionários
        funcionarioDAO.inserir(new Funcionario("Funcionário 1", dataAdmissao, salario, true));
        funcionarioDAO.inserir(new Funcionario("Funcionário 2", dataAdmissao, salario, true));
        funcionarioDAO.inserir(new Funcionario("Funcionário 3", dataAdmissao, salario, false));
        
        // Lista todos os funcionários
        List<Funcionario> funcionarios = funcionarioDAO.listarTodos();
        
        // Verifica se todos foram listados
        assertEquals(3, funcionarios.size());
        
        // Verifica se contém os funcionários
        boolean temFuncionario1 = funcionarios.stream().anyMatch(f -> "Funcionário 1".equals(f.getNome()));
        boolean temFuncionario2 = funcionarios.stream().anyMatch(f -> "Funcionário 2".equals(f.getNome()));
        boolean temFuncionario3 = funcionarios.stream().anyMatch(f -> "Funcionário 3".equals(f.getNome()));
        
        assertTrue(temFuncionario1);
        assertTrue(temFuncionario2);
        assertTrue(temFuncionario3);
    }
    
    // Uma classe que estende FuncionarioDAO para usar o banco H2 em vez do PostgreSQL
    private static class FuncionarioDAOTestImpl extends FuncionarioDAO {
        @Override
        protected Connection getConnection() throws SQLException {
            return TestDatabaseConfig.getConnection();
        }
        
        @Override
        public void criarTabela() {
            String sql = "CREATE TABLE IF NOT EXISTS funcionarios (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(100) NOT NULL, " +
                    "data_admissao DATE NOT NULL, " +
                    "salario NUMERIC(10,2) NOT NULL, " +
                    "status BOOLEAN NOT NULL)";
            
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public Optional<Funcionario> inserir(Funcionario funcionario) {
            // No H2, não podemos usar RETURNING id como no PostgreSQL
            String sql = "INSERT INTO funcionarios (nome, data_admissao, salario, status) VALUES (?, ?, ?, ?)";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, funcionario.getNome());
                stmt.setDate(2, java.sql.Date.valueOf(funcionario.getDataAdmissao()));
                stmt.setBigDecimal(3, funcionario.getSalario());
                stmt.setBoolean(4, funcionario.isStatus());
                
                int linhasAfetadas = stmt.executeUpdate();
                
                if (linhasAfetadas > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            funcionario.setId(generatedKeys.getLong(1));
                            return Optional.of(funcionario);
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