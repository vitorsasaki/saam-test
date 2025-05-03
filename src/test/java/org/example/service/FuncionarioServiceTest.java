package org.example.service;

import org.example.dao.FuncionarioDAO;
import org.example.model.Funcionario;
import org.example.util.H2DatabaseUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FuncionarioServiceTest {

    @Mock
    private FuncionarioDAO funcionarioDAO;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private Funcionario funcionarioMock;
    private final LocalDate dataAdmissao = LocalDate.of(2023, 1, 15);
    private final BigDecimal salario = new BigDecimal("5000.00");
    
    @BeforeAll
    static void inicializar() {
        // Inicializa o banco de dados de teste
        H2DatabaseUtil.limparBancoDados();
        H2DatabaseUtil.criarTabelaFuncionarios();
    }
    
    @AfterAll
    static void finalizar() {
        // Limpa e fecha o banco de dados de teste
        H2DatabaseUtil.limparBancoDados();
        H2DatabaseUtil.fecharConexoes();
    }

    @BeforeEach
    void setUp() {
        // Configuração comum para todos os testes
        funcionarioMock = new Funcionario("Funcionário Teste", dataAdmissao, salario, true);
        funcionarioMock.setId(1L);
    }

    @Test
    @DisplayName("Deve criar a tabela no banco ao inicializar")
    void inicializacaoDoBancoDeDados() {
        // O teste precisa criar uma nova instância de FuncionarioService manualmente
        // para que o construtor chame o método inicializarBancoDados()
        
        // Cria um novo mock para este teste específico
        FuncionarioDAO daoMock = mock(FuncionarioDAO.class);
        
        // Cria uma nova instância do service com o mock
        FuncionarioService service = new FuncionarioService(daoMock);
        
        // Verifica se o método do DAO foi chamado na inicialização
        verify(daoMock).criarTabela();
    }

    @Test
    @DisplayName("Deve cadastrar um novo funcionário com sucesso")
    void cadastrarFuncionario_ComDadosValidos_DeveRetornarFuncionario() {
        // Arrange
        String nome = "Novo Funcionário";
        LocalDate dataAdmissao = LocalDate.of(2023, 5, 10);
        BigDecimal salario = new BigDecimal("4500.00");
        boolean status = true;
        
        // Configura mock para simular inserção com sucesso
        when(funcionarioDAO.inserir(any(Funcionario.class))).thenReturn(Optional.of(funcionarioMock));

        // Act
        Optional<Funcionario> resultado = funcionarioService.cadastrarFuncionario(nome, dataAdmissao, salario, status);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        
        // Verifica se os métodos do DAO foram chamados corretamente
        verify(funcionarioDAO).inserir(any(Funcionario.class));
    }
    
    @Test
    @DisplayName("Não deve cadastrar funcionário com nome inválido")
    void cadastrarFuncionario_ComNomeInvalido_DeveRetornarEmpty() {
        // Act - Tenta cadastrar com nome nulo
        Optional<Funcionario> resultado1 = funcionarioService.cadastrarFuncionario(null, dataAdmissao, salario, true);
        
        // Assert
        assertFalse(resultado1.isPresent());
        
        // Act - Tenta cadastrar com nome vazio
        Optional<Funcionario> resultado2 = funcionarioService.cadastrarFuncionario("", dataAdmissao, salario, true);
        
        // Assert
        assertFalse(resultado2.isPresent());
        
        // Verifica que o método de inserção não foi chamado
        verify(funcionarioDAO, never()).inserir(any(Funcionario.class));
    }
    
    @Test
    @DisplayName("Não deve cadastrar funcionário com data de admissão nula")
    void cadastrarFuncionario_ComDataAdmissaoNula_DeveRetornarEmpty() {
        // Act
        Optional<Funcionario> resultado = funcionarioService.cadastrarFuncionario("Nome Teste", null, salario, true);
        
        // Assert
        assertFalse(resultado.isPresent());
        
        // Verifica que o método de inserção não foi chamado
        verify(funcionarioDAO, never()).inserir(any(Funcionario.class));
    }
    
    @Test
    @DisplayName("Não deve cadastrar funcionário com salário inválido")
    void cadastrarFuncionario_ComSalarioInvalido_DeveRetornarEmpty() {
        // Act - Salário nulo
        Optional<Funcionario> resultado1 = funcionarioService.cadastrarFuncionario("Nome Teste", dataAdmissao, null, true);
        
        // Assert
        assertFalse(resultado1.isPresent());
        
        // Act - Salário negativo
        Optional<Funcionario> resultado2 = funcionarioService.cadastrarFuncionario("Nome Teste", dataAdmissao, new BigDecimal("-100"), true);
        
        // Assert
        assertFalse(resultado2.isPresent());
        
        // Act - Salário zero
        Optional<Funcionario> resultado3 = funcionarioService.cadastrarFuncionario("Nome Teste", dataAdmissao, BigDecimal.ZERO, true);
        
        // Assert
        assertFalse(resultado3.isPresent());
        
        // Verifica que o método de inserção não foi chamado
        verify(funcionarioDAO, never()).inserir(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve atualizar um funcionário existente")
    void atualizarFuncionario_ComFuncionarioValido_DeveRetornarTrue() {
        // Arrange
        when(funcionarioDAO.atualizar(any(Funcionario.class))).thenReturn(true);

        // Act
        boolean resultado = funcionarioService.atualizarFuncionario(funcionarioMock);

        // Assert
        assertTrue(resultado);
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(funcionarioDAO).atualizar(funcionarioMock);
    }
    
    @Test
    @DisplayName("Não deve atualizar funcionário com dados inválidos")
    void atualizarFuncionario_ComDadosInvalidos_DeveRetornarFalse() {
        // Arrange - Funcionário com nome nulo
        Funcionario funcionarioNomeNulo = new Funcionario(null, dataAdmissao, salario, true);
        funcionarioNomeNulo.setId(1L);
        
        // Act
        boolean resultado1 = funcionarioService.atualizarFuncionario(funcionarioNomeNulo);
        
        // Assert
        assertFalse(resultado1);
        
        // Arrange - Funcionário com data nula
        Funcionario funcionarioDataNula = new Funcionario("Nome Teste", null, salario, true);
        funcionarioDataNula.setId(1L);
        
        // Act
        boolean resultado2 = funcionarioService.atualizarFuncionario(funcionarioDataNula);
        
        // Assert
        assertFalse(resultado2);
        
        // Arrange - Funcionário com salário inválido
        Funcionario funcionarioSalarioInvalido = new Funcionario("Nome Teste", dataAdmissao, new BigDecimal("-100"), true);
        funcionarioSalarioInvalido.setId(1L);
        
        // Act
        boolean resultado3 = funcionarioService.atualizarFuncionario(funcionarioSalarioInvalido);
        
        // Assert
        assertFalse(resultado3);
        
        // Verifica que o método de atualização não foi chamado
        verify(funcionarioDAO, never()).atualizar(any(Funcionario.class));
    }
    
    @Test
    @DisplayName("Não deve atualizar funcionário sem ID")
    void atualizarFuncionario_ComFuncionarioSemId_DeveRetornarFalse() {
        // Arrange
        Funcionario funcionarioSemId = new Funcionario("Nome Teste", dataAdmissao, salario, true);
        // ID será null
        
        // Act
        boolean resultado = funcionarioService.atualizarFuncionario(funcionarioSemId);
        
        // Assert
        assertFalse(resultado);
        
        // Verifica que o método de atualização não foi chamado
        verify(funcionarioDAO, never()).atualizar(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve excluir um funcionário pelo ID")
    void excluirFuncionario_ComIdValido_DeveRetornarTrue() {
        // Arrange
        when(funcionarioDAO.excluir(1L)).thenReturn(true);

        // Act
        boolean resultado = funcionarioService.excluirFuncionario(1L);

        // Assert
        assertTrue(resultado);
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(funcionarioDAO).excluir(1L);
    }
    
    @Test
    @DisplayName("Não deve excluir funcionário com ID nulo")
    void excluirFuncionario_ComIdNulo_DeveRetornarFalse() {
        // Act
        boolean resultado = funcionarioService.excluirFuncionario(null);
        
        // Assert
        assertFalse(resultado);
        
        // Verifica que o método de exclusão não foi chamado
        verify(funcionarioDAO, never()).excluir(anyLong());
    }

    @Test
    @DisplayName("Deve buscar um funcionário pelo ID")
    void buscarPorId_ComIdExistente_DeveRetornarFuncionario() {
        // Arrange
        when(funcionarioDAO.buscarPorId(1L)).thenReturn(Optional.of(funcionarioMock));

        // Act
        Optional<Funcionario> resultado = funcionarioService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(funcionarioDAO).buscarPorId(1L);
    }
    
    @Test
    @DisplayName("Não deve buscar funcionário com ID nulo")
    void buscarPorId_ComIdNulo_DeveRetornarEmpty() {
        // Act
        Optional<Funcionario> resultado = funcionarioService.buscarPorId(null);
        
        // Assert
        assertFalse(resultado.isPresent());
        
        // Verifica que o método de busca não foi chamado
        verify(funcionarioDAO, never()).buscarPorId(anyLong());
    }

    @Test
    @DisplayName("Deve listar todos os funcionários")
    void listarTodos_DeveRetornarListaDeFuncionarios() {
        // Arrange
        List<Funcionario> listaFuncionarios = new ArrayList<>();
        listaFuncionarios.add(funcionarioMock);
        
        Funcionario funcionario2 = new Funcionario("Outro Funcionário", dataAdmissao, salario, false);
        funcionario2.setId(2L);
        listaFuncionarios.add(funcionario2);
        
        when(funcionarioDAO.listarTodos()).thenReturn(listaFuncionarios);

        // Act
        List<Funcionario> resultado = funcionarioService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(funcionarioDAO).listarTodos();
    }
} 